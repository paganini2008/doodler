package com.github.doodler.common.amqp;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

/**
 * @Description: SimpleCachedObject
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
@Accessors(chain = true)
@Setter
@Getter
public class SimpleCachedObject implements RetryCache.CachedObject {

    private Object object;
    private String exchange;
    private String routingKey;
    private long retryAt = -1;

    public SimpleCachedObject() {
    }
    
    public SimpleCachedObject(Object object) {
        Assert.notNull(object, "Nullable object for caching");
        this.object = object;
    }

    public String toString() {
        return "[Retrived Object] Object class: " + object.getClass() + ", Retry at: " + new Date(retryAt);
    }
}