package com.github.doodler.common.feign;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import com.github.doodler.common.feign.RestClientMetadata.RestClientMethodInfo;
import com.github.doodler.common.utils.MapUtils;
import feign.Headers;
import feign.RequestLine;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @Description: RestClientMetadataCollector
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RestClientMetadataCollector implements InitializingRestClientBean, EnvironmentAware {

    private final Map<Type, RestClientMetadata> cache = new ConcurrentHashMap<>();

    public List<RestClientMetadata> metadatas() {
        return new ArrayList<>(cache.values());
    }

    public RestClientMetadata lookup(Type type) {
        return cache.get(type);
    }

    @Setter
    private Environment environment;

    @Override
    public void initialize(Object proxy, Class<?> apiInterfaceClass, String beanName) {
        final RestClient restClientAnno = apiInterfaceClass.getAnnotation(RestClient.class);
        String[] urls =
                StringUtils.isNotBlank(restClientAnno.url()) ? new String[] {restClientAnno.url()}
                        : findUrls(restClientAnno.serviceId());
        RestClientMetadata restClientMetadata = MapUtils.getOrCreate(cache, apiInterfaceClass,
                () -> new RestClientMetadata(StringUtils.isNotBlank(restClientAnno.serviceId())
                        ? restClientAnno.serviceId()
                        : "open-api", apiInterfaceClass, beanName, urls));
        Method[] methods = RestClient.class.getDeclaredMethods();
        if (ArrayUtils.isNotEmpty(methods)) {
            Map<String, Object> settings =
                    Arrays.stream(methods).collect(LinkedHashMap::new,
                            (map, method) -> map.put(method.getName(),
                                    getAnnotationValue(restClientAnno, method)),
                            LinkedHashMap::putAll);
            restClientMetadata.setSettings(settings);
        }
        List<Method> methodList =
                MethodUtils.getMethodsListWithAnnotation(apiInterfaceClass, RequestLine.class);
        if (CollectionUtils.isNotEmpty(methodList)) {
            List<RestClientMethodInfo> methodInfos = new ArrayList<>();
            methodList.forEach(method -> {
                String requestLine = method.getAnnotation(RequestLine.class).value();
                RestClientMethodInfo methodInfo = new RestClientMethodInfo();
                methodInfo.setRequestLine(requestLine);
                Headers headers = method.getAnnotation(Headers.class);
                if (headers == null) {
                    headers = apiInterfaceClass.getAnnotation(Headers.class);
                }
                if (headers != null) {
                    methodInfo.setRequestHeaders(headers.value());
                }
                methodInfo.setMethod(method.getName());
                methodInfo.setParameterTypes(method.getParameterTypes());
                methodInfo.setReturnType(method.getGenericReturnType());
                methodInfos.add(methodInfo);
            });
            restClientMetadata.setMethodInfos(methodInfos.toArray(new RestClientMethodInfo[0]));
        }
    }

    protected String[] findUrls(String serviceId) {
        return new String[] {String.format("http://%s", serviceId)};
    }

    @SneakyThrows
    private Object getAnnotationValue(RestClient restClientAnnotation, Method m) {
        m.setAccessible(true);
        return m.invoke(restClientAnnotation);
    }
}
