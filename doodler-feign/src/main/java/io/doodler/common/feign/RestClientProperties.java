package io.doodler.common.feign;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.doodler.common.SecurityKey;

/**
 * @Description: RestClientProperties
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@ConfigurationProperties("feign.client")
@Getter
@Setter
public class RestClientProperties {

    private long connectionTimeout = 10;
    private long readTimeout = 60;
    private boolean followRedirects = true;
    private Okhttp okhttp = new Okhttp();
    private SecurityKey security = new SecurityKey();

    private Instance[] instances;

    @Getter
    @Setter
    @ToString
    public static class Instance {

        private String serviceId;
        private List<String> urls;
        private String lbType;
    }

    @Getter
    @Setter
    @ToString
    public static class Okhttp {

        private boolean poolEnabled = false;
        private long connectionTimeout = 10;
        private long writeTimeout = 60;
        private long readTimeout = 60;
        private int maxIdleConnections = 100;
        private long keepAliveDuration = 60;
    }
}