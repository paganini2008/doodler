package com.github.doodler.common.cloud.feign;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import com.github.doodler.common.cloud.ServiceInstance;
import com.github.doodler.common.cloud.lb.LoadBalancerClient;
import com.github.doodler.common.utils.UrlUtils;
import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: StandardClient
 * @Author: Fred Feng
 * @Date: 03/11/2024
 * @Version 1.0.0
 */
@Slf4j
public final class StandardClient implements Client {

    private final Client target;
    private final LoadBalancerClient loadBalancerClient;
    private final BeanFactory beanFactory;

    public StandardClient(Client target, LoadBalancerClient loadBalancerClient,
            BeanFactory beanFactory) {
        this.target = target;
        this.loadBalancerClient = loadBalancerClient;
        this.beanFactory = beanFactory;
    }

    @Override
    public Response execute(Request request, Options options) throws IOException {
        final URI originalUri = URI.create(request.url());
        String userInfo = originalUri.getUserInfo();
        String serviceId = originalUri.getHost();
        Request newRequest;
        if ("spel".equalsIgnoreCase(userInfo)) {
            ConfigurableListableBeanFactory cbf = (ConfigurableListableBeanFactory) beanFactory;
            String hostUrl = (String) cbf.getBeanExpressionResolver().evaluate(
                    String.format("#{%s}", serviceId), new BeanExpressionContext(cbf, null));
            String reconstructedUrl = originalUri.toString().replace(
                    String.format("%s://%s@%s", originalUri.getScheme(), userInfo, serviceId),
                    hostUrl);
            newRequest = buildRequest(request, reconstructedUrl, null);
        } else {
            if (!loadBalancerClient.contains(serviceId)) {
                return target.execute(request, options);
            }
            Assert.state(serviceId != null,
                    "Request URI does not contain a valid hostname: " + originalUri);
            ServiceInstance instance = loadBalancerClient.choose(serviceId, request);
            if (instance == null) {
                String message =
                        "Load balancer does not contain an instance for the service " + serviceId;
                if (log.isWarnEnabled()) {
                    log.warn(message);
                }
                return Response.builder().request(request)
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .body(message, StandardCharsets.UTF_8).build();
            }
            String reconstructedUrl =
                    loadBalancerClient.reconstructURI(instance, originalUri).toString();
            newRequest = buildRequest(request, reconstructedUrl, instance);
        }
        if (log.isTraceEnabled()) {
            log.trace("Rewrite new request to: {}", newRequest);
        }
        return target.execute(newRequest, options);
    }

    protected Request buildRequest(Request request, String reconstructedUrl,
            ServiceInstance instance) {
        Map<String, Collection<String>> copyHeaders = new HashMap<>(request.headers());
        copyHeaders.put("Server-Host-Url",
                Collections.singletonList(instance != null ? instance.getUrl()
                        : UrlUtils.toHostUrl(reconstructedUrl).toString()));
        return Request.create(request.httpMethod(), reconstructedUrl, copyHeaders, request.body(),
                request.charset(), request.requestTemplate());
    }
}
