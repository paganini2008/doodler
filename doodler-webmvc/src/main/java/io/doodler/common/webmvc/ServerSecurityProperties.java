package io.doodler.common.webmvc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.doodler.common.SecurityKey;

import lombok.Data;

/**
 * @Description: ServerSecurityProperties
 * @Author: Fred Feng
 * @Date: 31/10/2023
 * @Version 1.0.0
 */
@Data
@ConfigurationProperties("server")
public class ServerSecurityProperties {

	private SecurityKey security;
}