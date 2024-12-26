package com.github.doodler.common.transmission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import com.github.doodler.common.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: Bucket
 * @Author: Fred Feng
 * @Date: 26/12/2024
 * @Version 1.0.0
 */
@Slf4j
public class Bucket extends HashMap<String, Object> {

    private static final long serialVersionUID = 1821760688638671763L;

    public Bucket() {
        this("default");
    }

    public Bucket(String topic) {
        this(topic, null);
    }

    public Bucket(String topic, String content) {
        setField("timestamp", System.currentTimeMillis());
        setTopic(topic);
        setContent(content);
    }

    public void setTopic(String topic) {
        setField("topic", topic);
    }

    public void setContent(String content) {
        setField("content", content);
    }

    public String getContent() {
        return getField("content", String.class);
    }

    public long getTimestamp() {
        return getField("timestamp", Long.class);
    }

    public boolean hasField(String fieldName) {
        return containsKey(fieldName);
    }


    public void setField(String fieldName, Object value) {
        put(fieldName, value);
    }


    public Object getField(String fieldName) {
        return get(fieldName);
    }


    public <T> T getField(String fieldName, Class<T> requiredType) {
        return ConvertUtils.convert(getField(fieldName), requiredType);
    }


    public void fill(Object object) {
        for (String key : keySet()) {
            try {
                PropertyUtils.setProperty(object, key, get(key));
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    public void append(Map<String, ?> m) {
        putAll(m);
    }

    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(this);
    }

    public Bucket copy() {
        return Bucket.wrap(this);
    }

    public static Bucket wrap(Map<String, ?> kwargs) {
        Bucket bucket = new Bucket();
        bucket.append(kwargs);
        return bucket;
    }
}
