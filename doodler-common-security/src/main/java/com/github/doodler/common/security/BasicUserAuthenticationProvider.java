package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.NA;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * @Description: BasicUserAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 25/11/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class BasicUserAuthenticationProvider implements AuthenticationProvider {

    private final BasicUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        UserDetails userDetails =
                userDetailsService.loadBasicUserByUsername(authentication.getName());
        if (!userDetails.getPassword().equals(authentication.getCredentials())) {
            throw new UsernameNotFoundException("For user: " + authentication.getName());
        }
        return new BasicUserAuthenticationToken(userDetails, NA,
                ((PlatformUserDetails) userDetails).getPlatform(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BasicUserAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
