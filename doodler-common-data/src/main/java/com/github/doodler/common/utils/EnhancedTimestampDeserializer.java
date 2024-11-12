package com.github.doodler.common.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.json.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.TimestampDeserializer;

/**
 * 
 * @Description: EnhancedTimestampDeserializer
 * @Author: Fred Feng
 * @Date: 10/11/2024
 * @Version 1.0.0
 */
public class EnhancedTimestampDeserializer extends TimestampDeserializer {

    private static final long serialVersionUID = -1827086290100584568L;

    private final String[] datetimePatterns;

    public EnhancedTimestampDeserializer(String... datetimePatterns) {
        this.datetimePatterns = datetimePatterns;
    }

    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return super.deserialize(p, ctxt);
        } catch (IOException e) {
            String str = p.getText().trim();
            try {
                return new Timestamp(Long.parseLong(str));
            } catch (RuntimeException ee) {
                try {
                    Date date = DateUtils.parseDate(str, Locale.ENGLISH, datetimePatterns);
                    return new Timestamp(date.getTime());
                } catch (ParseException eee) {
                    throw new JsonParseException(eee);
                }
            }
        }
    }
}
