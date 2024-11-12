package com.github.doodler.common.feign;

import static com.github.doodler.common.feign.RestClientConstants.DEFAULT_LOGGER_NAME;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.Response;
import feign.Retryer;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Description: RestClientProxyBuilder
 * @Author: Fred Feng
 * @Date: 06/01/2023
 * @Version 1.0.0
 */
@Accessors(chain = true)
@Setter
@Getter
public class RestClientProxyBuilder<T> {

    private final Class<T> apiInterfaceClass;
    private String provider;
    private Client client;
    private Encoder encoder;
    private Decoder decoder;
    private ErrorDecoder errorDecoder;
    private Logger logger;
    private Logger.Level level;
    private Options options;
    private Retryer retryer;
    private List<RequestInterceptor> interceptors = Collections.emptyList();
    private Consumer<Feign.Builder> configurer;
    private RestClientInterceptorContainer interceptorContainer = new RestClientInterceptorContainer();

    RestClientProxyBuilder(Class<T> apiInterfaceClass) {
        this.apiInterfaceClass = apiInterfaceClass;
        this.client = new OkHttpClient();
        
        GenericEncoderDecoderFactory encoderDecoderFactory = new GenericEncoderDecoderFactory();
        this.encoder = encoderDecoderFactory.getEncoder();
        this.decoder = new InternalDecoder(encoderDecoderFactory.getDecoder());

        this.errorDecoder = new GlobalErrorDecoder(interceptorContainer);
        this.logger = new Slf4jLogger(DEFAULT_LOGGER_NAME);
        this.level = Logger.Level.FULL;
        this.options = new Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true);
        this.retryer = Retryer.NEVER_RETRY;
    }

    public RestClientProxyBuilder<T> postConfigurer(Consumer<Feign.Builder> configurer) {
        if (configurer != null) {
            this.configurer = (this.configurer != null ? this.configurer.andThen(configurer) : configurer);
        }
        return this;
    }

    public T build() {
        Feign.Builder builder = Feign.builder()
                .client(getClient())
                .encoder(getEncoder())
                .decoder(getDecoder())
                .errorDecoder(getErrorDecoder())
                .options(getOptions())
                .retryer(getRetryer())
                .logger(getLogger())
                .logLevel(getLevel())
                .requestInterceptors(getInterceptors());
        if (this.configurer != null) {
            this.configurer.accept(builder);
        }
        return builder.target(apiInterfaceClass, provider);
    }

    @RequiredArgsConstructor
    class InternalDecoder implements Decoder {

        private final Decoder delegate;

        @Override
        public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
            try {
                return delegate.decode(response, type);
            } finally {
                if (interceptorContainer != null) {
                    interceptorContainer.onPostHandle(response.request(), response);
                }
            }
        }
    }

    public static <T> RestClientProxyBuilder<T> rpc(Class<T> apiInterfaceClass) {
        return new RestClientProxyBuilder<T>(apiInterfaceClass);
    }
}