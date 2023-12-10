package io.doodler.common.security;

import static io.doodler.common.security.SecurityConstants.MULTIPLE_LOGIN_AUTHENTICATOR;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;

import io.doodler.common.context.ConditionalOnApplication;

import io.doodler.common.security.facebook.FacebookAuthenticationToken;
import io.doodler.common.security.facebook.FacebookClientApiService;
import io.doodler.common.security.google.GoogleAuthenticationToken;
import io.doodler.common.security.google.GoogleClientApiService;
import io.doodler.common.security.line.LineAuthenticationToken;
import io.doodler.common.security.line.LineClientApiService;
import io.doodler.common.security.twitch.TwitchAuthenticationToken;
import io.doodler.common.security.twitch.TwitchClientApiService;

/**
 * @Description: SocialAccountService
 * @Author: Fred Feng
 * @Date: 21/02/2023
 * @Version 1.0.0
 */
@ConditionalOnApplication(applicationNames = MULTIPLE_LOGIN_AUTHENTICATOR)
@Component
public class SocialAccountService {

    @Autowired
    private GoogleClientApiService googleClientApiService;

    @Autowired
    private FacebookClientApiService facebookClientApiService;

    @Autowired
    private TwitchClientApiService twitchClientApiService;

    @Autowired
    private LineClientApiService lineClientApiService;

    @Autowired
    private AuthenticationService authenticationService;

    public String login(SocialAccountType socialAccountType, String identifier, String accessToken, HttpServletRequest request,
                        HttpServletResponse response) {
        AbstractAuthenticationToken authToken;
        switch (socialAccountType) {
            case GOOGLE:
                authToken = new GoogleAuthenticationToken(identifier, accessToken, socialAccountType.getValue());
                break;
            case FACEBOOK:
                authToken = new FacebookAuthenticationToken(identifier, accessToken, socialAccountType.getValue());
                break;
            case TWITCH:
                authToken = new TwitchAuthenticationToken(identifier, accessToken, socialAccountType.getValue());
                break;
            case LINE:
                authToken = new LineAuthenticationToken(identifier, accessToken, socialAccountType.getValue());
                break;
            default:
                throw new IllegalStateException();
        }
        return authenticationService.signIn(authToken, null, request, response);
    }

    public UserProfile getUserProfile(SocialAccountType socialAccountType, String accessToken) {
        switch (socialAccountType) {
            case GOOGLE:
                return googleClientApiService.getUserInfo(accessToken);
            case FACEBOOK:
                return facebookClientApiService.getUserInfo(accessToken);
            case TWITCH:
                return twitchClientApiService.getUserInfo(accessToken);
            case LINE:
                return lineClientApiService.getUserInfo(accessToken);
        }
        throw new IllegalStateException();
    }
}