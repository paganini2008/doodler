package com.github.doodler.common.mybatis;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 
 * @Description: BaseService
 * @Author: Fred Feng
 * @Date: 14/03/2022
 * @Version 1.0.0
 */
public interface BaseService<T> extends IEnhancedService<T> {

    Map<String, Object> getOneMap(QueryWrapper<T> queryWrapper);

    Map<String, Object> getOneMap(Map<String, Object> queryMap);

    Map<String, Object> getOneMap(T t);

    IPage<T> pageQuery(PageDto pageInfo, T t);

    IPage<T> pageQuery(PageDto pageInfo, QueryWrapper<T> queryWrapper);

    IPage<T> pageQuery(PageDto pageInfo, Map<String, Object> queryMap);

    boolean updateByEntity(T t, List<String> conditionColumns, String... updateColumns);
}