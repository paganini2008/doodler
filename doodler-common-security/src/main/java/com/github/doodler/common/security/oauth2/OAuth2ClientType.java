package com.github.doodler.common.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;
import com.github.doodler.common.enums.EnumUtils;

/**
 * 
 * @Description: OAuth2ClientType
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
public enum OAuth2ClientType implements EnumConstant {

    GOOGLE("google", true, "Google") {

        @Override
        public OAuth2UserInfo getUserInfo(OAuth2User oAuth2User) {
            return new GoogleUserInfo(oAuth2User);
        }

    },

    FACEBOOK("facebook", false, "Facebook") {
        @Override
        public OAuth2UserInfo getUserInfo(OAuth2User oAuth2User) {
            return new FacebookUserInfo(oAuth2User);
        }
    },

    GITHUB("github", false, "GitHub") {
        @Override
        public OAuth2UserInfo getUserInfo(OAuth2User oAuth2User) {
            return new GitHubUserInfo(oAuth2User);
        }
    },

    TWITCH("twitch", false, "Twitch") {
        @Override
        public OAuth2UserInfo getUserInfo(OAuth2User oAuth2User) {
            return new TwitchUserInfo(oAuth2User);
        }
    },

    LINE("line", true, "Line") {
        @Override
        public OAuth2UserInfo getUserInfo(OAuth2User oAuth2User) {
            return new LineUserInfo(oAuth2User);
        }
    };

    private final String value;
    private final boolean oidcSupported;
    private final String repr;

    private OAuth2ClientType(String value, boolean oidcSupported, String repr) {
        this.value = value;
        this.oidcSupported = oidcSupported;
        this.repr = repr;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return this.repr;
    }

    public boolean isOidcSupported() {
        return oidcSupported;
    }

    public abstract OAuth2UserInfo getUserInfo(OAuth2User oAuth2User);

    @JsonCreator
    public static OAuth2ClientType getBy(String value) {
        return EnumUtils.valueOf(OAuth2ClientType.class, value);
    }
}
