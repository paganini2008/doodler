package com.github.doodler.common.upms;

import java.time.LocalDateTime;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: SysUserLogDto
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class SysUserLogDto {

    private Long userId;
    private String permissionCode;
    private String apiUrl;
    private @Nullable String requestParams;
    private @Nullable String responseBody;
    private LocalDateTime createdAt;
}