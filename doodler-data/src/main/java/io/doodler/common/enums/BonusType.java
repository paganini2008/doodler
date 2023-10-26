package io.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Description: BonusType
 * @Author: Fred Feng
 * @Date: 07/09/2023
 * @Version 1.0.0
 */
public enum BonusType implements EnumConstant {

    FREE_SPINS(0, "free_spins"),

    DEPOSIT_BONUS(1, "deposit_bonus"),

    CRYPTO_BONUS(2, "crypto_bonus");

    BonusType(Integer value, String repr) {
        this.value = value;
        this.repr = repr;
    }

    private int value;
    private String repr;

    @Override
    @JsonValue
    public Integer getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return this.repr;
    }
    
    @JsonCreator
    public static BonusType getBy(Integer value) {
        return EnumUtils.valueOf(BonusType.class, value);
    }
}