package com.github.doodler.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Description: 数字处理工具类
 * Author: Vincent
 * Date: 2021/04/17 19:33:52
 */
public class MathUtil {

    private static final String DEFAULT_DIGITS = "0";
    private static final String FIRST_DEFAULT_DIGITS = "1";

    /**
     * @param target 目标数字
     * @param length 需要补充到的位数, 补充默认数字[0], 第一位默认补充[1]
     * @return 补充后的结果
     */
    public static String makeUpNewData(String target, int length) {
        return makeUpNewData(target, length, DEFAULT_DIGITS);
    }

    /**
     * @param target 目标数字
     * @param length 需要补充到的位数
     * @param add    需要补充的数字, 补充默认数字[0], 第一位默认补充[1]
     * @return 补充后的结果
     */
    public static String makeUpNewData(String target, int length, String add) {
        if (target.startsWith("-")) {
            target.replace("-", "");
        }
        if (target.length() >= length) {
            return target.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(FIRST_DEFAULT_DIGITS);
        for (int i = 0; i < length - (1 + target.length()); i++) {
            sb.append(add);
        }
        return sb.append(target).toString();
    }

    public static BigDecimal bigDecimalAdd(BigDecimal add1, BigDecimal add2) {
        if (add1 == null) {
            return add2;
        }
        if (add2 == null) {
            return add1;
        }
        return add1.add(add2);
    }

    public static BigDecimal bigDecimalSubtract(BigDecimal subtract1, BigDecimal subtract2) {
        if (subtract1 == null) {
            return subtract2;
        }
        if (subtract2 == null) {
            return subtract1;
        }
        return subtract1.subtract(subtract2);
    }

    public static BigDecimal bigDecimalMultiply(BigDecimal b1, BigDecimal b2) {
        if (b1 == null || b2 == null || b2.compareTo(BigDecimal.ONE) == 0) {
            return b1;
        }
        return b1.multiply(b2);
    }

    public static BigDecimal bigDecimalDivide(BigDecimal b1, BigDecimal b2) {
        if (b1 == null || zeroBigDecimal(b2) || b2.compareTo(BigDecimal.ONE) == 0) {
            return b1;
        }
        return b1.divide(b2, 10, RoundingMode.DOWN);
    }

    public static boolean zeroBigDecimal(BigDecimal b1) {
        return b1 == null || b1.compareTo(BigDecimal.ZERO) == 0;
    }

    public static BigDecimal fiatFormat(BigDecimal b) {
        return b == null ? null : b.setScale(2, RoundingMode.DOWN);
    }
}