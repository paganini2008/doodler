package com.github.doodler.common.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.id.IdGenerator;

/**
 * 
 * @Description: OAuth2ApiConfig
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2ApiConfig {

    @Bean
    public ClientCredentials clientCredentials(IdGenerator idGenerator) {
        return new DefaultClientCredentials(idGenerator);
    }

    @ConditionalOnMissingBean
    @Bean
    public OAuth2AuthenticationPostHandler oAuth2AuthenticationPostHandler() {
        return new DefaultOAuth2AuthenticationPostHandler();
    }

}
