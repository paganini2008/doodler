package io.doodler.common.security.twitch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import io.doodler.common.security.UserProfile;

/**
 * @Description: TwitchProfile
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class TwitchProfile implements UserProfile {

    private String azp;
    private String email;
    private String preferred_username;
    private Boolean verified_email;
    private String picture;

    @Override
    public String getId() {
        return azp;
    }

    @Override
    public String getName() {
        if (StringUtils.isBlank(preferred_username)) {
            preferred_username = "TwitchAccount" + RandomStringUtils.random(8, true, true);
        }
        return preferred_username;
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