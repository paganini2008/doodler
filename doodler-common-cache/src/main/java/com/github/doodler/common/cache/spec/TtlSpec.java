package com.github.doodler.common.cache.spec;

import com.github.doodler.common.annotations.TtlUnit;
import lombok.Getter;
import lombok.ToString;

/**
 * @Description: TtlSpec
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
@Getter
@ToString
public class TtlSpec {

    private final long expiration;
    private final TtlUnit ttlUnit;

    public TtlSpec(long expiration, TtlUnit ttlUnit) {
        this.expiration = expiration;
        this.ttlUnit = ttlUnit;
    }
}