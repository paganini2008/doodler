package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.AUTHORIZATION_TYPE_BEARER;
import static com.github.doodler.common.security.SecurityConstants.PERMISSION_TYPE_NAME_MENU;
import static com.github.doodler.common.security.SecurityConstants.PERMISSION_TYPE_NAME_OPERATION;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.github.doodler.common.BizException;
import com.github.doodler.common.security.AuthenticationInfo.GrantedAuthorityInfo;
import lombok.experimental.UtilityClass;

/**
 * @Description: SecurityUtils
 * @Author: Fred Feng
 * @Date: 18/11/2022
 * @Version 1.0.0
 */
@UtilityClass
public class SecurityUtils {

    public static final List<PermissionGrantedAuthority> NO_AUTHORITIES = Collections.emptyList();

    public String getBearerToken(String authorization) {
        if (StringUtils.isNotBlank(authorization) && authorization.startsWith(AUTHORIZATION_TYPE_BEARER)) {
            return authorization.substring(7);
        }
        throw new BizException(ErrorCodes.JWT_TOKEN_BAD_FORMAT, HttpStatus.UNAUTHORIZED, authorization);
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public RegularUser getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication instanceof PlatformToken) {
            return (RegularUser) authentication.getPrincipal();
        }
        throw new BizException(ErrorCodes.NONE_USER_AUTHORIZATION, HttpStatus.UNAUTHORIZED);
    }

    public <T extends GrantedAuthority> GrantedAuthorityInfo[] getGrantedAuthorities(Collection<T> authorities) {
        return authorities.stream().map(au -> {
            PermissionGrantedAuthority real = (PermissionGrantedAuthority) au;
            return new GrantedAuthorityInfo(real.getAuthority(), real.getPermissions(), real.getOpTypes());
        }).toArray(l -> new GrantedAuthorityInfo[l]);
    }

    public Collection<PermissionGrantedAuthority> getGrantedAuthorities(GrantedAuthorityInfo[] infos) {
        return Arrays.stream(infos).map(
                        info -> new PermissionGrantedAuthority(info.getRole(), info.getPermissions(), info.getOpTypes()))
                .collect(Collectors.toList());
    }

    public String[] getGrantedMenuAuthorities(String[] permissions) {
        return Arrays.stream(permissions).filter(p -> StringUtils.isNotBlank(p) && p.startsWith(PERMISSION_TYPE_NAME_MENU))
                .map(p -> p.replaceFirst(PERMISSION_TYPE_NAME_MENU, "")).toArray(l -> new String[l]);
    }

    public String[] getGrantedOperationAuthorities(String[] permissions) {
        return Arrays.stream(permissions).filter(
                        p -> StringUtils.isNotBlank(p) && p.startsWith(PERMISSION_TYPE_NAME_OPERATION))
                .map(p -> p.replaceFirst(PERMISSION_TYPE_NAME_OPERATION, "")).toArray(l -> new String[l]);
    }

    public Collection<PermissionGrantedAuthority> getGrantedAuthorities(String... roles) {
    	if(ArrayUtils.isEmpty(roles)) {
    		return Collections.emptyList();
    	}
        return Arrays.stream(roles).map(role -> new PermissionGrantedAuthority(role, new String[0], new String[0])).collect(
                Collectors.toList());
    }

    public Collection<PermissionGrantedAuthority> getGrantedAuthorities(Map<String, Collection<String>> authorities) {
        return authorities.entrySet().stream().map(
                e -> new PermissionGrantedAuthority(e.getKey(), e.getValue().toArray(new String[0]),
                        new String[0])).collect(
                Collectors.toList());
    }

    public <T extends GrantedAuthority> Collection<SimpleGrantedAuthority> getSimpleAuthorities(String... roles) {
    	if(ArrayUtils.isEmpty(roles)) {
    		return Collections.emptyList();
    	}
        return Arrays.stream(roles).map(role -> new SimpleGrantedAuthority(role)).collect(
                Collectors.toList());
    }

    public <T extends GrantedAuthority> Collection<SimpleGrantedAuthority> getSimpleAuthorities(Collection<T> authorities) {
        return authorities.stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).collect(Collectors.toList());
    }

    public String getTypeOfEncryptedPassword(String encodedPassword) {
        if (encodedPassword == null) {
            return null;
        }
        int start = encodedPassword.indexOf("{");
        if (start != 0) {
            return null;
        }
        int end = encodedPassword.indexOf("}", start);
        if (end < 0) {
            return null;
        }
        return encodedPassword.substring(start + 1, end);
    }
}