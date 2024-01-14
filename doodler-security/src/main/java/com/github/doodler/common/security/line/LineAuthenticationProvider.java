package com.github.doodler.common.security.line;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.doodler.common.security.PlatformUserDetailsService;

/**
 * @Description: LineAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 10/03/2021
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LineAuthenticationProvider implements AuthenticationProvider {

    private final LineClientApiService lineClientApiService;
    private final PlatformUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LineAuthenticationToken lineToken = (LineAuthenticationToken) authentication;
        final String channel = lineToken.getChannel();
        String identifier = (String) lineToken.getPrincipal();
        String accessToken = (String) lineToken.getCredentials();
        if (log.isTraceEnabled()) {
            log.trace("Authenticate identifier '{}' and accessToken '{}'", identifier, accessToken);
        }
        LineProfile userInfo = null;
        try {
            userInfo = lineClientApiService.getUserInfo(accessToken);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if (userInfo == null) {
            throw new UsernameNotFoundException("Mismatched Line user: " + identifier + ", token: " + accessToken);
        }
        UserDetails user = userDetailsService.loadUserBySocialAccount(identifier, channel);
        return new LineAuthenticationToken(user, accessToken, channel, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LineAuthenticationToken.class.isAssignableFrom(authentication);
    }
}