package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Description:
 *
 * @author: Vincent
 * Date: 12/1/2023 5:52 pm
 */
public enum PaymentStatus implements EnumConstant {

    /**
     * creating
     */
    CREATING("creating"),

    /**
     * created
     */
    CREATED("created"),
    /**
     * processing
     */
    PROCESSING("processing"),
    /**
     * approved withdrawal
     */
    APPROVED("approved"),

    /**
     * succeed
     */
    COMPLETED("completed"),
    /**
     * cancelled
     */
    CANCELLED("cancelled"),
    /**
     * declined
     */
    DECLINED("declined"),

    /**
     * ERROR
     */
    ERROR("error");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return this.value;
    }

    public boolean isProcessed() {
        switch (this.getValue()) {
            case "completed":
            case "approved":
            case "cancelled":
            case "declined":
                return true;
            default:
                return false;
        }
    }

    @JsonCreator
    public static PaymentStatus getBy(String value) {
        return EnumUtils.valueOf(PaymentStatus.class, value);
    }
}