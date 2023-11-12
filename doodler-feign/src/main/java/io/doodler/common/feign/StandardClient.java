package io.doodler.common.feign;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: StandardClient
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@Slf4j
public class StandardClient implements Client {

	private final Client target;
	private final LoadBalancerClient loadBalancerClient;

	public StandardClient(Client target, LoadBalancerClient loadBalancerClient) {
		this.target = target;
		this.loadBalancerClient = loadBalancerClient;
	}

	@Override
	public Response execute(Request request, Options options) throws IOException {
		final URI originalUri = URI.create(request.url());
		String serviceId = originalUri.getHost();
		if (!loadBalancerClient.contains(serviceId)) {
			return target.execute(request, options);
		}
		Assert.state(serviceId != null, "Request URI does not contain a valid hostname: " + originalUri);
		ServiceInstance instance = loadBalancerClient.choose(serviceId, request);
		if (instance == null) {
			String message = "Load balancer does not contain an instance for the service " + serviceId;
			if (log.isWarnEnabled()) {
				log.warn(message);
			}
			return Response.builder().request(request).status(HttpStatus.SERVICE_UNAVAILABLE.value())
					.body(message, StandardCharsets.UTF_8).build();
		}
		String reconstructedUrl = loadBalancerClient.reconstructURI(instance, originalUri).toString();
		Request newRequest = buildRequest(request, reconstructedUrl, instance);
		if (log.isTraceEnabled()) {
			log.trace("Rewrite new request to: {}", newRequest);
		}
		return target.execute(newRequest, options);
	}

	protected Request buildRequest(Request request, String reconstructedUrl, ServiceInstance instance) {
		Map<String, Collection<String>> copyHeaders = new HashMap<>(request.headers());
		copyHeaders.put("Server-Host-Url", Collections.singletonList(instance.getUrl()));
		return Request.create(request.httpMethod(), reconstructedUrl, copyHeaders, request.body(),
				request.charset(), request.requestTemplate());
	}
}