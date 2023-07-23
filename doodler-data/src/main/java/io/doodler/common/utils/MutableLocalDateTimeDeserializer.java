package io.doodler.common.utils;

import static io.doodler.common.Constants.SUPPORTED_DATE_TIME_PATTERNS;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.json.JsonParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

/**
 * @Description: MutableLocalDateTimeDeserializer
 * @Author: Fred Feng
 * @Date: 15/12/2022
 * @Version 1.0.0
 */
public class MutableLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final LocalDateTimeDeserializer prior;
    private final String[] datetimePatterns;
    
    public MutableLocalDateTimeDeserializer(LocalDateTimeDeserializer priorDateTimeDeserializer) {
    	this(priorDateTimeDeserializer, SUPPORTED_DATE_TIME_PATTERNS);
    }

    public MutableLocalDateTimeDeserializer(LocalDateTimeDeserializer priorDateTimeDeserializer,
                                            String... datetimePatterns) {
        this.prior = priorDateTimeDeserializer;
        this.datetimePatterns = datetimePatterns;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        try {
            return prior.deserialize(parser, ctxt);
        } catch (JsonProcessingException e) {
            String value = parser.getText().trim();
            Calendar cal = parseDate(value);
            return cal.toInstant().atZone(cal.getTimeZone().toZoneId()).toLocalDateTime();
        }
    }

    protected Calendar parseDate(String value) {
        Date real;
        try {
            real = DateUtils.parseDate(value, Locale.ENGLISH, datetimePatterns);
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(real);
        return cal;
    }

    public String[] getDatetimePatterns() {
        return datetimePatterns;
    }
}