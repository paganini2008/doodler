package io.doodler.i18n;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.doodler.feign.RestClientBeanDefinitionRegistrarSupport;
import io.doodler.feign.RestClientCandidatesAutoConfiguration;
import io.doodler.i18n.I18nAutoConfiguration.I18nRestClientRegistrar;

/**
 * 
 * @Description: I18nAutoConfiguration
 * @Author: Fred Feng
 * @Date: 22/05/2023
 * @Version 1.0.0
 */
@Import({RestClientCandidatesAutoConfiguration.class, I18nRestClientRegistrar.class})
public class I18nAutoConfiguration {
	
	@Bean("i18nCachedKeyGenerator")
	public I18nCachedKeyGenerator i18nCachedKeyGenerator() {
		return new I18nCachedKeyGenerator();
	}

	@AutoConfigureAfter(RestClientCandidatesAutoConfiguration.class)
	public static class I18nRestClientRegistrar extends RestClientBeanDefinitionRegistrarSupport {

		@Override
		protected Class<?>[] getApiInterfaceClasses() {
			return new Class<?>[]{IRemoteI18nService.class};
		}
	}
}
