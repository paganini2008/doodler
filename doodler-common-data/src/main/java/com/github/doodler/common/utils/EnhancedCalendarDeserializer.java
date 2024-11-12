package com.github.doodler.common.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.json.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.CalendarDeserializer;

/**
 * 
 * @Description: EnhancedCalendarDeserializer
 * @Author: Fred Feng
 * @Date: 10/11/2024
 * @Version 1.0.0
 */
public class EnhancedCalendarDeserializer extends CalendarDeserializer {

    private static final long serialVersionUID = 8293091643895886152L;

    private final String[] datetimePatterns;

    public EnhancedCalendarDeserializer(String... datetimePatterns) {
        this.datetimePatterns = datetimePatterns;
    }

    @Override
    public Calendar deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return super.deserialize(p, ctxt);
        } catch (IOException e) {
            String str = p.getText().trim();
            Date date;
            try {
                date = DateUtils.parseDate(str, Locale.ENGLISH, datetimePatterns);
            } catch (ParseException ee) {
                throw new JsonParseException(ee);
            }
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            if (ctxt.getTimeZone() != null) {
                c.setTimeZone(ctxt.getTimeZone());
            }
            return c;
        }
    }



}
