package io.doodler.security;

import static io.doodler.security.SecurityConstants.NA;

import java.util.Base64;

/**
 * @Description: BasicTokenStrategy
 * @Author: Fred Feng
 * @Date: 03/03/2023
 * @Version 1.0.0
 */
public class BasicTokenStrategy implements TokenStrategy {

    @Override
    public String encode(IdentifiableUserDetails userDetails, long expiration) {
        String payload = userDetails.getUsername() + ":" + NA;
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }

    @Override
    public IdentifiableUserDetails decode(String token) {
        String rawText = new String(Base64.getDecoder().decode(token));
        String[] usernameAndPassword = rawText.split(":", 2);
        String username = usernameAndPassword[0];
        return new BasicUser(null, username, NA);
        		
    }
}