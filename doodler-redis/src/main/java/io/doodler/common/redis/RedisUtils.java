package io.doodler.common.redis;

import java.time.Duration;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.doodler.common.Constants;
import io.doodler.common.enums.AppName;

/**
 * Description:
 * Author: Vincent
 * Date: 2022/11/21 4:58 下午
 */
@Component
@Slf4j
public class RedisUtils {

    private static RedisTemplateHolder redisTemplateHolder;
    private static final Duration MY_DURATION = Duration.ofMinutes(40);

    private static String applicationName;

    @Autowired
    public void setRedisTemplate(RedisTemplateHolder redisTemplateHolder,
                                 @Value("${spring.application.name}") String applicationName) {
        RedisUtils.redisTemplateHolder = redisTemplateHolder;
        RedisUtils.applicationName = applicationName;
    }

    private static String getRealKey(String key, String appName) {
    	String prefix = String.format(Constants.REDIS_CACHE_NAME_PREFIX_PATTERN, appName);
    	if(key.startsWith(prefix)) {
    		return key;
    	}
        return prefix + key;
    }

    private static String getRealKey(String key) {
        return getRealKey(key, applicationName);
    }

    /**
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        set(getRealKey(key), value, MY_DURATION);
    }

    /**
     * @param key
     * @param value
     * @param duration
     */
    public static void set(String key, Object value, Duration duration) {
        set(getRealKey(key), value, duration, true);
    }

    /**
     * @param key
     * @param value
     * @param duration
     */
    public static void set(String key, AppName appName, Object value, Duration duration) {
        set(getRealKey(key, appName.getValue()), value, duration, true);
    }

    /**
     * @param key
     * @param appName
     * @param value
     */
    public static void set(String key, AppName appName, Object value) {
        set(getRealKey(key, appName.getValue()), value, MY_DURATION, true);
    }

    /**
     * @param key
     * @param value
     * @param duration
     * @param compressed
     */
    public static void set(String key, Object value, Duration duration, boolean compressed) {
        try {
            getRedisTemplate(compressed).opsForValue().set(key, value, duration);
        } catch (Exception e) {
            log.warn("Set Cache Error:{}", e.getMessage());
        }
    }

    /**
     * @param key
     * @return
     */
    public static Object get(String key) {
        return get(getRealKey(key), true);
    }

    /**
     * @param key
     * @param appName
     * @return
     */
    public static Object get(String key, AppName appName) {
        return get(getRealKey(key, appName.getValue()), true);
    }

    /**
     * @param key
     * @param compressed
     * @return
     */
    public static Object get(String key, boolean compressed) {
        try {
            if (StringUtils.isEmpty(key)) {
                return null;
            }
            return getRedisTemplate(compressed).opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Get Cache Error:{}", e.getMessage());
            return null;
        }
    }

    public static Boolean del(String key) {
        return del(getRealKey(key), true);
    }

    public static Boolean del(String key, AppName appName) {
        return del(getRealKey(key, appName.getValue()), true);
    }

    /**
     * 删除属性
     */
    public static Boolean del(String key, boolean compressed) {
        if (StringUtils.isEmpty(key)) {
            return true;
        }
        try {
            return getRedisTemplate(compressed).delete(key);
        } catch (Exception e) {
            log.warn("Remove Cache Error:{}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断是否有该属性
     */
    public static Boolean hasKey(String key, boolean compressed) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        try {
            return getRedisTemplate(compressed).hasKey(key);
        } catch (Exception e) {
            log.warn("Cache HasKey Error:{}", e.getMessage());
        }
        return false;
    }

    public static Boolean hasKey(String key) {
        return hasKey(getRealKey(key), true);
    }

    public static Boolean hasKey(String key, AppName appName) {
        return hasKey(getRealKey(key, appName.getValue()), true);
    }

    /**
     * 从redis中获取key对应的过期时间;
     * 如果该值有过期时间，就返回相应的过期时间;
     * 如果该值没有设置过期时间，就返回-1;
     * 如果没有该值，就返回-2;
     *
     * @param key
     * @return
     */
    public static Long expire(String key) {
        return expire(getRealKey(key), true);
    }

    public static Long expire(String key, AppName appName) {
        return expire(getRealKey(key, appName.getValue()), true);
    }

    public static Long expire(String key, boolean compressed) {
        if (!hasKey(key, compressed)) {
            return -1L;
        }
        return getRedisTemplate(compressed).opsForValue().getOperations().getExpire(key);
    }

    public static void fuzzyDel(String key, AppName appName) {
        if (StringUtils.isBlank(key) || key.length() < 4) {
            return;
        }
        Set<String> keys = getRedisTemplate(true).keys(key + "*");
        if (keys != null) {
            keys.forEach(x -> {
                del(x, appName);
            });
        }
    }
    
    private static RedisTemplate<String, Object> getRedisTemplate(boolean compressed) {
        return compressed ? redisTemplateHolder.getJackson2JsonRedisTemplate() :
                redisTemplateHolder.getJackson2JsonRedisTemplate();
    }
}