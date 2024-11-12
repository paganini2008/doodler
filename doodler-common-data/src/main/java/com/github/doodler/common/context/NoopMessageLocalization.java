package com.github.doodler.common.context;

import java.util.Locale;

/**
 * @Description: NoopMessageLocalization
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class NoopMessageLocalization implements MessageLocalization {

    @Override
    public String getMessage(String messageKey, Locale locale, String defaultMessage, Object... args) {
        return messageKey;
    }
}