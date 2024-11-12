package com.github.doodler.common.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 
 * @Description: SecureLoginFeatures
 * @Author: Fred Feng
 * @Date: 28/10/2024
 * @Version 1.0.0
 */
public final class SecureLoginFeatures implements ImportSelector, Ordered {

    private static final Map<String, Object> features = new HashMap<>();

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableSecureLogin.class.getName()));
        for (Map.Entry<String, Object> entry : annotationAttributes.entrySet()) {
            features.put(entry.getKey(), entry.getValue());
        }
        return new String[0];
    }

    public static boolean isEnabled(String feature) {
        return features.containsKey(feature) && (Boolean) features.get(feature);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


}
