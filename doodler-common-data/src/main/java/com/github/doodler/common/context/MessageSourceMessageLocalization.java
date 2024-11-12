package com.github.doodler.common.context;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

/**
 * @Description: MessageSourceMessageLocalization
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class MessageSourceMessageLocalization implements MessageLocalization {

    private final MessageSource messageSource;

    @Override
    public String getMessage(String messageKey, Locale locale, String defaultMessage, Object... args) {
        return messageSource.getMessage(messageKey, args, defaultMessage, locale);
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }
}