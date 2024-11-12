package com.github.doodler.common.webmvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.Constants;
import com.github.doodler.common.SecurityKey;
import com.github.doodler.common.context.ENC;
import com.github.doodler.common.context.ServerProperties;
import com.github.doodler.common.context.WebMvcInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * @Description: EncryptableWebMvcInterceptor
 * @Author: Fred Feng
 * @Date: 31/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class EncryptableWebMvcInterceptor implements WebMvcInterceptor {

    private static final Pattern ENC_PATTERN = Pattern.compile("ENC\\('(.*)'\\)");

    private final ServerProperties config;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityKey security = config.getSecurity();
        String cipherText = request.getHeader(Constants.REQUEST_HEADER_ENDPOINT_SECURITY_KEY);
        if (security == null || StringUtils.isBlank(cipherText)) {
            return false;
        }
        Matcher matcher = ENC_PATTERN.matcher(cipherText);
        if (matcher == null || !matcher.find()) {
            return false;
        }
        cipherText = matcher.group(1);
        String securityKey = ENC.decrypt(cipherText, security.getSalt());
        return securityKey.equals(security.getKey());
    }
}