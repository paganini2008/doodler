package com.github.doodler.common.upms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.RequiredArgsConstructor;

/**
 * @Description: PermissionPersistenceRemoteCaller
 * @Author: Fred Feng
 * @Date: 07/11/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class PermissionPersistenceRemoteCaller {

    private static final String PATH_VARS_PATTERN = "\\{(.*?)\\}";
    
    private final PermissionPersistenceService permissionPersistenceService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        WebApplicationContext applicationContext = (WebApplicationContext) event.getApplicationContext();
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> requestHandlerMapper = mapping.getHandlerMethods();
        RequestMappingInfo info;
        HandlerMethod handlerMethod;
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestHandlerMapper.entrySet()) {
            info = entry.getKey();
            handlerMethod = entry.getValue();
            PermissionPersistences annos = AnnotationUtils.findAnnotation(handlerMethod.getMethod(),
                    PermissionPersistences.class);
            if (annos != null) {
                for (PermissionPersistence anno : annos.value()) {
                	permissionPersistenceService.saveExternalPermissions(transfer2Perms(anno, info));
                }
            } else {
                PermissionPersistence anno = AnnotationUtils.findAnnotation(handlerMethod.getMethod(),
                        PermissionPersistence.class);
                if (anno != null) {
                	permissionPersistenceService.saveExternalPermissions(transfer2Perms(anno, info));
                }
            }
        }
    }

    private List<PermissionPersistenceDto> transfer2Perms(PermissionPersistence anno, RequestMappingInfo info) {
        List<PermissionPersistenceDto> dtos = new ArrayList<>();
        info.getPatternsCondition().getPatterns().forEach(url -> {
            PermissionPersistenceDto dto = new PermissionPersistenceDto();
            dto.setName(StringUtils.isNotBlank(anno.name()) ? anno.name() : anno.perm().toUpperCase());
            dto.setPerm(anno.perm());
            dto.setPath(url.replaceAll(PATH_VARS_PATTERN, "*"));
            dto.setOpType(anno.opType());
            dto.setSuperior(anno.superior());
            dto.setRole(anno.role());
            dtos.add(dto);
        });
        return dtos;
    }
}