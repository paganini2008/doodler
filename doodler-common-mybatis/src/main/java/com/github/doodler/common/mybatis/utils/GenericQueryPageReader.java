package com.github.doodler.common.mybatis.utils;

import static com.github.doodler.common.mybatis.StringPool.SQL_LIMIT_SYNTAX_FORMAT;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.github.doodler.common.mybatis.IEnhancedService;
import com.github.doodler.common.page.DefaultPageContent;
import com.github.doodler.common.page.PageContent;
import com.github.doodler.common.page.PageReader;

/**
 * @Description: GenericQueryPageReader
 * @Author: Fred Feng
 * @Date: 17/03/2023
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class GenericQueryPageReader<T, VO> implements PageReader<VO> {

    private final IEnhancedService<T> service;
    private final Wrapper<T> query;
    private final Class<VO> requiredClass;

    public GenericQueryPageReader(IEnhancedService<T> service, Wrapper<T> query,
            Class<VO> requiredClass) {
        this.service = service;
        this.query = query;
        this.requiredClass = requiredClass;
    }

    @Override
    public PageContent<VO> list(int pageNumber, int offset, int limit, Object nextToken) {
        if (!(query instanceof Join)) {
            throw new UnsupportedOperationException(
                    "Unknown query type: " + query.getClass().getName());
        }
        Wrapper<T> ref = (Wrapper<T>) ((Join) query)
                .last(String.format(SQL_LIMIT_SYNTAX_FORMAT, limit, offset));
        return new DefaultPageContent<>(service.list(ref, requiredClass), null);
    }
}
