package com.github.doodler.common.mybatis.utils;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.doodler.common.jdbc.page.PageReader;
import com.github.yulichang.base.MPJBaseService;
import com.github.yulichang.interfaces.MPJBaseJoin;

/**
 * @Description: MPJMapQueryPageReader
 * @Author: Fred Feng
 * @Date: 19/01/2020
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class MPJMapQueryPageReader<T> implements PageReader<Map<String, Object>> {

    private final MPJBaseService<T> baseService;
    private final MPJBaseJoin<T> query;

    public MPJMapQueryPageReader(MPJBaseService<T> service, MPJBaseJoin<T> query) {
        this.baseService = service;
        this.query = query;
    }

    @Override
    public List<Map<String, Object>> list(int pageNumber, int maxResults) {
        IPage<Map<String, Object>> page = baseService.selectJoinMapsPage(new Page<>(pageNumber, maxResults, false), query);
        return page.getRecords();
    }
}