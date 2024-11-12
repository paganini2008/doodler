package com.github.doodler.common.email;

import java.util.Map;
import java.util.function.Function;

/**
 * @Description: TextMessageTemplate
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class TextMessageTemplate implements MessageTemplate {

    @Override
    public String loadContent(String templateName, String templateContent, Map<String, Object> kwargs)
            throws Exception {
        return parseText(templateContent, "${", "}", key -> kwargs.get(key));
    }

    protected final String parseText(String text, String prefix, String suffix, Function<String, Object> function) {
        int offset = 0;
        int start = text.indexOf(prefix, offset);
        if (start == -1) {
            return text;
        }
        StringBuilder builder = new StringBuilder();
        final char[] src = text.toCharArray();
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                builder.append(src, offset, start - offset - 1).append(prefix);
                offset = start + prefix.length();
            } else {
                int end = text.indexOf(suffix, start);
                if (end == -1) {
                    builder.append(src, offset, src.length - offset);
                    offset = src.length;
                } else {
                    builder.append(src, offset, start - offset);
                    offset = start + prefix.length();
                    String part = new String(src, offset, end - offset);
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(part)) {
                        Object value = function.apply(part);
                        if (value == null) {
                            value = System.getProperty(part);
                        }
                        builder.append(value);
                    } else {
                        // builder.append(src, start, end - start + suffix.length());
                    }
                    offset = end + suffix.length();
                }
            }
            start = text.indexOf(prefix, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    @Override
    public boolean isHtml() {
        return false;
    }
}