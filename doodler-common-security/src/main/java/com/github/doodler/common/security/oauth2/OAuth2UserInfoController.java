package com.github.doodler.common.security.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.security.WebAppAuthenticationService;

/**
 * 
 * @Description: OAuth2UserInfoController
 * @Author: Fred Feng
 * @Date: 18/10/2024
 * @Version 1.0.0
 */
@RequestMapping("/oauth2")
@RestController
public class OAuth2UserInfoController {

    @Autowired
    private WebAppAuthenticationService authenticationService;


    @GetMapping("/welcome")
    private ApiResult<String> welcome(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authenticationService.signIn((OAuth2AuthenticationToken) authentication,
                null, request, response);
        return ApiResult.ok(token);
    }

}
