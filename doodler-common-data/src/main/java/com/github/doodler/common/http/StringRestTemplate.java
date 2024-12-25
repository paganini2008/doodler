package com.github.doodler.common.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @Description: StringRestTemplate
 * @Author: Fred Feng
 * @Date: 16/12/2024
 * @Version 1.0.0
 */
public class StringRestTemplate extends RestTemplate {

    public StringRestTemplate() {
        this(StandardCharsets.UTF_8);
    }

    public StringRestTemplate(Charset charset) {
        super();
        for (HttpMessageConverter<?> converter : getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(charset);
                break;
            }
        }
    }

    public StringRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory, Charset charset) {
        super(clientHttpRequestFactory);
        for (HttpMessageConverter<?> converter : getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(charset);
                break;
            }
        }
    }

    public StringRestTemplate(List<HttpMessageConverter<?>> converters, Charset charset) {
        super(converters);
        for (HttpMessageConverter<?> converter : getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(charset);
                break;
            }
        }
    }
}
