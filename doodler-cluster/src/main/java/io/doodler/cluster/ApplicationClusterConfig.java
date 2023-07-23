package io.doodler.cluster;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: ApplicationClusterConfig
 * @Author: Fred Feng
 * @Date: 21/07/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan("io.doodler.cluster")
public class ApplicationClusterConfig {

}