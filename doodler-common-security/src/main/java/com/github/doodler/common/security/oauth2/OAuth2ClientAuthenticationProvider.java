package com.github.doodler.common.security.oauth2;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import com.github.doodler.common.security.PlatformUserDetailsService;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: OAuth2ClientAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 18/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class OAuth2ClientAuthenticationProvider implements AuthenticationProvider {

    private final PlatformUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2UserInfoAware userInfoAware =
                (OAuth2UserInfoAware) authenticationToken.getPrincipal();
        OAuth2UserInfo oauth2UserInfo = userInfoAware.getOAuth2UserInfo();
        UserDetails userDetails = userDetailsService.loadUserByIdentityAndRegistrationId(
                oauth2UserInfo.getIdentity(), oauth2UserInfo.getRegistrationId());
        return new OAuth2ClientAuthenticationToken(userDetails, null,
                OAuth2ClientType.getBy(oauth2UserInfo.getRegistrationId()),
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
    }

}
