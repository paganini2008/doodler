package io.doodler.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Description:
 *
 * @author: Vincent
 * Date: 1/1/2023 1:37 pm
 */
public enum TransType implements EnumConstant {
    /**
     * bet
     */
    BET("bet"),
    WIN("win"),
    CANCEL("cancel"),
    AMEND("amend"),
    DEPOSIT("deposit"),
    WITHDRAWAL("withdrawal"),
    /**
     * refund funds after declined by auditor
     */
    REFUND("refund"),
    TRANS_IN("transIn"),
    TRANS_OUT("transOut"),
    COMMISSION("commission"),
    BONUS("bonus"),
    JACKPOT("jackpot"),
    PROMO("promo"),
    LEADERBOARD("leaderboard"),
    RAKEBACK("rakeback"),
    LEVEL_UP("levelUp"),
    CASHBACK("cashback");

    private final String value;

    TransType(String value) {
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

    public boolean isDebitTrans() {
        switch (this.getValue()) {
            case "bet":
            case "withdrawal":
            case "transIn":
                return true;
            default:
                return false;
        }
    }
    
    @JsonCreator
    public static TransType getBy(String value) {
    	return EnumUtils.valueOf(TransType.class, value);
    }
}
