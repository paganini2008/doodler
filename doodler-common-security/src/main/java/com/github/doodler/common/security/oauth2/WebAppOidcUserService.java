package com.github.doodler.common.security.oauth2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import com.github.doodler.common.security.PlatformUserDetailsService;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: WebAppOidcUserService
 * @Author: Fred Feng
 * @Date: 21/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class WebAppOidcUserService extends OidcUserService {

    private final PlatformUserDetailsService userDetailsService;
    private final OAuth2UserInfoService auth2UserInfoService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    protected OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        OAuth2UserInfo oAuth2UserInfo =
                OAuth2ClientType.getBy(userRequest.getClientRegistration().getRegistrationId())
                        .getUserInfo(oidcUser);
        if (StringUtils.isBlank(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException(
                    "Email not found from OAuth2 provider");
        }
        OAuth2ClientUser userDetails =
                (OAuth2ClientUser) userDetailsService.loadUserByIdentityAndRegistrationId(
                        oAuth2UserInfo.getIdentity(), oAuth2UserInfo.getRegistrationId());
        if (userDetails != null) {
            auth2UserInfoService.updateExistingUser(oAuth2UserInfo, userDetails, userRequest);
        } else {
            auth2UserInfoService.registerNewUser(oAuth2UserInfo, userRequest);
            userDetails = (OAuth2ClientUser) userDetailsService.loadUserByIdentityAndRegistrationId(
                    oAuth2UserInfo.getIdentity(), oAuth2UserInfo.getRegistrationId());
        }
        return new OidcUserDetails(oAuth2UserInfo, userDetails);
    }

}
