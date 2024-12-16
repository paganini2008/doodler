package com.github.doodler.common.http;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @Description: HttpComponentProperties
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@ConfigurationProperties("http.components")
@Getter
@Setter
public class HttpComponentProperties {

    private int connectionTimeout = 10 * 1000;
    private int readTimeout = 60 * 1000;
    private Proxy proxy = new Proxy();
    private Okhttp okhttp = new Okhttp();

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

    @Getter
    @Setter
    @ToString
    public static class Proxy {

        private String host;
        private int port;
        private String username;
        private String password;

    }

}
