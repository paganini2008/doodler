package com.github.doodler.common.security;

import java.util.Collections;
import com.github.doodler.common.security.WhiteListProperties.Mode;
import com.google.common.base.Optional;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

/**
 * @Description: WhiteListBuilderPlugin
 * @Author: Fred Feng
 * @Date: 21/11/2023
 * @Version 1.0.0
 */
public class WhiteListBuilderPlugin implements OperationBuilderPlugin {

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {
    	Optional<WhiteList> controllerAnnotation = context.findControllerAnnotation(WhiteList.class);
    	if (controllerAnnotation.isPresent()) {
            Mode mode = controllerAnnotation.get().mode();
            context.operationBuilder().tags(
                    Collections.singleton(mode.equals(Mode.EXTERNAL) ? "API Friendly" : "Service Friendly"));
        }
        Optional<WhiteList> methodAnnotation = context.findAnnotation(WhiteList.class);
        if (methodAnnotation.isPresent()) {
            Mode mode = methodAnnotation.get().mode();
            context.operationBuilder().tags(
                    Collections.singleton(mode.equals(Mode.EXTERNAL) ? "API Friendly" : "Service Friendly"));
        }
    }
}