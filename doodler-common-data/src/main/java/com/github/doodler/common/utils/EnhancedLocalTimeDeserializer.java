package com.github.doodler.common.utils;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.json.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;

/**
 * 
 * @Description: EnhancedLocalTimeDeserializer
 * @Author: Fred Feng
 * @Date: 10/11/2024
 * @Version 1.0.0
 */
public class EnhancedLocalTimeDeserializer extends LocalTimeDeserializer {

    private static final long serialVersionUID = 4494558095641868325L;

    private final String[] datetimePatterns;

    public EnhancedLocalTimeDeserializer(String... datetimePatterns) {
        this.datetimePatterns = datetimePatterns;
    }

    @Override
    public LocalTime deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException {
        try {
            return super.deserialize(parser, ctxt);
        } catch (IOException e) {
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
                    .toLocalTime();
        }
    }


}
