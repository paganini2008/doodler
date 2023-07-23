package io.doodler.webmvc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.doodler.common.ErrorCode;
import io.doodler.common.ExceptionDescriptor;
import io.doodler.common.context.ApplicationContextUtils;
import io.doodler.common.context.HttpRequestContextHolder;
import io.doodler.common.context.MessageLocalization;
import io.doodler.common.utils.LangUtils;

import static io.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;

import java.util.Locale;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: JacksonExceptionSerializer
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class JacksonExceptionSerializer extends StdSerializer<ExceptionDescriptor> {

    private static final long serialVersionUID = 4369712924277867480L;

    public JacksonExceptionSerializer() {
        super(ExceptionDescriptor.class);
    }

    @Override
    @SneakyThrows
    public void serialize(ExceptionDescriptor e, JsonGenerator gen, SerializerProvider provider) {
        gen.writeStartObject();
        gen.writeObjectField("code", e.getErrorCode().getCode());
        gen.writeStringField("msg", getI18nMessage(e.getErrorCode(), LangUtils.toObjectArray(e.getArg())));
        gen.writeObjectField("data", e.getArg());
        gen.writeStringField("requestPath", HttpRequestContextHolder.getPath());
        String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
        long elapsed = 0;
        if (StringUtils.isNotBlank(timestamp)) {
            elapsed = System.currentTimeMillis() - Long.parseLong(timestamp);
        }
        gen.writeNumberField("elapsed", elapsed);
        gen.writeEndObject();
    }

    private String getI18nMessage(ErrorCode errorCode, Object[] args) {
        Locale locale = HttpRequestContextHolder.getLocale();
        MessageLocalization messageLocalization = ApplicationContextUtils.getBean(MessageLocalization.class);
        return messageLocalization.getMessage(errorCode, locale, args);
    }
}