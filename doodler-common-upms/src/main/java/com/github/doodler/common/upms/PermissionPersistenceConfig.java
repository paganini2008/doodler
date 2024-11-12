package com.github.doodler.common.upms;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.github.doodler.common.feign.RestClientBeanDefinitionRegistrarSupport;
import com.github.doodler.common.feign.RestClientCandidatesAutoConfiguration;
import com.github.doodler.common.upms.PermissionPersistenceConfig.SysPermissionRestClientRegistrar;

/**
 * @Description: PermissionPersistenceConfig
 * @Author: Fred Feng
 * @Date: 07/11/2023
 * @Version 1.0.0
 */
@Import({ SysPermissionRestClientRegistrar.class })
@Configuration(proxyBeanMethods = false)
public class PermissionPersistenceConfig {

	@ConditionalOnMissingBean
	@Bean
	public PermissionPersistenceService permissionPersistenceService(IRemoteSysPermissionService remoteSysPermissionService) {
		return new RemotePermissionPersistenceServiceImpl(remoteSysPermissionService);
	}
	
	@Bean
	public PermissionPersistenceRemoteCaller permissionPersistenceRemoteCaller(PermissionPersistenceService permissionPersistenceService) {
		return new PermissionPersistenceRemoteCaller(permissionPersistenceService);
	}

	@AutoConfigureAfter(RestClientCandidatesAutoConfiguration.class)
	public static class SysPermissionRestClientRegistrar extends RestClientBeanDefinitionRegistrarSupport {

		@Override
		protected Class<?>[] getApiInterfaceClasses() {
			return new Class<?>[] { IRemoteSysPermissionService.class };
		}
	}
}