package com.github.doodler.common.oauth2;

/**
 * 
 * @Description: OAuth2Constants
 * @Author: Fred Feng
 * @Date: 06/11/2024
 * @Version 1.0.0
 */
public interface OAuth2Constants {

    String DEFAULT_CLIENT_NAME = "OpenAPIClient";

    String DEFAULT_OPENAPI_ENDPOINT_URI = "/oauth2/openapi";

    String DEFAULT_OPENAPI_CALLBACK_ENDPOINT_URI = DEFAULT_OPENAPI_ENDPOINT_URI + "/callback";

    String DEFAULT_OPENAPI_TOKEN_ENDPOINT_URI = DEFAULT_OPENAPI_ENDPOINT_URI + "/token";

}
