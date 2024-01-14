package com.github.doodler.common;

/**
 * @Description: Constants
 * @Author: Fred Feng
 * @Date: 21/03/2020
 * @Version 1.0.0
 */
public interface Constants {

	String PROJECT_NAME = "Spring Application Demo";
    String VERSION = "1.0.0-SNAPSHOT";
    String DEFAULT_CLUSTER_NAME = "default";

    int SERVER_PORT_START_WITH = 39000;
    int SERVER_PORT_END_WITH = 40000;
    String REQUEST_HEADER_REQUEST_ID = "__request_id__";
    String REQUEST_HEADER_TIMESTAMP = "__timestamp__";
    String REQUEST_HEADER_TRACES = "__traces__";
    String REQUEST_HEADER_TRACE_ID = "__trace_id__";
    String REQUEST_HEADER_SPAN_ID = "__span_id__";
    String REQUEST_HEADER_PARENT_SPAN_ID = "__parent_span_id__";
    String REQUEST_HEADER_API_REALM = "__api__";
    
    long DEFAULT_MAXIMUM_RESPONSE_TIME = 3L * 1000;
    
    String REQUEST_HEADER_ENDPOINT_SECURITY_KEY = "ENDPOINT_SECURITY_KEY";
    String REQUEST_HEADER_REST_CLIENT_SECURITY_KEY = "REST_CLIENT_SECURITY_KEY";
    
    String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String[] SUPPORTED_DATE_TIME_PATTERNS = {
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.S",
            "yyyy-MM-dd'T'HH:mm:ss.SXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy",
            "dd/MMM/yyyy",
            "yyyyMMddHHmmss",
            "yyyyMMdd",
            "yyyy-MM-dd HH:mm:ss.SSS"
    };
    
    String URL_PATTERN_PING = "%s://%s:%d%s/ping";

    Integer CURRENCY_RATE_CACHE_MINUTES = 10;
    String CACHE_KEY_CURRENCY_RATE = "currency_rate_%s_%s";
    String CACHE_KEY_USER_ID = "cache_user_id_%s";

    String REDIS_CACHE_NAME_PREFIX_PATTERN = DEFAULT_CLUSTER_NAME + ":%s:";
    String ENC_PATTERN = "ENC('%s')";

    String DEFAULT_SERVER_SECURITY_KEY = "5h0E5GZ3DhAJTSQOFQhlxEZEgJYFjdQz";
    String REQUEST_INFO_CHECKING_PERMISSIONS = "permissions";
    String PLATFORM_WEBSITE = "website";
    String PLATFORM_ADMIN = "admin";

    String USER_ATTRIBUTE_MY_PROFILE = "MY_PROFILE";
    String PUBSUB_CHANNEL_FANOUT_CHAT_ENABLED = "FANOUT-CHAT-ENABLED";
    
    String REDIS_KEY_PATTERN_MAINTENANCE = "maintenance";
}