package com.github.doodler.common.security.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.doodler.common.security.PlatformUserDetailsService;

/**
 * @Description: TwitchAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 10/03/2021
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TwitchAuthenticationProvider implements AuthenticationProvider {

    private final TwitchClientApiService twitchClientApiService;
    private final PlatformUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TwitchAuthenticationToken twitchToken = (TwitchAuthenticationToken) authentication;
        final String channel = twitchToken.getChannel();
        String identifier = (String) twitchToken.getPrincipal();
        String accessToken = (String) twitchToken.getCredentials();
        if (log.isTraceEnabled()) {
            log.trace("Authenticate identifier '{}' and accessToken '{}'", identifier, accessToken);
        }
        TwitchProfile userInfo = null;
        try {
            userInfo = twitchClientApiService.getUserInfo(accessToken);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        if (userInfo == null) {
            throw new UsernameNotFoundException("Mismatched Twitch user: " + identifier + ", token: " + accessToken);
        }
        UserDetails user = userDetailsService.loadUserBySocialAccount(identifier, channel);
        return new TwitchAuthenticationToken(user, accessToken, channel, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TwitchAuthenticationToken.class.isAssignableFrom(authentication);
    }
}