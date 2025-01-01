package com.github.doodler.common.mybatis.utils;

import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.doodler.common.page.DefaultPageContent;
import com.github.doodler.common.page.PageContent;
import com.github.doodler.common.page.PageReader;
import com.github.yulichang.base.MPJBaseService;
import com.github.yulichang.interfaces.MPJBaseJoin;

/**
 * @Description: MPJMapQueryPageReader
 * @Author: Fred Feng
 * @Date: 19/03/2023
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
    public PageContent<Map<String, Object>> list(int pageNumber, int offset, int limit,
            Object nextToken) {
        IPage<Map<String, Object>> page =
                baseService.selectJoinMapsPage(new Page<>(pageNumber, limit, false), query);
        return new DefaultPageContent<>(page.getRecords(), null);
    }
}
