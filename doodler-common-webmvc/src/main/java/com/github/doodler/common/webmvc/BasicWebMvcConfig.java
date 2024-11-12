package com.github.doodler.common.webmvc;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.github.doodler.common.utils.JacksonUtils;

/**
 * @Description: BasicWebMvcConfig
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@ComponentScan("com.github.doodler.common.webmvc")
@EnableConfigurationProperties({ThreadPoolProperties.class})
@Configuration
public class BasicWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private HandlerInterceptorChain handlerInterceptorChain;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptorChain).addPathPatterns("/**");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(x -> x instanceof MappingJackson2HttpMessageConverter);
        converters.add(0,
                new MappingJackson2HttpMessageConverter(JacksonUtils.getObjectMapperForWebMvc()));
    }
}
