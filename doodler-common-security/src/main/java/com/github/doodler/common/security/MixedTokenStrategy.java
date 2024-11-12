package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BEARER;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: MixedTokenStrategy
 * @Author: Fred Feng
 * @Date: 03/03/2023
 * @Version 1.0.0
 */
public class MixedTokenStrategy implements TokenStrategy {

    private final Map<String, TokenStrategy> tokenStrategies = new ConcurrentHashMap<>();
    private TokenStrategy defaultTokenStrategy;

    public void addTokenStrategy(String type, TokenStrategy tokenStrategy) {
        tokenStrategies.put(type, tokenStrategy);
    }

    public void removeTokenStrategy(String type) {
        tokenStrategies.remove(type);
    }

    public void setDefaultTokenStrategy(TokenStrategy defaultTokenStrategy) {
        this.defaultTokenStrategy = defaultTokenStrategy;
    }

    @Override
    public String encode(IdentifiableUserDetails userDetails, long expiration) {
        TokenStrategy tokenStrategy = tokenStrategies.getOrDefault(userDetails.getAuthorizationType(),
                defaultTokenStrategy);
        if (tokenStrategy == null) {
            throw new TokenStrategyNotFoundException();
        }
        return tokenStrategy.encode(userDetails, expiration);
    }

    @Override
    public IdentifiableUserDetails decode(String token) {
        String authorizationType = AUTHORIZATION_TYPE_BEARER;
        int blankIndex = token.indexOf(" ");
        if (blankIndex > 0) {
            authorizationType = token.substring(0, blankIndex);
            token = token.substring(blankIndex + 1);
        }
        TokenStrategy tokenStrategy = tokenStrategies.getOrDefault(authorizationType, defaultTokenStrategy);
        if (tokenStrategy == null) {
            throw new TokenStrategyNotFoundException(token);
        }
        return tokenStrategy.decode(token);
    }

    @Override
    public boolean validate(String token) {
        String authorizationType = AUTHORIZATION_TYPE_BEARER;
        int blankIndex = token.indexOf(" ");
        if (blankIndex > 0) {
            authorizationType = token.substring(0, blankIndex);
            token = token.substring(blankIndex + 1);
        }
        TokenStrategy tokenStrategy = tokenStrategies.getOrDefault(authorizationType, defaultTokenStrategy);
        if (tokenStrategy == null) {
            throw new TokenStrategyNotFoundException(token);
        }
        return tokenStrategy.validate(token);
    }
}