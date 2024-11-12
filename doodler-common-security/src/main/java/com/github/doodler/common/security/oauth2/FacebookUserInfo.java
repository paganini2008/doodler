package com.github.doodler.common.security.oauth2;

import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 
 * @Description: FacebookUserInfo
 * @Author: Fred Feng
 * @Date: 15/10/2024
 * @Version 1.0.0
 */
public class FacebookUserInfo extends OAuth2UserInfo {

    public FacebookUserInfo(OAuth2User oAuth2User) {
        super(oAuth2User);
    }

    @Override
    public String getExternalId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getAvatar() {
        if (attributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            if (pictureObj != null && pictureObj.containsKey("data")) {
                Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                if (dataObj != null && dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return null;
    }

    @Override
    public String getRegistrationId() {
        return "facebook";
    }
}
