package com.github.doodler.common.security;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.ExceptionDescriptor;
import com.github.doodler.common.SimpleErrorCode;

/**
 * @Description: ErrorCodes
 * @Author: Fred Feng
 * @Date: 18/11/2022
 * @Version 1.0.0
 */
public abstract class ErrorCodes {

    public static final SimpleErrorCode UNAUTHORIZED =
            new SimpleErrorCode("UNAUTHORIZED", 1004005, "Unauthorized request");
    public static final SimpleErrorCode FORBIDDEN =
            new SimpleErrorCode("FORBIDDEN", 1004006, "Forbidden request");
    public static final SimpleErrorCode INVALID =
            new SimpleErrorCode("INVALID", 1004007, "Invalid request");
    public static final SimpleErrorCode BAD_CREDENTIALS =
            new SimpleErrorCode("BAD_CREDENTIALS", 1004008, "Invalid username or password");
    public static final SimpleErrorCode BAD_CLIENT_CREDENTIALS =
            new SimpleErrorCode("BAD_CLIENT_CREDENTIALS", 104009, "Bad client credentials");
    public static final SimpleErrorCode ACCESS_DENIED =
            new SimpleErrorCode("ACCESS_DENIED", 1004010, "Your request is denied");
    public static final SimpleErrorCode ACCOUNT_EXPIRED =
            new SimpleErrorCode("ACCOUNT_EXPIRED", 1004011, "Your account is expired");
    public static final SimpleErrorCode CREDENTIALS_EXPIRED =
            new SimpleErrorCode("CREDENTIALS_EXPIRED", 1004012, "Your credential is expired");
    public static final SimpleErrorCode INSUFFICIENT_AUTHORITY =
            new SimpleErrorCode("INSUFFICIENT_AUTHORITY", 1004013, "Insufficient authority");
    public static final SimpleErrorCode ACCOUNT_DISABLED =
            new SimpleErrorCode("ACCOUNT_DISABLED", 1004014, "Your account is disabled");
    public static final SimpleErrorCode ACCOUNT_LOCKED =
            new SimpleErrorCode("ACCOUNT_LOCKED", 1004015, "Your account is locked");

    public static final SimpleErrorCode USER_NOT_FOUND =
            new SimpleErrorCode("USER_NOT_FOUND", 1004016, "User detail is not found");

    public static final SimpleErrorCode USER_NOT_FOUND_FOR =
            new SimpleErrorCode("USER_NOT_FOUND", 1004016, "User detail is not found for: %s");

    public static final SimpleErrorCode SERVICE_NOT_AVAILABLE =
            new SimpleErrorCode("SERVICE_NOT_AVAILABLE", 1004017, "Service is unavailable");

    public static final SimpleErrorCode TOTP_CODE_MISMATCHED = new SimpleErrorCode(
            "TOTP_CODE_MISMATCHED", 1004018, "Error, the 2FA code is incorrect");

    public static final SimpleErrorCode REMEMBER_ME_NOT_AVAILABLE = new SimpleErrorCode(
            "REMEMBER_ME_NOT_AVAILABLE", 1004019, "Remember-me function is unavailable");

    public static final SimpleErrorCode JWT_TOKEN_EXPIRATION =
            new SimpleErrorCode("JWT_TOKEN_EXPIRATION", 1004020, "Jwt token is expired", false);

    public static final SimpleErrorCode JWT_TOKEN_BAD_FORMAT =
            new SimpleErrorCode("JWT_TOKEN_BAD_FORMAT", 1004021, "Jwt token has a bad format '%s'");

    public static final SimpleErrorCode NONE_USER_AUTHORIZATION =
            new SimpleErrorCode("NONE_USER_AUTHORIZATION", 1004022,
                    "Current authorization comes from a none-login user or anonymous user");

    public static final SimpleErrorCode SOCIAL_ACCOUNT_AUTHORIZATION_FAILURE =
            new SimpleErrorCode("SOCIAL_ACCOUNT_AUTHORIZATION_FAILURE", 1004023,
                    "Failed to login to third-party social platform");

    public static final SimpleErrorCode JWT_TOKEN_INVALID = new SimpleErrorCode("JWT_TOKEN_INVALID",
            1004020, "Jwt token is invalid due to reason '%s'", false);

    public static ErrorCode matches(AuthenticationException e) {
        if (e instanceof ExceptionDescriptor) {
            return ((ExceptionDescriptor) e).getErrorCode();
        }
        if (e.getCause() instanceof ExceptionDescriptor) {
            return ((ExceptionDescriptor) e.getCause()).getErrorCode();
        }

        if (e instanceof AccountExpiredException) {
            return ACCOUNT_EXPIRED;
        } else if (e instanceof CredentialsExpiredException) {
            return CREDENTIALS_EXPIRED;
        } else if (e instanceof InsufficientAuthenticationException) {
            return INSUFFICIENT_AUTHORITY;
        } else if (e instanceof DisabledException) {
            return ACCOUNT_DISABLED;
        } else if (e instanceof LockedException) {
            return ACCOUNT_LOCKED;
        } else if (e instanceof UsernameNotFoundException) {
            return USER_NOT_FOUND;
        } else if (e instanceof BadCredentialsException) {
            return BAD_CREDENTIALS;
        } else if (e instanceof RememberMeAuthenticationException) {
            return REMEMBER_ME_NOT_AVAILABLE;
        } else if (e instanceof AuthenticationServiceException) {
            return SERVICE_NOT_AVAILABLE;
        } else if (e instanceof JwtAuthenticationException) {
            return JWT_TOKEN_EXPIRATION;
        }
        return UNAUTHORIZED;
    }
}
