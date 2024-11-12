package com.github.doodler.common.upms;

import java.util.List;

import org.springframework.scheduling.annotation.Async;

import lombok.RequiredArgsConstructor;

/**
 * @Description: RemotePermissionPersistenceServiceImpl
 * @Author: Fred Feng
 * @Date: 08/11/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RemotePermissionPersistenceServiceImpl implements PermissionPersistenceService {

    private final IRemoteSysPermissionService remoteSysPermissionService;

    @Async
    @Override
    public void saveExternalPermissions(List<PermissionPersistenceDto> list) {
        remoteSysPermissionService.saveExternalPermissions(list);
    }
}