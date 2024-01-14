package com.github.doodler.common.mybatis.utils;

import static com.github.doodler.common.mybatis.MyBatisConstants.SQL_LIMIT_SYNTAX_FORMAT;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.doodler.common.jdbc.page.PageReader;

/**
 * @Description: MapQueryPageReader
 * @Author: Fred Feng
 * @Date: 17/01/2020
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class MapQueryPageReader<T> implements PageReader<Map<String, Object>> {

    private final IService<T> service;
    private final Wrapper<T> query;

    public MapQueryPageReader(IService<T> service, Wrapper<T> query) {
        this.service = service;
        this.query = query;
    }

    @Override
    public List<Map<String, Object>> list(int pageNumber, int maxResults) {
        if (!(query instanceof Join)) {
            throw new UnsupportedOperationException("Unknown query type: " + query.getClass().getName());
        }
        int offset = (pageNumber - 1) * maxResults;
        Wrapper<T> ref = (Wrapper<T>) ((Join) query).last(String.format(SQL_LIMIT_SYNTAX_FORMAT, maxResults, offset));
        return service.listMaps(ref);
    }
}