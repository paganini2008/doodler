package com.github.doodler.common.grpc;

import io.grpc.NameResolver;

/**
 * 
 * @Description: LbNameResolverFactory
 * @Author: Fred Feng
 * @Date: 03/01/2025
 * @Version 1.0.0
 */
public interface LbNameResolverFactory {

    NameResolver getNameResolver(String serviceId);

}
