package io.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Description:
 *
 * @author: Vincent
 * Date: 1/1/2023 1:37 pm
 */
public enum AppName implements EnumConstant {

	AGGREGATION("crypto-aggregation-service", "aggregation"),
    PAYMENT("crypto-payment-service", "payment"),
    GAMING("crypto-gaming-service", "gaming"),
    USER("crypto-user-service", "user"),
    GAME("crypto-game-service", "game"),
    UPMS("crypto-upms-service", "upms"),
    COMMON("crypto-common-service", "common"),
    NEWSLETTER("crypto-newsletter-service", "newsletter"),
    PROMOTION("crypto-promotion-service", "promotion"),
	CHAT("crypto-chat-service", "chat");

    private final String fullName;
    private final String shortName;

    private AppName(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
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

    @Override
    public String toString() {
        return this.fullName;
    }
    
    @JsonCreator
    public static AppName get(String value) {
        return EnumUtils.valueOf(AppName.class, value);
    }
}