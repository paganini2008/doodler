package io.doodler.security.line;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import io.doodler.security.UserProfile;

/**
 * @Description: LineProfile
 * @Author: Fred Feng
 * @Date: 11/01/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class LineProfile implements UserProfile {

    private String sub;
    private String name;
    private String email;
    private String picture;

    @Override
    public String getId() {
        return sub;
    }

    @Override
    public String getName() {
        if (StringUtils.isBlank(name)) {
            name = "LineAccount" + RandomStringUtils.random(8, true, true);
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