package io.doodler.common.amqp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.doodler.common.enums.EnumConstant;
import io.doodler.common.enums.EnumUtils;

/**
 * @Description: ApplicationQueue
 * @Author: Fred Feng
 * @Date: 13/04/2023
 * @Version 1.0.0
 */
public enum ApplicationQueue implements EnumConstant {

    USER("crypto-user-service", "crypto.queue.user"),

    UPMS("crypto-upms-service", "crypto.queue.upms"),

    COMMON("crypto-common-service", "crypto.queue.common"),

    GAME("crypto-game-service", "crypto.queue.game"),

    GAMING("crypto-gaming-service", "crypto.queue.gaming"),

    PROMOTION("crypto-promotion-service", "crypto.queue.promotion"),

    PAYMENT("crypto-payment-service", "crypto.queue.payment"),

    AGGREGATION("crypto-aggregation-service", "crypto.queue.aggregation"),

    NEWSLETTER("crypto-newsletter-service", "crypto.queue.newsletter"),
	
	CHAT("crypto-chat-service", "crypto.queue.chat");

    private final String value;
    private final String repr;

    private ApplicationQueue(String value, String repr) {
        this.value = value;
        this.repr = repr;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return repr;
    }

    @JsonCreator
    public static ApplicationQueue forName(String value) {
        return EnumUtils.valueOf(ApplicationQueue.class, value);
    }
}