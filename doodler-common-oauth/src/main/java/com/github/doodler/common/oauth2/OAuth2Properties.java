package com.github.doodler.common.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * 
 * @Description: OAuth2Properties
 * @Author: Fred Feng
 * @Date: 11/11/2024
 * @Version 1.0.0
 */
@ConfigurationProperties(prefix = "spring.security.oauth2")
@Data
public class OAuth2Properties {

    private String loginPageUrl = "/login";
    private String testUsername = "guest";
    private String testPassword = "guest";

}
