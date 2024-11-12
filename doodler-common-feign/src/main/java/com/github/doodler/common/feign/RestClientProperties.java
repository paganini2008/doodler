package com.github.doodler.common.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;
import com.github.doodler.common.SecurityKey;
import lombok.Getter;
import lombok.Setter;

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
    private SecurityKey security = new SecurityKey();


}
