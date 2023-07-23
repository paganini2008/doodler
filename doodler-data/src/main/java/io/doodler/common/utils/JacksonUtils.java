package io.doodler.common.utils;

import static io.doodler.common.Constants.DEFAULT_DATE_PATTERN;
import static io.doodler.common.Constants.ISO8601_DATE_TIME_PATTERN;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * @Description: JacksonUtils
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public abstract class JacksonUtils {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDateFormat(new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN))
                .registerModule(getGenericJavaTimeModule())
                .findAndRegisterModules();
    }

    public static ObjectMapper getGenericObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .dateFormat(new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN))
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .postConfigurer(om -> om.registerModule(getGenericJavaTimeModule()).findAndRegisterModules())
                .build();
    }

    public static SimpleModule getGenericJavaTimeModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(ISO8601_DATE_TIME_PATTERN)));
        simpleModule.addDeserializer(LocalDateTime.class, new MutableLocalDateTimeDeserializer(
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(ISO8601_DATE_TIME_PATTERN))));
        simpleModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        simpleModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        // All long values convert to string value for frontend
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(BigDecimal.class, new BigDecimalToStringSerializer(10));
        simpleModule.addSerializer(Double.class, new DoubleToStringSerializer(10));
        return simpleModule;
    }

    public static byte[] toJsonStringBytes(Object object) {
        if (object == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static String toJsonString(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static void writeObject(Writer out, Object object) {
        try {
            mapper.writeValue(out, object);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static void writeObject(OutputStream out, Object object) {
        try {
            mapper.writeValue(out, object);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static <T> T parseJson(byte[] bytes, Class<T> requiredType) {
        if (bytes == null) {
            return null;
        }
        try {
            return mapper.readValue(bytes, requiredType);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static <T> T parseJson(String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static <T> T parseJson(String json, Class<T> requiredType) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, requiredType);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static boolean isJson(String json) {
        JsonFactory jsonFactory = mapper.getFactory();
        try {
            JsonParser jsonParser = jsonFactory.createParser(json);
            while (jsonParser.nextToken() != null) {
                ;
            }
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean containsKeys(String json, String... includedProperties) {
        Map map;
        try {
            map = parseJson(json, HashMap.class);
        } catch (RuntimeException e) {
            return false;
        }
        if (ArrayUtils.isNotEmpty(includedProperties)) {
            return Arrays.stream(includedProperties).allMatch(p -> map.containsKey(p));
        }
        return true;
    }
}