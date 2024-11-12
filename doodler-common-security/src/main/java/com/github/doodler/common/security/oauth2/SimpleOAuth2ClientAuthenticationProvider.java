package com.github.doodler.common.security.oauth2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.github.doodler.common.security.PlatformUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: SimpleOAuth2ClientAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SimpleOAuth2ClientAuthenticationProvider implements AuthenticationProvider {


    private final ClientRegistrationRepository clientRegistrationRepository;

    private final DefaultOAuth2UserService oAuth2UserService;

    private final PlatformUserDetailsService userDetailsService;

    private final OAuth2UserInfoService oauth2UserInfoService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        SimpleOAuth2ClientAuthenticationToken authenticationToken =
                (SimpleOAuth2ClientAuthenticationToken) authentication;
        ClientRegistration clientRegistration =
                clientRegistrationRepository.findByRegistrationId(authenticationToken.getSource());
        OAuth2UserRequest oAuth2UserRequest =
                new OAuth2UserRequest(clientRegistration, new OAuth2AccessToken(TokenType.BEARER,
                        authenticationToken.getAccessToken(), null, null));
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);
        return processOAuth2User(oAuth2UserRequest, oAuth2User);
    }

    protected Authentication processOAuth2User(OAuth2UserRequest oAuth2UserRequest,
            OAuth2User oAuth2User) {
        OAuth2ClientType oAuth2ClientType = OAuth2ClientType
                .getBy(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        OAuth2UserInfo oAuth2UserInfo = oAuth2ClientType.getUserInfo(oAuth2User);
        if (StringUtils.isBlank(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException(
                    "Email not found from OAuth2 provider");
        }
        OAuth2ClientUser userDetails =
                (OAuth2ClientUser) userDetailsService.loadUserByIdentityAndPlatform(
                        oAuth2UserInfo.getIdentity(), oAuth2UserInfo.getRegistrationId());
        if (userDetails != null) {
            oauth2UserInfoService.updateExistingUser(oAuth2UserInfo, userDetails,
                    oAuth2UserRequest);
        } else {
            oauth2UserInfoService.registerNewUser(oAuth2UserInfo, oAuth2UserRequest);
            userDetails = (OAuth2ClientUser) userDetailsService.loadUserByIdentityAndPlatform(
                    oAuth2UserInfo.getIdentity(), oAuth2UserInfo.getRegistrationId());
        }
        return new OAuth2ClientAuthenticationToken(userDetails, null, oAuth2ClientType,
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SimpleOAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
