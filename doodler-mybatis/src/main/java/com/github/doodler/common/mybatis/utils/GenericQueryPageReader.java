package com.github.doodler.common.mybatis.utils;

import static com.github.doodler.common.mybatis.MyBatisConstants.SQL_LIMIT_SYNTAX_FORMAT;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.github.doodler.common.jdbc.page.PageReader;
import com.github.doodler.common.mybatis.IEnhancedService;

/**
 * @Description: GenericQueryPageReader
 * @Author: Fred Feng
 * @Date: 17/01/2020
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class GenericQueryPageReader<T, VO> implements PageReader<VO> {

    private final IEnhancedService<T> service;
    private final Wrapper<T> query;
    private final Class<VO> requiredClass;

    public GenericQueryPageReader(IEnhancedService<T> service, Wrapper<T> query, Class<VO> requiredClass) {
        this.service = service;
        this.query = query;
        this.requiredClass = requiredClass;
    }

    @Override
    public List<VO> list(int pageNumber, int maxResults) {
        if (!(query instanceof Join)) {
            throw new UnsupportedOperationException("Unknown query type: " + query.getClass().getName());
        }
        int offset = (pageNumber - 1) * maxResults;
        Wrapper<T> ref = (Wrapper<T>) ((Join) query).last(String.format(SQL_LIMIT_SYNTAX_FORMAT, maxResults, offset));
        return service.list(ref, requiredClass);
    }
}