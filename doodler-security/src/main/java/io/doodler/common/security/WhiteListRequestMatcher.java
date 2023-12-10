package io.doodler.common.security;

import io.doodler.common.Constants;
import io.doodler.common.SecurityKey;
import io.doodler.common.context.ENC;
import io.doodler.common.utils.WebUtils;

import io.doodler.common.security.WhiteListProperties.Mode;
import io.doodler.common.security.WhiteListProperties.WhiteListInfo;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @Description: WhiteListRequestMatcher
 * @Author: Fred Feng
 * @Date: 19/11/2022
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class WhiteListRequestMatcher implements RequestMatcher {

    private static final Pattern ENC_PATTERN = Pattern.compile("ENC\\('(.*)'\\)");
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final WhiteListProperties whiteListProperties;
    private final RestClientProperties restClientProperties;

    @Override
    public boolean matches(HttpServletRequest request) {
        final String path = request.getRequestURI();
        Optional<WhiteListInfo> result = whiteListProperties.getWhiteListUrls().stream()
                .filter(info -> info.getUrlPattern().equals(path) ||
                        pathMatcher.match(info.getUrlPattern(), path)).findFirst();
        if (result.isPresent()) {
            WhiteListInfo info = result.get();
            if (CollectionUtils.isNotEmpty(info.getIpAddresses())) {
                String remoteIpAddr = WebUtils.getIpAddr(request);
                if (!info.getIpAddresses().contains(remoteIpAddr)) {
                    return false;
                }
            }
            Mode mode = info.getMode();
            return mode == Mode.INTERNAL ? matchesRestClient(request) : true;
        }
        return false;
    }

    private boolean matchesRestClient(HttpServletRequest request) {
        SecurityKey security = restClientProperties.getSecurity();
        String cipherText = request.getHeader(Constants.REQUEST_HEADER_REST_CLIENT_SECURITY_KEY);
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