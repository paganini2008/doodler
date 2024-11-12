package com.github.doodler.common.context;

import java.util.Locale;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: FormattedMessageLocalization
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class FormattedMessageLocalization implements MessageLocalization {

    @Override
    public String getMessage(String messageKey, Locale locale, String defaultMessage, Object... args) {
        if (StringUtils.isNotBlank(defaultMessage) && ArrayUtils.isNotEmpty(args)) {
            return String.format(defaultMessage, args);
        }
        return defaultMessage;
    }
}