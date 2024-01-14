package com.github.doodler.common.security;

/**
 * @Description: TokenStrategy
 * @Author: Fred Feng
 * @Date: 03/01/2020
 * @Version 1.0.0
 */
public interface TokenStrategy {

    String encode(IdentifiableUserDetails userDetails, long expiration);

    IdentifiableUserDetails decode(String token);

    default boolean validate(String token) {
        return true;
    }
}