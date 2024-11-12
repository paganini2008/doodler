package com.github.doodler.common.ws;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import com.github.doodler.common.security.IdentifiableUserDetails;
import com.github.doodler.common.security.InternalAuthenticationToken;

/**
 * @Description: StdOutMessageWriter
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
@Order(100)
@RequiredArgsConstructor
public class StdOutMessageWriter implements WsStateChangeListener {

    private final WsMessageFanoutAdviceContainer messageFanoutAdviceContainer;

    @Override
    public void onOpen(WsSession session) throws IOException {
        session.sendSessionId();
    }

    @Override
    public final void onMessage(WsUser from, Object data, long timestamp, WsSession session) throws IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            IdentifiableUserDetails userDetails = from.getUserDetails();
            if (userDetails != null) {
                InternalAuthenticationToken authentication = new InternalAuthenticationToken(userDetails,
                        userDetails.getUsername(),
                        userDetails.getPlatform(), false, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        if (!data.getClass().isAnnotationPresent(Transient.class)) {
            session.sendObject(data, timestamp);
        }
        messageFanoutAdviceContainer.triggerPostFanout(from, data, timestamp, session);
    }
}