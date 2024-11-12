package com.github.doodler.common.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.github.doodler.common.ws.client.WsClientConfig;

/**
 * 
 * @Description: EnableWsClient
 * @Author: Fred Feng
 * @Date: 08/01/2023
 * @Version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(WsClientConfig.class)
public @interface EnableWsClient {

}