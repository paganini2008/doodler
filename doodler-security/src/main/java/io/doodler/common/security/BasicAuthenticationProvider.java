package io.doodler.common.security;

import static io.doodler.common.security.SecurityConstants.NA;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * @Description: BasicAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 25/11/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class BasicAuthenticationProvider implements AuthenticationProvider {

    private final BasicUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadBasicUserByUsername(authentication.getName());
        if (!userDetails.getPassword().equals(authentication.getCredentials())) {
            throw new UsernameNotFoundException("For user: " + authentication.getName());
        }
        return new BasicCredentialsAuthenticationToken(userDetails, NA, ((PlatformUserDetails) userDetails).getPlatform(),
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}