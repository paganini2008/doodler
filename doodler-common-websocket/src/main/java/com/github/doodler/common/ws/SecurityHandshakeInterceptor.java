package com.github.doodler.common.ws;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.utils.DecryptionUtils;
import lombok.RequiredArgsConstructor;

/**
 * @Description: SecurityHandshakeInterceptor
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class SecurityHandshakeInterceptor extends HandshakeInterceptorSupport {

    private final WsServerProperties serverConfig;
    private final RedisOperations<String, Object> redisOperations;
    private final InstanceId instanceId;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
    	if(!instanceId.isStandby()) {
    		return false;
    	}
        String requestPath = request.getURI().getPath();
        //requestPath = URLDecoder.decode(requestPath, "UTF-8");
        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }
        List<String> pathArgs = new ArrayList<>(Arrays.asList(requestPath.split("\\/", 4)));
        Collections.reverse(pathArgs);
        String cipherText = pathArgs.get(0);
        if (redisOperations.hasKey(cipherText)) {
            if (log.isWarnEnabled()) {
                log.warn("Websocket cipher identifier is unique. Url: {}", request.getURI());
            }
            return false;
        }

        attributes.put("channel", pathArgs.get(1));
        String identifier = null;
        String rawText;
        try {
        	String securityKey = serverConfig.getSecurityKey();
            rawText = DecryptionUtils.decryptText(cipherText, securityKey);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Cipher text cannot be decrypted by AES, input: {}", cipherText);
            }
            return false;
        }
        long timestamp;
        int index;
        if ((index = rawText.indexOf(":")) != -1) {
            timestamp = Long.parseLong(rawText.substring(index + 1));
            identifier = rawText.substring(0, index);
        } else {
            timestamp = Long.parseLong(rawText);
        }
        if (serverConfig.getValidityPeriod() > 0 &&
                System.currentTimeMillis() - timestamp >= serverConfig.getValidityPeriod()) {
            if (log.isWarnEnabled()) {
                log.warn("Websocket cipher identifier is expired. Url: {}", request.getURI());
            }
            return false;
        }
        if (StringUtils.isNotBlank(identifier)) {
            attributes.put("identifier", identifier);
        }
        if (serverConfig.getValidityPeriod() > 0) {
            redisOperations.opsForValue().set(cipherText, "N/A",
                    Duration.ofMillis(serverConfig.getValidityPeriod()));
        }
        return true;
    }

}