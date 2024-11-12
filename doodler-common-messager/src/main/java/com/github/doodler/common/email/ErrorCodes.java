package com.github.doodler.common.email;

import com.github.doodler.common.SimpleErrorCode;

/**
 * 
 * @Description: ErrorCodes
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
public abstract class ErrorCodes {

    public static final SimpleErrorCode EMAIL_SETTING_FAULT =
            new SimpleErrorCode("EMAIL_SETTING_FAULT", 9004010, "Email setting faults");

    public static final SimpleErrorCode EMAIL_SENDING_FAULT = new SimpleErrorCode(
            "EMAIL_SENDING_FAULT", 9004011, "Failed to send email in the background");



}
