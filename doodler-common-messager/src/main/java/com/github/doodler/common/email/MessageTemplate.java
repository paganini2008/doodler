package com.github.doodler.common.email;

import java.util.Map;

/**
 * @Description: MessageTemplate
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public interface MessageTemplate {

    String loadContent(String templateName, String templateContent, Map<String, Object> kwargs) throws Exception;

    default boolean isHtml() {
        return true;
    }
}