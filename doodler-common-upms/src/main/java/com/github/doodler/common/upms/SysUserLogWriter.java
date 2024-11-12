package com.github.doodler.common.upms;

import java.time.Instant;
import java.time.ZoneId;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.HttpRequestInfo;
import com.github.doodler.common.context.WebRequestCompletionAdvice;
import com.github.doodler.common.security.PermissionGrantedAuthority;
import com.github.doodler.common.security.RegularUser;
import com.github.doodler.common.security.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @Description: SysUserLogWriter
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class SysUserLogWriter extends WebRequestCompletionAdvice {

    private final ObjectMapper objectMapper;
    private final SysUserLogger sysUserLogger;

    @SneakyThrows
    @Override
    protected void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                     Exception e) {
        HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
        HttpRequestInfo.AuthInfo authInfo = httpRequestInfo.getAuthInfo();
        if (authInfo == null || authInfo.getPrincipal() == null || !authInfo.isApproved()) {
            return;
        }
        UserDetails userDetails = (UserDetails) authInfo.getPrincipal();
        if (!checkUpdateOpType(authInfo.getPermission(), userDetails) || shouldFilter(httpRequestInfo)) {
            return;
        }
        SysUserLogDto dto = createPayload(httpRequestInfo);
        sysUserLogger.saveSysUserLog(dto);
    }

    @SneakyThrows
    private SysUserLogDto createPayload(HttpRequestInfo httpRequestInfo) {
        RegularUser regularUser = (RegularUser) httpRequestInfo.getAuthInfo().getPrincipal();
        SysUserLogDto sysUserLogDto = new SysUserLogDto();
        sysUserLogDto.setUserId(regularUser.getId());
        sysUserLogDto.setPermissionCode(httpRequestInfo.getAuthInfo().getPermission());
        sysUserLogDto.setApiUrl(httpRequestInfo.getPath());
        sysUserLogDto.setRequestParams(httpRequestInfo.getRequestBody());
        if (httpRequestInfo.getResponseBody() != null) {
            sysUserLogDto.setResponseBody(objectMapper.writeValueAsString(httpRequestInfo.getResponseBody()));
        }
        if (httpRequestInfo.getTimestmap() > 0) {
            sysUserLogDto
                    .setCreatedAt(Instant.ofEpochMilli(httpRequestInfo.getTimestmap()).atZone(
                            ZoneId.systemDefault()).toLocalDateTime());
        }
        return sysUserLogDto;
    }

    private boolean checkUpdateOpType(String permissionCode, UserDetails userDetails) {
        return userDetails.getAuthorities().stream().anyMatch(ga -> isUpdatedOpType(permissionCode, ga));
    }

    private boolean isUpdatedOpType(String permissionCode, GrantedAuthority grantedAuthority) {
        PermissionGrantedAuthority permissionGrantedAuthority = (PermissionGrantedAuthority) grantedAuthority;
        if (ArrayUtils.isEmpty(permissionGrantedAuthority.getPermissions())
                || ArrayUtils.isEmpty(permissionGrantedAuthority.getOpTypes())) {
            return false;
        }
        String[] permissions = permissionGrantedAuthority.getPermissions();
        int index = ArrayUtils.indexOf(permissions, SecurityConstants.PERMISSION_TYPE_NAME_OPERATION + permissionCode);
        if (index != ArrayUtils.INDEX_NOT_FOUND) {
            String[] opTypes = permissionGrantedAuthority.getOpTypes();
            return ArrayUtils.isSameLength(permissions, opTypes) && "U".equalsIgnoreCase(opTypes[index]);
        }
        return false;
    }

    protected boolean shouldFilter(HttpRequestInfo httpRequestInfo) {
        return false;
    }
}