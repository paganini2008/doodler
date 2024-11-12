package com.github.doodler.common.cloud.lb;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @Description: LoadBalancerProperties
 * @Author: Fred Feng
 * @Date: 17/10/2024
 * @Version 1.0.0
 */
@ConfigurationProperties("loadbalancer.client")
@Getter
@Setter
public class LoadBalancerProperties {

    private Instance[] instances;

    @Getter
    @Setter
    @ToString
    public static class Instance {

        private String serviceId;
        private List<String> urls;
        private String lbType;
    }

}
