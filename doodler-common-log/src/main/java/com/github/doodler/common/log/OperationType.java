package com.github.doodler.common.log;

/**
 * 
 * @Description: OperationType
 * @Author: Fred Feng
 * @Date: 12/11/2024
 * @Version 1.0.0
 */
public enum OperationType {
    /**
     * Create
     */
    CREATE("Create"),
    /**
     * Retrieve
     */
    RETRIEVE("Retrieve"),
    /**
     * Update
     */
    UPDATE("Update"),
    /**
     * Delete
     */
    DELETE("Delete"),
    /**
     * Signin
     */
    SIGNIN("Signin"),
    /**
     * Singup
     */
    SIGNUP("Singup"),
    /**
     * Signout
     */
    SIGNOUT("Signout"),

    /**
     * Gaming
     */
    GAMING("Gaming"),
    /**
     * Paying
     */
    PAYING("Paying");

    private final String describe;

    OperationType(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }
}
