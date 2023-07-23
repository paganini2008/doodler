package io.doodler.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: Vincent
 * Date: 2021/5/25 1:46 下午
 */
public interface BaseService<T> extends IEnhancedService<T> {

    /**
     * 根据查询条件
     *
     * @param queryWrapper
     * @return
     */
    Map<String, Object> getOneMap(QueryWrapper<T> queryWrapper);

    /**
     * @param queryMap
     * @return
     */
    Map<String, Object> getOneMap(Map<String, Object> queryMap);

    /**
     * 根据model查询
     *
     * @param t
     * @return
     */
    Map<String, Object> getOneMap(T t);

    /**
     * 分页查询
     *
     * @param pageInfo
     * @param t
     * @return
     */
    IPage<T> pageQuery(PageInfo pageInfo, T t);

    /**
     * 分页查询
     *
     * @param pageInfo
     * @param queryWrapper
     * @return
     */
    IPage<T> pageQuery(PageInfo pageInfo, QueryWrapper<T> queryWrapper);

    /**
     * 分页查询
     *
     * @param pageInfo
     * @param queryMap
     * @return
     */
    IPage<T> pageQuery(PageInfo pageInfo, Map<String, Object> queryMap);

    /**
     * 根据model更新实体
     *
     * @param t                实体对象
     * @param conditionColumns 更新时的条件字段（所有条件都是等于）
     * @param updateColumns    需要更新的字段
     * @return
     */
    boolean updateByEntity(T t, List<String> conditionColumns, String... updateColumns);
}