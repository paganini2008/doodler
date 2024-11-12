package com.github.doodler.common.feign;

import static com.github.doodler.common.feign.RestClientConstants.DEFAULT_LOGGER_NAME;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import com.github.doodler.common.feign.logger.ElkLogger;
import com.github.doodler.common.utils.ListUtils;
import feign.Feign;
import feign.Logger;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.Retryer;
import lombok.experimental.UtilityClass;

/**
 * <p>
 * RestClientUtils
 * How to use?
 * </p>
 *
 * <pre>
 * <code>
 *
 * IRemoteUserService remoteUserService = RestClientUtils.getRemoteService(IRemoteUserService.class,
 *                                                                         "http://13.210.186.45:8088",
 *                                                                         "123456");
 * UserVo userVo = remoteUserService.getUserById(10000L).getData();
 * System.out.println(JacksonUtils.toJsonString(userVo));
 *
 * </code>
 * </pre>
 *
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@UtilityClass
public class RestClientUtils {

    private static final RestClientInterceptorContainer restClientInterceptorContainer = new RestClientInterceptorContainer();
    private static final List<RequestInterceptor> requestInterceptors = new ArrayList<>();
    
    private static Logger defaultLogger = new ElkLogger(DEFAULT_LOGGER_NAME);
    
    static {
        addRequestInterceptor(restClientInterceptorContainer);
        addRestClientInterceptor(new ExternalRequestRestClientInterceptor());
    }

    public void addRequestInterceptor(RequestInterceptor requestInterceptor) {
        if (requestInterceptor != null) {
            requestInterceptors.add(requestInterceptor);
        }
    }

    public void addRestClientInterceptor(RestClientInterceptor restClientInterceptor) {
        restClientInterceptorContainer.addInterceptor(restClientInterceptor);
    }
    
	public static void setDefaultLogger(Logger defaultLogger) {
		RestClientUtils.defaultLogger = defaultLogger;
	}

    public <T> T getRemoteService(Class<T> apiClass, String provider, String securityKey) {
        return getRemoteService(apiClass, provider, 0, securityKey);
    }

    public <T> T getRemoteService(Class<T> apiClass, String provider, int maxRetries, String securityKey) {
        return getRemoteService(apiClass, provider, maxRetries, securityKey, Collections.emptyList());
    }

    public <T> T getRemoteService(Class<T> apiClass, String provider, int maxRetries, String securityKey,
                                  Map<String, String> requestHeaders) {
        return getRemoteService(apiClass, provider, maxRetries, securityKey,
                new SetHeaderRequestInterceptor(requestHeaders));
    }

    public <T> T getRemoteService(Class<T> apiClass, String provider, int maxRetries, String securityKey,
                                  RequestInterceptor requestInterceptor) {
        return getRemoteService(apiClass, provider, maxRetries, securityKey, Arrays.asList(requestInterceptor));
    }

    public <T> T getRemoteService(Class<T> apiClass, String provider, int maxRetries, String securityKey,
                                  List<RequestInterceptor> interceptors) {
        return getRemoteService(apiClass, provider, 10, 60, TimeUnit.SECONDS, maxRetries, Logger.Level.FULL, securityKey,
                interceptors,
                null);
    }

    public <T> T getRemoteService(Class<T> apiClass, String provider, long connectTimeout, long readTimeout,
                                  TimeUnit timeUnit, int maxRetries, Logger.Level level, String securityKey,
                                  List<RequestInterceptor> interceptors,
                                  Consumer<Feign.Builder> consumer) {
        return openRestClient(apiClass, provider, connectTimeout, readTimeout, timeUnit, maxRetries, level,
                mergeInterceptors(securityKey, interceptors), consumer);
    }

    private List<RequestInterceptor> mergeInterceptors(String securityKey,
                                                       List<RequestInterceptor> interceptors) {
        List<RequestInterceptor> list = Arrays.asList(new RestfulRequestInterceptor(),
                new SecurityRequestInterceptor(securityKey));
        return interceptors != null ? ListUtils.concat(list, interceptors) : list;
    }

    public <T> T openRestClient(Class<T> apiClass, String provider, List<RequestInterceptor> interceptors) {
        return openRestClient(apiClass, provider, 0, interceptors);
    }

    public <T> T openRestClient(Class<T> apiClass, String provider, int maxRetries) {
        return openRestClient(apiClass, provider, maxRetries, Collections.emptyList());
    }

    public <T> T openRestClient(Class<T> apiClass, String provider, int maxRetries, Map<String, String> requestHeaders) {
        return openRestClient(apiClass, provider, maxRetries, new SetHeaderRequestInterceptor(requestHeaders));
    }

    public <T> T openRestClient(Class<T> apiClass, String provider, RequestInterceptor requestInterceptor) {
        return openRestClient(apiClass, provider, 0, requestInterceptor);
    }

    public <T> T openRestClient(Class<T> apiClass, String provider, int maxRetries, RequestInterceptor requestInterceptor) {
        return openRestClient(apiClass, provider, maxRetries, Arrays.asList(requestInterceptor));
    }

    public <T> T openRestClient(Class<T> apiClass, String provider, int maxRetries, List<RequestInterceptor> interceptors) {
        return openRestClient(apiClass, provider, 10, 60, TimeUnit.SECONDS, maxRetries, Logger.Level.FULL, interceptors,
                null);
    }

    public <T> T openRestClient(Class<T> apiClass, String provider, long connectTimeout,
                                long readTimeout, TimeUnit timeUnit, int maxRetries, Logger.Level level,
                                List<RequestInterceptor> interceptors, Consumer<Feign.Builder> consumer) {
        return RestClientProxyBuilder.rpc(apiClass)
        		.setLogger(defaultLogger)
                .setProvider(provider)
                .setOptions(new Options(connectTimeout, timeUnit, readTimeout, timeUnit, true))
                .setRetryer(maxRetries > 0 ? new Retryer.Default(3000L, 15L * 1000, maxRetries) : Retryer.NEVER_RETRY)
                .setInterceptors(getAllInterceptors(interceptors))
                .setInterceptorContainer(restClientInterceptorContainer)
                .postConfigurer(consumer)
                .build();
    }

    private List<RequestInterceptor> getAllInterceptors(List<RequestInterceptor> externalInterceptors) {
        List<RequestInterceptor> allRequestInterceptors = new ArrayList<>(requestInterceptors);
        if (CollectionUtils.isNotEmpty(externalInterceptors)) {
            allRequestInterceptors.addAll(externalInterceptors);
        }
        return allRequestInterceptors;
    }



}