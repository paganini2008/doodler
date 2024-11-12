package com.github.doodler.common.id;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RemoteIdBatchGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RemoteIdBatchGenerator implements IdBatchGenerator {

    private final RemoteIdService remoteIdService;

    @Override
    public List<String> createManyStrings(int n) {
        return remoteIdService.createManyStrings(n).getData();
    }

    @Override
    public List<Long> createManyLongs(int n) {
        return remoteIdService.createManyLongs(n).getData();
    }

}
