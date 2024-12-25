package com.github.doodler.common.context;

import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @Description: OnNotApplicationCondition
 * @Author: Fred Feng
 * @Date: 08/12/2022
 * @Version 1.0.0
 */
public class OnNotApplicationCondition extends SpringBootCondition {

    private static final ConditionMessage EMPYT_MESSAGE = ConditionMessage.empty();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
            AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes =
                metadata.getAnnotationAttributes(ConditionalOnNotApplication.class.getName());
        String[] applicationNames = (String[]) annotationAttributes.get("value");
        if (applicationNames == null || applicationNames.length == 0) {
            return ConditionOutcome.match(EMPYT_MESSAGE);
        }
        String applicationName =
                context.getEnvironment().getRequiredProperty("spring.application.name");
        if (ArrayUtils.contains(applicationNames, applicationName)) {
            return ConditionOutcome.noMatch(EMPYT_MESSAGE);
        }
        return ConditionOutcome.match(EMPYT_MESSAGE);
    }
}
