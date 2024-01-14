package com.github.doodler.common.security.google;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.doodler.common.security.UserProfile;

/**
 * @Description: GoogleProfile
 * @Author: Fred Feng
 * @Date: 30/07/2020
 * @Version 1.0.0
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class GoogleProfile implements UserProfile {

    private String id;
    private String name;
    private String email;
    private Boolean verified_email;
    private String picture;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        if (StringUtils.isBlank(name)) {
            name = "GoogleAccount" + RandomStringUtils.random(8, true, true);
        }
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getAvatar() {
        return picture;
    }
}