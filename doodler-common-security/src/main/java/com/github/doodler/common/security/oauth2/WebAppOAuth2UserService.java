package com.github.doodler.common.security.oauth2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.github.doodler.common.security.PlatformUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @Description: WebAppOAuth2UserService
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class WebAppOAuth2UserService extends DefaultOAuth2UserService {

    private final PlatformUserDetailsService userDetailsService;
    private final OAuth2UserInfoService oauth2UserInfoService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    protected OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest,
            OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2ClientType
                .getBy(oAuth2UserRequest.getClientRegistration().getRegistrationId())
                .getUserInfo(oAuth2User);
        if (StringUtils.isBlank(oAuth2UserInfo.getEmail())) {
            if (log.isWarnEnabled()) {
                log.warn("Email is not found in OAuth2 User Info");
            }
        }
        OAuth2ClientUser userDetails =
                (OAuth2ClientUser) userDetailsService.loadUserByIdentityAndRegistrationId(
                        oAuth2UserInfo.getIdentity(), oAuth2UserInfo.getRegistrationId());
        if (userDetails != null) {
            oauth2UserInfoService.updateExistingUser(oAuth2UserInfo, userDetails,
                    oAuth2UserRequest);
        } else {
            oauth2UserInfoService.registerNewUser(oAuth2UserInfo, oAuth2UserRequest);
            userDetails = (OAuth2ClientUser) userDetailsService.loadUserByIdentityAndRegistrationId(
                    oAuth2UserInfo.getIdentity(), oAuth2UserInfo.getRegistrationId());
        }
        return new OAuth2UserDetails(oAuth2UserInfo, userDetails);
    }

}
