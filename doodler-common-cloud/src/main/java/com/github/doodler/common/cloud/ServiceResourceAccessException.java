package com.github.doodler.common.cloud;

import java.io.IOException;
import java.net.URI;
import org.springframework.web.client.ResourceAccessException;

/**
 * @Description: ServiceResourceAccessException
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
public class ServiceResourceAccessException extends ResourceAccessException {

    private static final long serialVersionUID = 6635894909025962936L;

    public ServiceResourceAccessException(String serviceId, URI uri, String msg) {
        super(msg);
        this.uri = uri;
        this.serviceId = serviceId;
    }

    public ServiceResourceAccessException(String serviceId, URI uri, String msg, IOException ex) {
        super(msg, ex);
        this.serviceId = serviceId;
        this.uri = uri;
    }

    private final String serviceId;
    private final URI uri;

    public String getServiceId() {
        return serviceId;
    }

    public URI getUri() {
        return uri;
    }
}