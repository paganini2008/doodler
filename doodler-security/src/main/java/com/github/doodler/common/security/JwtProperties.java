package com.github.doodler.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: JwtProperties
 * @Author: Fred Feng
 * @Date: 16/07/2020
 * @Version 1.0.0
 */
@ConfigurationProperties("spring.security.jwt")
@Getter
@Setter
public class JwtProperties {

    private String prefix = "";
    private String issuer = "doodler";
    private String secretKey = "doodler";
    private int expiration = 24 * 60 * 60;
}