package com.github.doodler.common.context;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 
 * @Description: RequestContextUtils
 * @Author: Fred Feng
 * @Date: 30/10/2024
 * @Version 1.0.0
 */
@ConditionalOnWebApplication(type = Type.SERVLET)
@Component
public class RequestContextUtils {

    private static final RequestContextHolder contextHolder = new RequestContextHolder();

    static class RequestContextHolder {

        DispatcherServlet dispatcherServlet;

        public DispatcherServlet getDispatcherServlet() {
            Assert.notNull(dispatcherServlet, "Nullable DispatcherServlet.");
            return dispatcherServlet;
        }

    }

    @Autowired
    public void configure(DispatcherServlet dispatcherServlet) {
        contextHolder.dispatcherServlet = dispatcherServlet;
    }

    public static boolean isHandlerMethod(HttpServletRequest request) throws Exception {
        Map<String, HandlerMapping> requestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                contextHolder.getDispatcherServlet().getWebApplicationContext(),
                HandlerMapping.class, true, false);
        for (HandlerMapping handlerMapping : requestMappings.values()) {
            try {
                HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
                if (handlerExecutionChain != null && handlerExecutionChain.getHandler() instanceof HandlerMethod) {
                    return true;
                }
            } catch (RuntimeException ignored) {
                break;
            }
        }
        return false;
    }

}
