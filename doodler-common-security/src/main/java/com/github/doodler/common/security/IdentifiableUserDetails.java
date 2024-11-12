package com.github.doodler.common.security;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: IdentifiableUserDetails
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
public interface IdentifiableUserDetails extends PlatformUserDetails {

    @Nullable
    Long getId();

    String getEmail();

    default String getIdentity() {
        String identity = getEmail();
        if (StringUtils.isNotBlank(identity)) {
            return identity;
        }
        identity = getUsername();
        if (StringUtils.isNotBlank(identity)) {
            return identity;
        }
        if (getId() == null) {
            throw new UsernameNotFoundException("Null Identity");
        }
        return getId().toString();
    }

    default boolean isFirstLogin() {
        return false;
    }

    default Map<String, Object> getAdditionalInformation() {
        return new HashMap<>();
    }
}
