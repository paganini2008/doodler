package com.github.doodler.common.ws;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.doodler.common.security.IdentifiableUserDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: AnonymousUser
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
@JsonInclude(value = Include.NON_NULL)
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class AnonymousUser implements WsUser, Serializable {

    private static final long serialVersionUID = -3024777940351224548L;

    public AnonymousUser(String channel, String sessionId) {
        this.channel = channel;
        this.sessionId = WsContants.SESSION_ID_PREFIX + (StringUtils.isNotBlank(sessionId) ? sessionId : UUID.randomUUID().toString());
    }

    private String channel;
    private String sessionId;

    @Override
    public IdentifiableUserDetails getUserDetails() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("[%s-%s]", channel, sessionId);
    }
}