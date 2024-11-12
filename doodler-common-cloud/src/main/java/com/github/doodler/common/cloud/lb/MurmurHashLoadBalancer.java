package com.github.doodler.common.cloud.lb;

import java.nio.charset.StandardCharsets;
import java.util.List;
import com.github.doodler.common.cloud.ServiceInstance;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: MurmurHashLoadBalancer
 * @Author: Fred Feng
 * @Date: 14/08/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class MurmurHashLoadBalancer implements LoadBalancer {

    private final PartitionKey partitionKey;

    @Override
    public ServiceInstance choose(String serviceId, List<ServiceInstance> apiInstances, Object parameter) {
        HashFunction hashFunction = Hashing.murmur3_128();
        int hash = hashFunction.hashString(partitionKey != null ? partitionKey.getHashKey(parameter) : parameter.toString(),
                StandardCharsets.UTF_8).asInt();
        int index = (int) (hash & 0x7FFFFFFF % apiInstances.size());
        return apiInstances.get(index);
    }

    @FunctionalInterface
    public static interface PartitionKey {

        String getHashKey(Object parameter);

    }

}
