package com.github.doodler.common.upms;

import lombok.Data;

/**
 * @Description: PermissionPersistenceDto
 * @Author: Fred Feng
 * @Date: 07/11/2023
 * @Version 1.0.0
 */
@Data
public class PermissionPersistenceDto {

	private String name;
	private String perm;
	private String path;
	private String opType;
	private String superior;
	private String role;
}