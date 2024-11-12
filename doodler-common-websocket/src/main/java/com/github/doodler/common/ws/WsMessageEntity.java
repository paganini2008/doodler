package com.github.doodler.common.ws;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: WsMessageEntity
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class WsMessageEntity {

    private String serverId;
    private WsUser from;
    private Object payload;
    private long timestamp;
    private String[] includedSessionIds;

    public WsMessageEntity(String serverId, WsUser from, Object payload, String[] includedSessionIds) {
        this.serverId = serverId;
        this.from = from;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
        this.includedSessionIds = includedSessionIds;
    }
}