package io.doodler.feign;

import feign.Headers;
import feign.RequestLine;
import io.doodler.common.utils.MapUtils;
import io.doodler.feign.RestClientInfo.RestClientMethodInfo;
import io.doodler.feign.RestClientProperties.Instance;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @Description: RestClientInfoCollector
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RestClientInfoCollector implements InitializingRestClientBean, EnvironmentAware {

    private final RestClientProperties config;

    private final Map<Type, RestClientInfo> infos = new ConcurrentHashMap<>();

    public List<RestClientInfo> infos() {
        return new ArrayList<>(infos.values());
    }

    public RestClientInfo get(Type type) {
        return infos.get(type);
    }

    @Setter
    private Environment environment;

    @Override
    public void initialize(Object proxy, Class<?> apiInterfaceClass, String beanName) {
        final RestClient restClientAnnotation = apiInterfaceClass.getAnnotation(RestClient.class);
        String[] urls;
        if (StringUtils.isNotBlank(restClientAnnotation.url())) {
            urls = new String[]{restClientAnnotation.url()};
        } else {
            urls = findUrls(restClientAnnotation.serviceId());
        }
        RestClientInfo restClientInfo = MapUtils.getOrCreate(infos, apiInterfaceClass,
                () -> new RestClientInfo(restClientAnnotation.serviceId(), apiInterfaceClass, beanName, urls));
        Method[] methods = RestClient.class.getDeclaredMethods();
        if (ArrayUtils.isNotEmpty(methods)) {
            Map<String, Object> settings = Arrays.stream(methods).collect(LinkedHashMap::new,
                    (map, method) -> map.put(method.getName(), getAnnotationValue(restClientAnnotation, method)),
                    LinkedHashMap::putAll);
            restClientInfo.setSettings(settings);
        }
        List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(apiInterfaceClass, RequestLine.class);
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
            restClientInfo.setMethodInfos(methodInfos.toArray(new RestClientMethodInfo[0]));
        }
    }

    private String[] findUrls(String serviceId) {
        if (StringUtils.isNotBlank(serviceId)) {
            if (ArrayUtils.isNotEmpty(config.getInstances())) {
                Optional<Instance> opt = Arrays.stream(config.getInstances())
                        .filter(i -> i.getServiceId().equals(serviceId))
                        .findFirst();
                if (opt.isPresent()) {
                    return opt.get().getUrls().toArray(new String[0]);
                }
            }
        }
        return new String[0];
    }

    @SneakyThrows
    private Object getAnnotationValue(RestClient restClientAnnotation, Method m) {
        m.setAccessible(true);
        return m.invoke(restClientAnnotation);
    }
}