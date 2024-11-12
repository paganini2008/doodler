package com.github.doodler.common.upms;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.feign.RestClientBeanDefinitionRegistrarSupport;
import com.github.doodler.common.feign.RestClientCandidatesAutoConfiguration;
import com.github.doodler.common.upms.SysUserLogWriterConfig.SysUserLogRestClientRegistrar;

/**
 * @Description: SysUserLogWriterConfig
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@Import({SysUserLogRestClientRegistrar.class})
@Configuration(proxyBeanMethods = false)
public class SysUserLogWriterConfig {

    @ConditionalOnMissingBean
    @Bean
    public SysUserLogger restClientSysUserLogger(IRemoteSysUserLogService sysUserLogService) {
        return new RestClientSysUserLogger(sysUserLogService);
    }

    @Bean
    public SysUserLogWriter sysUserLogWriter(ObjectMapper objectMapper, SysUserLogger sysUserLoger) {
        return new SysUserLogWriter(objectMapper, sysUserLoger);
    }

    @AutoConfigureAfter(RestClientCandidatesAutoConfiguration.class)
    public static class SysUserLogRestClientRegistrar extends RestClientBeanDefinitionRegistrarSupport {

        @Override
        protected Class<?>[] getApiInterfaceClasses() {
            return new Class<?>[]{IRemoteSysUserLogService.class};
        }
    }
}