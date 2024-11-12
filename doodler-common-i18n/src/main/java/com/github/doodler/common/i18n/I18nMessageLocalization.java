package com.github.doodler.common.i18n;

import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.FormattedMessageLocalization;
import com.github.doodler.common.context.MessageLocalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: I18nMessageLocalization
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class I18nMessageLocalization implements MessageLocalization {

    private final IRemoteI18nService remoteI18nService;
    private final FormattedMessageLocalization defaultMessageFormatter = new FormattedMessageLocalization();

    @Override
    public String getMessage(String messageKey, Locale locale, String defaultMessage, Object... args) {
        if (StringUtils.isBlank(messageKey)) {
            return defaultMessageFormatter.getMessage(messageKey, locale, defaultMessage, args);
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String message = null;
        try {
            ApiResult<String> result = remoteI18nService.getMessage(DEFAULT_GROUP_NAME, locale.getLanguage(), messageKey);
            if (result.ifPresent()) {
                message = result.getData();
            }
        } catch (Exception e) {
        	if(log.isWarnEnabled()) {
        		log.warn(e.getMessage(), e);
        	}
        }
        if (StringUtils.isBlank(message)) {
            message = defaultMessage;
        }
        if (StringUtils.isNotBlank(message) && ArrayUtils.isNotEmpty(args)) {
        	message = String.format(message, args);
        }
        return message;
    }
}