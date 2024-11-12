package com.github.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Description:
 *
 * @author: Vincent
 * Date: 18/1/2023 10:34 am
 */
public enum EventType implements EnumConstant {

    LOGIN("common.event.login"),
    LOGOUT("common.event.logout"),
    REGISTER("common.event.register"),
    UNREGISTER("common.event.unregister"),
    LEVEL_UP("common.event.levelUp"),
    GAMING("common.event.gaming"),
    RECENT_GAME("common.event.recentGame"),
    BONUS_GAMING("common.event.bonusGaming"),
    PAYMENT("common.event.payment"),
    RAKEBACK("common.event.rakeback"),
    CASHBACK("common.event.cashback"),
    CLAIM_BONUS("common.event.claimBonus"),
    PROVIDER_MAINTENANCE("common.event.providerMaintenance"),
    PLATFORM_NOTIFICATION("common.event.platformNotification"),
    USER_ONLINE("common.event.userOnline"),

    USER_INACTIVE("common.event.userInactive");

    private final String value;

    EventType(String value) {
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

    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static EventType getBy(String value) {
        return EnumUtils.valueOf(EventType.class, value);
    }
}