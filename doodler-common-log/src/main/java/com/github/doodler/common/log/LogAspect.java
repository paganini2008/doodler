package com.github.doodler.common.log;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import com.github.doodler.common.utils.Markers;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Value("${spring.application.name}")
    private String applicationName;

    private Marker marker;

    @PostConstruct
    public void configure() {
        this.marker = Markers.forName(applicationName);
    }

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.elraytech.maxibet.common.log.LogAnnotation)")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        LogEntity logEntity = new LogEntity();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        // 注解信息
        LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
        logEntity.setLogType(annotation.operation().toString());
        // 请求信息
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            Map<String, Object> headers = new HashMap<>();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                if (key.contains("authorization")) {
                    headers.put(key, request.getHeader(key));
                }
            }
            logEntity.setHeaders(headers);
        }

        logEntity.setIp(ipAddress);
        logEntity.setMethod(request.getMethod());
        if (request.getMethod().equals(HttpMethod.POST.name())) {
            logEntity.setReq(getParameter(method, joinPoint.getArgs()));
        }
        logEntity.setQuery(request.getQueryString());
        logEntity.setUrl(request.getRequestURL().toString());
        logEntity.setStartTime(startTime);
        logEntity.setDescription(annotation.desc());

        try {
            Object result = joinPoint.proceed();
            logEntity.setRes(result);
            logEntity.setSpendTime((int) (System.currentTimeMillis() - startTime));
            if (log.isInfoEnabled()) {
                log.info(marker, JSONUtil.toJsonStr(logEntity));
            }
            return result;
        } catch (Throwable throwable) {
            logEntity.setRes(throwable.toString());
            logEntity.setSpendTime((int) (System.currentTimeMillis() - startTime));
            if (log.isInfoEnabled()) {
                log.info(marker, JSONUtil.toJsonStr(logEntity));
            }
            throw throwable;
        }
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (args[i] instanceof HttpServletResponse || args[i] instanceof BindingResult
                    || args[i] instanceof ServletRequest || args[i] instanceof ServletResponse
                    || args[i] instanceof MultipartFile) {
                continue;
            }
            // 将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            } else {
                // 将RequestParam注解修饰的参数作为请求参数
                RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    Map<String, Object> map = new HashMap<>();
                    String key = parameters[i].getName();
                    if (!StringUtils.isBlank(requestParam.value())) {
                        key = requestParam.value();
                    }
                    map.put(key, args[i]);
                    argList.add(map);
                } else { // 如果不是 RequestParam 和 RequestBody 注解
                    argList.add(args[i]);
                }
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }
}
