package com.github.doodler.common.quartz;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import com.github.doodler.common.quartz.EnableQuartz.Mode;

/**
 * @Description: QuartzIntegratedSelector
 * @Author: Fred Feng
 * @Date: 21/06/2023
 * @Version 1.0.0
 */
public class QuartzIntegratedSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableQuartz.class.getName()));
        Mode mode = annotationAttributes.getEnum("value");
        switch (mode) {
            case EXECUTOR:
                return new String[] {
                        "com.github.doodler.common.quartz.executor.QuartzExecutorConfiguration"};
            case SCHEDULER:
                return new String[] {
                        "com.github.doodler.common.quartz.scheduler.QuartzSchedulerConfiguration"};
        }
        throw new IllegalStateException();
    }
}
