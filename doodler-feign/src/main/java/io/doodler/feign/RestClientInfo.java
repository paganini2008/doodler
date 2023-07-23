package io.doodler.feign;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: RestClientInfo
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RestClientInfo implements Serializable {

    private static final long serialVersionUID = 3390792833926153210L;

    private String serviceId;
    private Class<?> apiInterfaceClass;
    private String beanName;
    private String[] urls;

    private Map<String, Object> settings;

    public RestClientInfo(String serviceId, Class<?> apiInterfaceClass, String beanName, String[] urls) {
    	this.serviceId = serviceId;
        this.apiInterfaceClass = apiInterfaceClass;
        this.beanName = beanName;
        this.urls = urls;
    }

    private RestClientMethodInfo[] methodInfos = new RestClientMethodInfo[0];

    @Getter
    @Setter
    @ToString
    public static class RestClientMethodInfo implements Serializable {

        private static final long serialVersionUID = -8056760910046716857L;

        private String method;
        private Class<?>[] parameterTypes;
        private Type returnType;
        private String requestLine;
        private String[] requestHeaders;
    }
}