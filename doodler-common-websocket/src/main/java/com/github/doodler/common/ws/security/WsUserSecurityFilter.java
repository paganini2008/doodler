package com.github.doodler.common.ws.security;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.GenericFilterBean;
import com.github.doodler.common.BizException;
import com.github.doodler.common.security.ErrorCodes;
import com.github.doodler.common.security.IdentifiableUserDetails;
import com.github.doodler.common.security.InternalAuthenticationToken;
import com.github.doodler.common.security.Visitor;
import com.github.doodler.common.utils.DecryptionUtils;
import com.github.doodler.common.ws.WsServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: WsUserSecurityFilter
 * @Author: Fred Feng
 * @Date: 15/03/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class WsUserSecurityFilter extends GenericFilterBean {

    private final WsServerProperties serverConfig;
    private final UserDetailsService userDetailsService;
    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestPath = httpRequest.getRequestURI();
        if (!requestPath.startsWith("/news/ws/user") && !requestPath.startsWith("/chat/ws/chat")) {
            chain.doFilter(request, response);
            return;
        }

        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }
        requestPath = URLDecoder.decode(requestPath, "UTF-8");
        List<String> pathArgs = new ArrayList<>(Arrays.asList(requestPath.split("\\/", 4)));
        Collections.reverse(pathArgs);
        String cipherText = pathArgs.get(0);
        String rawText = null;
        try {
            String securityKey = serverConfig.getSecurityKey();
            rawText = DecryptionUtils.decryptText(cipherText, securityKey);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to decrypt request path: {}", httpRequest.getRequestURI(), e);
            }
            // throw new BizException(ErrorCodes.BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED, e);
        }
        String identifier = null;
        if (StringUtils.isNotBlank(rawText)) {
            int index;
            if ((index = rawText.indexOf(":")) != -1) {
                identifier = rawText.substring(0, index);
            }
        }
        if (StringUtils.isNotBlank(identifier)) {
            IdentifiableUserDetails user;
            try {
                user = (IdentifiableUserDetails) userDetailsService.loadUserByUsername(identifier);
            } catch (RestClientException e) {
                user = new Visitor();
            }
            try {
                userDetailsChecker.check(user);
                InternalAuthenticationToken authentication = new InternalAuthenticationToken(user,
                        user.getUsername(), user.getPlatform(), false, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new BizException(ErrorCodes.BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED, e);
            }
        }
        chain.doFilter(request, response);
    }
}
