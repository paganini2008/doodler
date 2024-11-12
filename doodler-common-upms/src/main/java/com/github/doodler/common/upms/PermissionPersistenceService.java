package com.github.doodler.common.upms;

import java.util.List;

/**
 * @Description: PermissionPersistenceService
 * @Author: Fred Feng
 * @Date: 08/11/2023
 * @Version 1.0.0
 */
public interface PermissionPersistenceService {

	void saveExternalPermissions(List<PermissionPersistenceDto> list);
}