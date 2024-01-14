package com.github.doodler.common.security;

/**
 * @Description: SecurityConstants
 * @Author: Fred Feng
 * @Date: 19/07/2020
 * @Version 1.0.0
 */
public abstract class SecurityConstants {

	public static final String LOGIN_AUTHENTICATOR = "doodler-upms-service";
    public static final String MULTIPLE_LOGIN_AUTHENTICATOR = "doodler-user-service";
	
    public static final String SUPER_AMDIN = "sa";
    public static final String ROLE_SUPER_AMDIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_SUPPORTER = "ROLE_SUPPORTER";
    public static final String NA = "N/A";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String AUTHORIZATION_TYPE_BEARER = "Bearer";
    public static final String AUTHORIZATION_TYPE_BASIC = "Basic";
    public static final String ENCRYPTION_TYPE_NOOP = "noop";
    public static final String TOKEN_KEY = "jwt:%s:%s";
    public static final String LOGIN_KEY = "login:%s:%s";
    public static final String PLATFORM_WEBSITE = "website";
    public static final String PLATFORM_ADMIN = "admin";
    public static final String PLATFORM_BOTH = "both";
    public static final String REMEMBER_ME_KEY = "remember-me";

    public static final int PERMISSION_TYPE_MENU = 1;
    public static final int PERMISSION_TYPE_OPERATION = 2;
    public static final String PERMISSION_TYPE_NAME_MENU = "MENU_";
    public static final String PERMISSION_TYPE_NAME_OPERATION = "OPER_";

    public static final String GOOGLE_API_URI = "https://www.googleapis.com";
    public static final String FACEBOOK_API_URI = "https://graph.facebook.com";
    public static final String TWITCH_API_URI = "https://id.twitch.tv";
    public static final String LINE_API_URI = "https://api.line.me";
    
}