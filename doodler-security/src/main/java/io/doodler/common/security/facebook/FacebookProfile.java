package io.doodler.common.security.facebook;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import io.doodler.common.security.UserProfile;

/**
 * @Description: FacebookProfile
 * @Author: Fred Feng
 * @Date: 02/12/2022
 * @Version 1.0.0
 */
@NoArgsConstructor
@Setter
@ToString
public class FacebookProfile implements UserProfile {

    private String id;
    private String name;
    private String email;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        if (StringUtils.isBlank(name)) {
            name = "FacebookAcount" + RandomStringUtils.random(8, true, true);
        }
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}