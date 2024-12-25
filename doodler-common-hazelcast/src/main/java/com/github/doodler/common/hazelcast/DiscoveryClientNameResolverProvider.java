package com.github.doodler.common.hazelcast;

import java.net.URI;
import io.grpc.NameResolver;
import io.grpc.NameResolver.Args;
import io.grpc.NameResolverProvider;

/**
 * 
 * @Description: DiscoveryClientNameResolverProvider
 * @Author: Fred Feng
 * @Date: 20/12/2024
 * @Version 1.0.0
 */
public class DiscoveryClientNameResolverProvider extends NameResolverProvider {

    @Override
    protected boolean isAvailable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int priority() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, Args args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDefaultScheme() {
        // TODO Auto-generated method stub
        return null;
    }

}
