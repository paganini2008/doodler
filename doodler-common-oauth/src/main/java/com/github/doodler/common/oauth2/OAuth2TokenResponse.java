package com.github.doodler.common.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @Description: OAuth2TokenResponse
 * @Author: Fred Feng
 * @Date: 04/11/2024
 * @Version 1.0.0
 */
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OAuth2TokenResponse {

    private String access_token;
    private String refresh_token;
    private String scope;
    private String token_type;
    private Long expires_in;
}
