package com.github.doodler.common.ws;

import com.github.doodler.common.security.IdentifiableUserDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: BasicUser
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BasicUser extends AnonymousUser {

    private static final long serialVersionUID = 3598098575534611484L;

    public BasicUser(String channel, String sessionId, IdentifiableUserDetails userDetails) {
        super(channel, sessionId);
        this.userDetails = userDetails;
    }

    private IdentifiableUserDetails userDetails;

    @Override
    public String toString() {
        return String.format("[%s-%s]: %s", getChannel(), getSessionId(), getUserDetails());
    }
}