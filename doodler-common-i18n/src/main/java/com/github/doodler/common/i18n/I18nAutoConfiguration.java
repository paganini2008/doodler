package com.github.doodler.common.i18n;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import com.github.doodler.common.context.ConditionalOnNotApplication;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.feign.RestClientBeanDefinitionRegistrarSupport;
import com.github.doodler.common.feign.RestClientCandidatesAutoConfiguration;
import com.github.doodler.common.i18n.I18nAutoConfiguration.I18nRestClientRegistrar;

/**
 * 
 * @Description: I18nAutoConfiguration
 * @Author: Fred Feng
 * @Date: 22/05/2023
 * @Version 1.0.0
 */
@ConditionalOnNotApplication("doodler-common-service")
@ConditionalOnClass({RestClientCandidatesAutoConfiguration.class})
@Import({RestClientCandidatesAutoConfiguration.class, I18nRestClientRegistrar.class})
@Configuration(proxyBeanMethods = false)
public class I18nAutoConfiguration {

    @Bean("i18nCachedKeyGenerator")
    public I18nCachedKeyGenerator i18nCachedKeyGenerator() {
        return new I18nCachedKeyGenerator();
    }

    @Primary
    @Bean
    public MessageLocalization i18nMessageLocalization(IRemoteI18nService remoteI18nService) {
        return new I18nMessageLocalization(remoteI18nService);
    }

    @AutoConfigureAfter(RestClientCandidatesAutoConfiguration.class)
    public static class I18nRestClientRegistrar extends RestClientBeanDefinitionRegistrarSupport {

        @Override
        protected Class<?>[] getApiInterfaceClasses() {
            return new Class<?>[] {IRemoteI18nService.class};
        }
    }
}
