package com.github.doodler.common.grpc;

import java.net.URI;
import io.grpc.NameResolver;
import io.grpc.NameResolver.Args;
import io.grpc.NameResolverProvider;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: LbNameResolverProvider
 * @Author: Fred Feng
 * @Date: 20/12/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class LbNameResolverProvider extends NameResolverProvider {

    private final LbNameResolverFactory lbNameResolverFactory;

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 0;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, Args args) {
        String serviceId = targetUri.getHost();
        return lbNameResolverFactory.getNameResolver(serviceId);
    }

    @Override
    public String getDefaultScheme() {
        return "lb";
    }

}
