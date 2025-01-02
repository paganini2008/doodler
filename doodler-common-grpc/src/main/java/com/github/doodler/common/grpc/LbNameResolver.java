package com.github.doodler.common.grpc;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;

/**
 * 
 * @Description: LbNameResolver
 * @Author: Fred Feng
 * @Date: 03/01/2025
 * @Version 1.0.0
 */
public class LbNameResolver extends NameResolver {

    private final String serviceId;
    private final DiscoveryClient discoveryClient;

    public LbNameResolver(String serviceId, DiscoveryClient discoveryClient) {
        this.serviceId = serviceId;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public String getServiceAuthority() {
        return serviceId;
    }

    @Override
    public void start(Listener2 listener) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (instances.isEmpty()) {
            listener.onError(
                    Status.UNAVAILABLE.withDescription("No instances available for " + serviceId));
            return;
        }
        List<EquivalentAddressGroup> addressGroups = instances.stream()
                .map(instance -> new EquivalentAddressGroup(
                        new InetSocketAddress(instance.getHost(), instance.getPort())))
                .collect(Collectors.toList());
        ResolutionResult resolutionResult =
                ResolutionResult.newBuilder().setAddresses(addressGroups).build();
        listener.onResult(resolutionResult);
    }

    @Override
    public void shutdown() {}

}
