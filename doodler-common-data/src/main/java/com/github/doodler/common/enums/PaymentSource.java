package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Description:
 *
 * @author: Vincent
 * Date: 12/1/2023 5:52 pm
 */
public enum PaymentSource implements EnumConstant {
    /**
     * internal
     */
    INTERNAL("I", "Internal"),
    /**
     * provider
     */
    PROVIDER("P", "Provider"),
    /**
     * payment gateway
     */
    GATEWAY("G", "Gateway");

    private final String value;
    private final String desc;

    PaymentSource(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return this.desc;
    }
    
    @JsonCreator
    public static PaymentSource getBy(String value) {
    	return EnumUtils.valueOf(PaymentSource.class, value);
    }
}
