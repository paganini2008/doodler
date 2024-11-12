package com.github.doodler.common.utils;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.json.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

/**
 * @Description: EnhancedLocalDateTimeDeserializer
 * @Author: Fred Feng
 * @Date: 15/12/2022
 * @Version 1.0.0
 */
public class EnhancedLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

    private static final long serialVersionUID = 6663586266353752649L;

    private final String[] datetimePatterns;

    public EnhancedLocalDateTimeDeserializer(String... datetimePatterns) {
        this.datetimePatterns = datetimePatterns;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        try {
            return super.deserialize(parser, ctxt);
        } catch (JsonProcessingException e) {
            String str = parser.getText().trim();
            Date actualDate;
            try {
                actualDate = DateUtils.parseDate(str, Locale.ENGLISH, datetimePatterns);
            } catch (ParseException ee) {
                throw new JsonParseException(ee);
            }
            return actualDate.toInstant()
                    .atZone(ctxt.getTimeZone() != null ? ctxt.getTimeZone().toZoneId()
                            : ZoneId.systemDefault())
                    .toLocalDateTime();

        }
    }

    public String[] getDatetimePatterns() {
        return datetimePatterns;
    }
}
