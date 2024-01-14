package com.github.doodler.common.security.facebook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.doodler.common.security.PlatformUserDetailsService;

/**
 * @Description: FacebookAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 02/10/2020
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class FacebookAuthenticationProvider implements AuthenticationProvider {

    private final FacebookClientApiService facebookClientApiService;
    private final PlatformUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        FacebookAuthenticationToken facebookToken = (FacebookAuthenticationToken) authentication;
        final String channel = facebookToken.getChannel();
        String identifier = (String) facebookToken.getPrincipal();
        String accessToken = (String) facebookToken.getCredentials();
        if (log.isTraceEnabled()) {
            log.trace("Authenticate identifier '{}' and accessToken '{}'", identifier, accessToken);
        }
        FacebookProfile userInfo = null;
        try {
            userInfo = facebookClientApiService.getUserInfo(accessToken);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if (userInfo == null) {
            throw new UsernameNotFoundException("Mismatched facebook user: " + identifier + ", token: " + accessToken);
        }
        UserDetails user = userDetailsService.loadUserBySocialAccount(identifier, channel);
        return new FacebookAuthenticationToken(user, accessToken, channel, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FacebookAuthenticationToken.class.isAssignableFrom(authentication);
    }
}