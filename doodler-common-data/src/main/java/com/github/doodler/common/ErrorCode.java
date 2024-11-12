package com.github.doodler.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: ErrorCode
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public interface ErrorCode {

    String getMessageKey();

    int getCode();

    default String getDefaultMessage() {
        return "";
    }

    default boolean isFatal() {
        return true;
    }

    static ErrorCode jobSchedulerError(Throwable e) {
        return new SimpleErrorCode("JOB_SERVER_ERROR", 9999990, e.getMessage());
    }

    static ErrorCode jobSchedulerError(String msg) {
        return new SimpleErrorCode("JOB_SERVER_ERROR", 9999990, msg);
    }

    static ErrorCode sqlError(String msg) {
        return new SimpleErrorCode("SQL_ERROR", 9999996, msg);
    }

    static ErrorCode sqlError(Throwable e) {
        return new SimpleErrorCode("SQL_ERROR", 9999996, e.getMessage());
    }

    static ErrorCode webServerError(String msg) {
        return new SimpleErrorCode("WEB_SERVER_ERROR", 9999997, msg);
    }

    static ErrorCode webServerError(Throwable e) {
        return new SimpleErrorCode("WEB_SERVER_ERROR", 9999997, e.getMessage());
    }

    static ErrorCode restClientError(Throwable e) {
        return new SimpleErrorCode("REST_CLIENT_ERROR", 9999998, e.getMessage());
    }

    static ErrorCode restClientError(String msg) {
        return new SimpleErrorCode("REST_CLIENT_ERROR", 9999998, msg);
    }

    static ErrorCode internalServerError(Throwable e) {
        return new SimpleErrorCode("INTERNAL_SERVER_ERROR", 9999999, e.getMessage());
    }

    static ErrorCode internalServerError(String msg) {
        return new SimpleErrorCode("INTERNAL_SERVER_ERROR", 9999999, msg);
    }

    static ErrorCode userInsufficientFundError(String msg) {
        return new SimpleErrorCode("USER_INSUFFICIENT_FUND", 6001001,
                StringUtils.isBlank(msg) ? "User Insufficient fund" : msg);
    }

    static ErrorCode fundChangeBusyError(String msg) {
        return new SimpleErrorCode("FUND_CHANGE_BUSY", 6001002,
                StringUtils.isBlank(msg) ? "User fund changing busy, try later" : msg);
    }

    static ErrorCode dataNotFound(String msg) {
        return new SimpleErrorCode("DATA_NOT_FOUND", 9999997,
                StringUtils.isBlank(msg) ? "Data not found" : msg);
    }
}