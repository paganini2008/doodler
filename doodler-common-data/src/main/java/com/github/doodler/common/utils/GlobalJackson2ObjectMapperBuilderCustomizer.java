package com.github.doodler.common.utils;

import static com.github.doodler.common.Constants.ISO8601_DATE_TIME_PATTERN;
import java.text.SimpleDateFormat;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * @Description: GlobalJackson2ObjectMapperBuilderCustomizer
 * @Author: Fred Feng
 * @Date: 31/10/2024
 * @Version 1.0.0
 */
public class GlobalJackson2ObjectMapperBuilderCustomizer
        implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.dateFormat(new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN))
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .postConfigurer(om -> om.registerModule(JacksonUtils.getJavaTimeModuleForWebMvc())
                        .findAndRegisterModules());
    }

}
