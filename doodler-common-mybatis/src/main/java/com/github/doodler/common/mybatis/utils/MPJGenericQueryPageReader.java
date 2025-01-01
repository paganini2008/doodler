package com.github.doodler.common.mybatis.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.doodler.common.page.DefaultPageContent;
import com.github.doodler.common.page.PageContent;
import com.github.doodler.common.page.PageReader;
import com.github.yulichang.base.MPJBaseService;
import com.github.yulichang.interfaces.MPJBaseJoin;

/**
 * @Description: MPJGenericQueryPageReader
 * @Author: Fred Feng
 * @Date: 19/03/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class MPJGenericQueryPageReader<T, VO> implements PageReader<VO> {

    private final MPJBaseService<T> baseService;
    private final MPJBaseJoin<T> query;
    private final Class<VO> resultClass;

    public MPJGenericQueryPageReader(MPJBaseService<T> baseService, MPJBaseJoin<T> query,
            Class<VO> resultClass) {
        this.baseService = baseService;
        this.query = query;
        this.resultClass = resultClass;
    }

    @Override
    public PageContent<VO> list(int pageNumber, int offset, int limit, Object nextToken) {
        IPage<VO> page = baseService.selectJoinListPage(new Page<>(pageNumber, limit, false),
                resultClass, query);
        return new DefaultPageContent<>(page.getRecords(), null);
    }
}
