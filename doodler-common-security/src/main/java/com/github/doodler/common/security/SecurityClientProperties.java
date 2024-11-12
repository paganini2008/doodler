package com.github.doodler.common.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: SecurityClientProperties
 * @Author: Fred Feng
 * @Date: 06/12/2022
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("spring.security.client")
public class SecurityClientProperties {
	
	private int expiration = 24 * 60 * 60;
	private int rememberMeDuration = AbstractRememberMeServices.TWO_WEEKS_S;
    private String saPassword;
    private List<String> permittedUrls = new ArrayList<>();
    private boolean basicEnabled = true;
    private boolean showAuthorizationType = false;
}