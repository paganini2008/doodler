package com.github.doodler.common.i18n;

import lombok.experimental.UtilityClass;
import static com.github.doodler.common.context.MessageLocalization.DEFAULT_GROUP_NAME;
import static com.github.doodler.common.context.MessageLocalization.DEFAULT_LANGUAGE;
import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.context.ApplicationContextUtils;

/**
 * @Description: I18nUtils
 * @Author: Fred Feng
 * @Date: 20/12/2022
 * @Version 1.0.0
 */
@UtilityClass
public class I18nUtils {

    public String getErrorMessage(String lang, ErrorCode errorCode) {
        if (StringUtils.isBlank(errorCode.getMessageKey())) {
            return errorCode.getDefaultMessage();
        }
        String msg;
        try {
            msg = getMessage(lang, errorCode.getMessageKey());
        } catch (Exception e) {
            msg = null;
        }
        if (StringUtils.isBlank(msg)) {
            msg = errorCode.getDefaultMessage();
        }
        return msg;
    }

    public String getMessage(String lang, String code) {
        return getMessage(DEFAULT_GROUP_NAME, lang, code);
    }

    public String getMessage(String group, String lang, String code) {
        if (StringUtils.isBlank(lang)) {
            lang = DEFAULT_LANGUAGE;
        }
        IRemoteI18nService remoteI18nService = ApplicationContextUtils.getBean(IRemoteI18nService.class);
        ApiResult<String> result = remoteI18nService.getMessage(group, lang, code);
        return result.getData();
    }
    
}