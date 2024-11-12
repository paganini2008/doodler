package com.github.doodler.common.context;

import java.util.Locale;
import com.github.doodler.common.ErrorCode;

/**
 * @Description: MessageLocalization
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public interface MessageLocalization {

    String DEFAULT_LANGUAGE = "en";
    String DEFAULT_GROUP_NAME = "default";

    default String getMessage(ErrorCode errorCode, Locale locale, Object... args) {
        return getMessage(errorCode.getMessageKey(), locale, errorCode.getDefaultMessage(), args);
    }

    String getMessage(String messageKey, Locale locale, String defaultMessage, Object... args);
}