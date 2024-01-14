package com.github.doodler.common.context;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.doodler.common.SecurityKey;

import lombok.Data;

/**
 * @Description: ServerProperties
 * @Author: Fred Feng
 * @Date: 31/10/2023
 * @Version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
public class ServerProperties {

	private SecurityKey security;
	private BasicCredentials credentials;
	private List<String> exposeServlets;
	
	@Data
	public static class BasicCredentials{
		
		private String user;
		
		private String password;
	}
}