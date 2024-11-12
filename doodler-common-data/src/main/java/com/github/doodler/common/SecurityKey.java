package com.github.doodler.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @Description: SecurityKey
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SecurityKey {

	private String key = "123456";
	private String salt;
	
	public SecurityKey(String key){
		this.key = key;
		this.salt = null;
	}
	
}
