package com.github.doodler.common.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

/**
 * @Description: AuthenticationService
 * @Author: Fred Feng
 * @Date: 30/11/2022
 * @Version 1.0.0
 */
public interface AuthenticationService {

    /**
     * Sign in by username and password
     *
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    default String signIn(String username, String password, HttpServletRequest request,
                          HttpServletResponse response) {
        return signIn(username, password, null, request, response);
    }

    /**
     * Sign in by username and password
     *
     * @param username
     * @param password
     * @param postHandler
     * @param request
     * @param response
     * @return
     */
    default String signIn(String username, String password, AuthenticationPostHandler postHandler,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        return signIn(SecurityConstants.SUPER_AMDIN.equals(username) ? new SuperAdminAuthenticationToken(password) :
                new UsernamePasswordAuthenticationToken(username, password), postHandler, request, response);
    }

    /**
     * Sign in by authenticationToken
     *
     * @param authenticationToken
     * @param postHandler
     * @param request
     * @param response
     * @return
     */
    String signIn(AbstractAuthenticationToken authenticationToken, AuthenticationPostHandler postHandler,
                  HttpServletRequest request,
                  HttpServletResponse response);

    /**
     * Sign in by username and password and remember me
     *
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    default String signInAndRememberMe(String username, String password, HttpServletRequest request,
                                       HttpServletResponse response) {
        return signInAndRememberMe(username, password, null, request, response);
    }

    /**
     * Sign in by username and password and remember me
     *
     * @param username
     * @param password
     * @param postHandler
     * @param request
     * @param response
     * @return
     */
    default String signInAndRememberMe(String username, String password, AuthenticationPostHandler postHandler,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        return signInAndRememberMe(
                SecurityConstants.SUPER_AMDIN.equals(username) ? new SuperAdminAuthenticationToken(password) :
                        new UsernamePasswordAuthenticationToken(username, password), postHandler, request, response);
    }

    /**
     * Sign in by authenticationToken and Remember me
     *
     * @param authenticationToken
     * @param postHandler
     * @param request
     * @param response
     * @return
     */
    String signInAndRememberMe(AbstractAuthenticationToken authenticationToken, AuthenticationPostHandler postHandler,
                               HttpServletRequest request,
                               HttpServletResponse response);

    /**
     * authenticate by username and password
     *
     * @param username
     * @param password
     * @return
     */
    default IdentifiableUserDetails authenticate(String username, String password) {
        return authenticate(SecurityConstants.SUPER_AMDIN.equals(username) ? new SuperAdminAuthenticationToken(password) :
                new UsernamePasswordAuthenticationToken(username, password));
    }

    /**
     * authenticate
     *
     * @param authenticationToken
     * @return
     */
    IdentifiableUserDetails authenticate(AbstractAuthenticationToken authenticationToken) throws AuthenticationException;

    /**
     * Sign out
     *
     * @param request
     * @param response
     */
    void signOut(HttpServletRequest request, HttpServletResponse response);
}