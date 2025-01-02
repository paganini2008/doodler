package com.github.doodler.common.grpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import com.github.doodler.common.utils.MapUtils;
import io.grpc.NameResolver;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: DefaultLbNameResolverFactory
 * @Author: Fred Feng
 * @Date: 03/01/2025
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultLbNameResolverFactory implements LbNameResolverFactory {

    private final DiscoveryClient discoveryClient;
    private final Map<String, NameResolver> cache = new ConcurrentHashMap<>();

    @Override
    public NameResolver getNameResolver(String serviceId) {
        return MapUtils.getOrCreate(cache, serviceId,
                () -> new LbNameResolver(serviceId, discoveryClient));
    }

}
