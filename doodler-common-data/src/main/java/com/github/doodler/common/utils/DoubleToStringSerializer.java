package com.github.doodler.common.utils;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Description: DoubleToStringSerializer
 * @Author: Fred Feng
 * @Date: 16/03/2023
 * @Version 1.0.0
 */
public class DoubleToStringSerializer extends ToStringSerializerBase {

    private static final long serialVersionUID = 6010144556709097414L;

    public DoubleToStringSerializer(int scale) {
        this(scale, RoundingMode.HALF_UP);
    }

    public DoubleToStringSerializer(int scale, RoundingMode roundingMode) {
        super(Double.class);
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    private final int scale;
    private final RoundingMode roundingMode;

    @Override
    public String valueToString(Object value) {
        Double doubleValue = (Double) value;
        BigDecimal newValue = NumberUtil.round(doubleValue, scale, roundingMode);
        newValue = newValue.stripTrailingZeros();
        return newValue.toPlainString();
    }
}