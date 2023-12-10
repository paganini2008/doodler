package io.doodler.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: BasicCredentials
 * @Author: Fred Feng
 * @Date: 26/11/2023
 * @Version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicCredentials {

	private String username;
	private String password;
	private String platform;
	private String[] roles;
}