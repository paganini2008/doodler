package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Description:
 *
 * @author: Vincent
 * Date: 1/1/2023 1:37 pm
 */
public enum AppName implements EnumConstant {

    AGGREGATION("crypto-aggregation-service", "aggregation", "/agg"),
    PAYMENT("crypto-payment-service", "payment", "/pm"),
    GAMING("crypto-gaming-service", "gaming", "/gd"),
    USER("crypto-user-service", "user", "/website"),
    GAME("crypto-game-service", "game", "/gm"),
    UPMS("crypto-upms-service", "upms", "/upms"),
    COMMON("crypto-common-service", "common", "/common"),
    NEWSLETTER("crypto-newsletter-service", "newsletter", "/news"),
    PROMOTION("crypto-promotion-service", "promotion", "/po"),
    CHAT("crypto-chat-service", "chat", "/chat");

    private final String fullName;
    private final String shortName;
    private final String contextPath;

    private AppName(String fullName, String shortName, String contextPath) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.contextPath = contextPath;
    }

    @Override
    @JsonValue
    public String getValue() {
        return fullName;
    }

    @Override
    public String getRepr() {
        return this.fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String toString() {
        return this.fullName;
    }

    @JsonCreator
    public static AppName get(String value) {
        return EnumUtils.valueOf(AppName.class, value);
    }
}