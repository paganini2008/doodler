package com.github.doodler.common.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @Description: PlatformTokenBasedRememberMeServices
 * @Author: Fred Feng
 * @Date: 21/12/2022
 * @Version 1.0.0
 * @see TokenBasedRememberMeServices
 */
public class PlatformTokenBasedRememberMeServices extends AbstractRememberMeServices {

    public PlatformTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
            HttpServletResponse response) {
        if (cookieTokens.length != 4) {
            throw new InvalidCookieException("Cookie token did not contain 4"
                    + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
        long tokenExpiryTime = getTokenExpiryTime(cookieTokens);
        if (isTokenExpired(tokenExpiryTime)) {
            throw new InvalidCookieException("Cookie token[1] has expired (expired on '"
                    + new Date(tokenExpiryTime) + "'; current time is '" + new Date() + "')");
        }
        // Check the user exists. Defer lookup until after expiry time checked, to
        // possibly avoid expensive database call.
        String platform = cookieTokens[3];
        UserDetails userDetails = ((PlatformUserDetailsService) getUserDetailsService())
                .loadUserByIdentityAndPlatform(cookieTokens[0], platform);
        Assert.notNull(userDetails,
                () -> "UserDetailsService " + getUserDetailsService()
                        + " returned null for username " + cookieTokens[0] + ". "
                        + "This is an interface contract violation");
        // Check signature of token matches remaining details. Must do this after user
        // lookup, as we need the DAO-derived password. If efficiency was a major issue,
        // just add in a UserCache implementation, but recall that this method is usually
        // only called once per HttpSession - if the token is valid, it will cause
        // SecurityContextHolder population, whilst if invalid, will cause the cookie to
        // be cancelled.
        String expectedTokenSignature =
                makeTokenSignature(platform, tokenExpiryTime, userDetails.getUsername());
        if (!equals(expectedTokenSignature, cookieTokens[2])) {
            throw new InvalidCookieException("Cookie token[2] contained signature '"
                    + cookieTokens[2] + "' but expected '" + expectedTokenSignature + "'");
        }
        return userDetails;
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        String parameterValue = request.getHeader(parameter);
        if (!StringUtils.hasText(parameterValue)) {
            parameterValue = (String) request.getAttribute(parameter);
        }
        if (StringUtils.hasText(parameterValue)) {
            if (parameterValue.equalsIgnoreCase("true") || parameterValue.equalsIgnoreCase("on")
                    || parameterValue.equalsIgnoreCase("yes") || parameterValue.equals("1")) {
                return true;
            }
        }
        this.logger.debug(LogMessage.format(
                "Did not send remember-me cookie (principal did not set parameter '%s')",
                parameter));
        return false;
    }

    private long getTokenExpiryTime(String[] cookieTokens) {
        try {
            return new Long(cookieTokens[1]);
        } catch (NumberFormatException nfe) {
            throw new InvalidCookieException(
                    "Cookie token[1] did not contain a valid number (contained '" + cookieTokens[1]
                            + "')");
        }
    }

    /**
     * Calculates the digital signature to be put in the cookie. Default value is MD5
     * ("username:tokenExpiryTime:password:key")
     */
    protected String makeTokenSignature(String platform, long tokenExpiryTime, String username) {
        String data = platform + ":" + username + ":" + tokenExpiryTime + ":" + getKey();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return new String(Hex.encode(digest.digest(data.getBytes())));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
    }

    protected boolean isTokenExpired(long tokenExpiryTime) {
        return tokenExpiryTime < System.currentTimeMillis();
    }

    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {
        String username = retrieveUserName(successfulAuthentication);
        String password = retrievePassword(successfulAuthentication);
        // If unable to find a username and password, just abort as
        // TokenBasedRememberMeServices is
        // unable to construct a valid token in this case.
        if (!StringUtils.hasLength(username)) {
            this.logger.debug("Unable to retrieve username");
            return;
        }
        if (!StringUtils.hasLength(password)) {
            UserDetails user = getUserDetailsService().loadUserByUsername(username);
            password = user.getPassword();
            if (!StringUtils.hasLength(password)) {
                this.logger.debug("Unable to obtain password for user: " + username);
                return;
            }
        }
        String platform = retrievePlatform(successfulAuthentication);
        int tokenLifetime = calculateLoginLifetime(request, successfulAuthentication);
        long expiryTime = System.currentTimeMillis();
        // SEC-949
        expiryTime += 1000L * ((tokenLifetime < 0) ? TWO_WEEKS_S : tokenLifetime);
        String signatureValue = makeTokenSignature(platform, expiryTime, username);
        setCookie(new String[] {username, Long.toString(expiryTime), signatureValue, platform},
                tokenLifetime, request, response);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Added remember-me cookie for user '" + username + "', expiry: '"
                    + new Date(expiryTime) + "'");
        }
    }

    /**
     * Calculates the validity period in seconds for a newly generated remember-me login. After this
     * period (from the current time) the remember-me login will be considered expired. This method
     * allows customization based on request parameters supplied with the login or information in
     * the <tt>Authentication</tt> object. The default value is just the token validity period
     * property, <tt>tokenValiditySeconds</tt>.
     * <p>
     * The returned value will be used to work out the expiry time of the token and will also be
     * used to set the <tt>maxAge</tt> property of the cookie.
     * <p>
     * See SEC-485.
     *
     * @param request the request passed to onLoginSuccess
     * @param authentication the successful authentication object.
     * @return the lifetime in seconds.
     */
    protected int calculateLoginLifetime(HttpServletRequest request,
            Authentication authentication) {
        return getTokenValiditySeconds();
    }

    protected String retrieveUserName(Authentication authentication) {
        if (isInstanceOfUserDetails(authentication)) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return authentication.getPrincipal().toString();
    }

    protected String retrievePassword(Authentication authentication) {
        if (isInstanceOfUserDetails(authentication)) {
            return ((UserDetails) authentication.getPrincipal()).getPassword();
        }
        if (authentication.getCredentials() != null) {
            return authentication.getCredentials().toString();
        }
        return null;
    }

    protected String retrievePlatform(Authentication authentication) {
        if (authentication.getPrincipal() instanceof RegularUser) {
            return ((RegularUser) authentication.getPrincipal()).getPlatform();
        }
        return "unknown";
    }

    private boolean isInstanceOfUserDetails(Authentication authentication) {
        return authentication.getPrincipal() instanceof UserDetails;
    }

    public void cleanCookies(HttpServletRequest request, HttpServletResponse response) {
        cancelCookie(request, response);
    }

    /**
     * Constant time comparison to prevent against timing attacks.
     */
    private static boolean equals(String expected, String actual) {
        byte[] expectedBytes = bytesUtf8(expected);
        byte[] actualBytes = bytesUtf8(actual);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static byte[] bytesUtf8(String s) {
        return (s != null) ? Utf8.encode(s) : null;
    }
}
