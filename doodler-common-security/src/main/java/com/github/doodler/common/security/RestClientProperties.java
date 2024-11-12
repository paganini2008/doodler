package com.github.doodler.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import com.github.doodler.common.SecurityKey;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: RestClientProperties
 * @Author: Fred Feng
 * @Date: 24/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("feign.client")
public class RestClientProperties {

	private SecurityKey security = new SecurityKey();
}