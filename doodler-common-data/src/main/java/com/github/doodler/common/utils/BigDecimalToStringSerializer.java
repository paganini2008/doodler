package com.github.doodler.common.utils;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Description: BigDecimalToStringSerializer
 * @Author: Fred Feng
 * @Date: 16/03/2023
 * @Version 1.0.0
 */
public class BigDecimalToStringSerializer extends ToStringSerializerBase {

    private static final long serialVersionUID = 6898338867143941294L;

    public BigDecimalToStringSerializer(int scale) {
        this(scale, RoundingMode.HALF_UP);
    }

    public BigDecimalToStringSerializer(int scale, RoundingMode roundingMode) {
        super(BigDecimal.class);
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    private final int scale;
    private final RoundingMode roundingMode;

    @Override
    public String valueToString(Object value) {
        BigDecimal bigValue = (BigDecimal) value;
        BigDecimal newValue = NumberUtil.round(bigValue, scale, roundingMode);
        newValue = newValue.stripTrailingZeros();
        return newValue.toPlainString();
    }
}