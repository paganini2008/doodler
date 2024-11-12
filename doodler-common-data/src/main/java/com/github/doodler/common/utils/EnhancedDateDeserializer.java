package com.github.doodler.common.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.json.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;

/**
 * 
 * @Description: EnhancedDateDeserializer
 * @Author: Fred Feng
 * @Date: 10/11/2024
 * @Version 1.0.0
 */
public class EnhancedDateDeserializer extends DateDeserializer {

    private static final long serialVersionUID = -7637052653088816408L;

    private final String[] datetimePatterns;

    public EnhancedDateDeserializer(String... datetimePatterns) {
        this.datetimePatterns = datetimePatterns;
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return super.deserialize(p, ctxt);
        } catch (IOException e) {
            String str = p.getText().trim();
            try {
                return DateUtils.parseDate(str, Locale.ENGLISH, datetimePatterns);
            } catch (ParseException ee) {
                throw new JsonParseException(ee);
            }
        }
    }
}
