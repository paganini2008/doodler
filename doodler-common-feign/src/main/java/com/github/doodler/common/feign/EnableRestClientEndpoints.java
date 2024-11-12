package com.github.doodler.common.feign;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.github.doodler.common.feign.statistics.RestClientStatisticAutoConfiguration;

/**
 * @Description: EnableRestClientEndpoints
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RestClientPackageScanningRegistrar.class, RestClientCandidatesRegistrar.class,
        RestClientCandidatesAutoConfiguration.class, RestClientStatisticAutoConfiguration.class})
public @interface EnableRestClientEndpoints {

    String[] basePackages() default {};
}