package com.github.doodler.common.security;

import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 
 * @Description: OnSecureLoginCondition
 * @Author: Fred Feng
 * @Date: 28/10/2024
 * @Version 1.0.0
 */
public class OnSecureLoginCondition extends SpringBootCondition {

    private static final ConditionMessage EMPYT_MESSAGE = ConditionMessage.empty();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
            AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes =
                metadata.getAnnotationAttributes(ConditionalOnSecureLogin.class.getName());
        String feature = (String) annotationAttributes.get("value");
        if (SecureLoginFeatures.isEnabled(feature)) {
            return ConditionOutcome.match(EMPYT_MESSAGE);
        }
        return ConditionOutcome.noMatch(EMPYT_MESSAGE);
    }
}
