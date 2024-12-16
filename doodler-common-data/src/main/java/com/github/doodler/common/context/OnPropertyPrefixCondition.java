package com.github.doodler.common.context;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 
 * @Description: OnPropertyPrefixCondition
 * @Author: Fred Feng
 * @Date: 30/11/2024
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class OnPropertyPrefixCondition extends SpringBootCondition {

    private static final ConditionMessage EMPYT_MESSAGE = ConditionMessage.empty();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final String prefix = (String) metadata.getAnnotationAttributes(ConditionalOnPropertyPrefix.class.getName()).get(
                "value");
        if (StringUtils.isBlank(prefix)) {
            return ConditionOutcome.noMatch(EMPYT_MESSAGE);
        }
        ((ConfigurableEnvironment) context.getEnvironment()).getPropertySources();

        boolean result = ((ConfigurableEnvironment) context.getEnvironment()).getPropertySources().stream().filter(
                p -> p instanceof MapPropertySource).anyMatch(
                        propertySource -> matchPrefix(prefix, propertySource));
        return result ? ConditionOutcome.match(EMPYT_MESSAGE) : ConditionOutcome.noMatch(EMPYT_MESSAGE);
    }

    private boolean matchPrefix(final String prefix, PropertySource<?> propertySource) {
        if (propertySource.getSource() instanceof Map) {
            return ((Map) propertySource.getSource()).keySet().stream().anyMatch(
                    key -> key.toString().startsWith(prefix));
        }
        return false;
    }

}
