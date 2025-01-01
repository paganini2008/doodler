package com.github.doodler.common.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.doodler.common.mybatis.utils.DefaultQueryPageReader;
import com.github.doodler.common.mybatis.utils.GenericQueryPageReader;
import com.github.doodler.common.mybatis.utils.MapQueryPageReader;
import com.github.doodler.common.mybatis.utils.ScanHandler;
import com.github.doodler.common.page.EachPage;
import com.github.doodler.common.page.PageReader;
import com.github.doodler.common.page.PageRequest;
import com.github.doodler.common.page.PageResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description: IEnhancedService
 * @Author: Fred Feng
 * @Date: 20/12/2022
 * @Version 1.0.0
 */
public interface IEnhancedService<T> extends IService<T> {

    /**
     * Get first one with specific result class
     *
     * @param <VO>
     * @param resultClass
     * @return
     */
    default <VO> VO getOne(Class<VO> resultClass) {
        return getOne(Wrappers.emptyWrapper(), resultClass);
    }

    /**
     * Get first one with specific result class
     *
     * @param <VO>
     * @param query
     * @param resultClass
     * @return
     */
    <VO> VO getOne(Wrapper<T> query, Class<VO> resultClass);

    /**
     * Query for list with specific result class
     *
     * @param <VO>
     * @param resultClass
     * @return
     */
    default <VO> List<VO> list(Class<VO> resultClass) {
        return list(Wrappers.emptyWrapper(), resultClass);
    }

    /**
     * Query for list with specific result class
     *
     * @param <VO>
     * @param query
     * @param resultClass
     * @return
     */
    <VO> List<VO> list(Wrapper<T> query, Class<VO> resultClass);

    /**
     * Query for page with specific result class
     *
     * @param <VO>
     * @param pageInfo
     * @param resultClass
     * @return
     */
    default <VO> IPage<VO> pageQuery(PageInfo pageInfo, Class<VO> resultClass) {
        return pageQuery(pageInfo, Wrappers.emptyWrapper(), resultClass);
    }

    /**
     * Query for page with specific result class
     *
     * @param <VO>
     * @param pageInfo
     * @param query
     * @param resultClass
     * @return
     */
    <VO> IPage<VO> pageQuery(PageInfo pageInfo, Wrapper<T> query, Class<VO> resultClass);

    /**
     * Get a PageReader based on simple query
     *
     * @param wrapper
     * @return
     */
    default PageReader<T> pageReader(Wrapper<T> wrapper) {
        return new DefaultQueryPageReader<>(this, wrapper);
    }

    /**
     * Handle data rows page by page
     *
     * @param wrapper
     * @param pageNumber
     * @param pageSize
     * @param handler
     */
    default void scan(Wrapper<T> wrapper, int pageNumber, int pageSize, ScanHandler<T> handler) {
        PageReader<T> pageReader = pageReader(wrapper);
        PageResponse<T> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        for (EachPage<T> page : pageResponse) {
            if (handler != null) {
                handler.onEachPage(page);
            }
        }
    }

    /**
     * Get a PageReader based on map
     *
     * @param wrapper
     * @return
     */
    default PageReader<Map<String, Object>> pageReaderForMap(Wrapper<T> wrapper) {
        return new MapQueryPageReader<>(this, wrapper);
    }

    /**
     * Handle data rows page by page
     *
     * @param wrapper
     * @param pageNumber
     * @param pageSize
     * @param handler
     */
    default void scanForMap(Wrapper<T> wrapper, int pageNumber, int pageSize,
                                ScanHandler<Map<String, Object>> handler) {
        PageReader<Map<String, Object>> pageReader = pageReaderForMap(wrapper);
        PageResponse<Map<String, Object>> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        for (EachPage<Map<String, Object>> page : pageResponse) {
            if (handler != null) {
                handler.onEachPage(page);
            }
        }
    }

    /**
     * Get a PageReader based on customized object
     *
     * @param <VO>
     * @param wrapper
     * @param resultClass
     * @return
     */
    default <VO> PageReader<VO> pageReader(Wrapper<T> wrapper, Class<VO> resultClass) {
        return new GenericQueryPageReader<>(this, wrapper, resultClass);
    }

    /**
     * Handle data rows page by page
     *
     * @param <VO>
     * @param wrapper
     * @param resultClass
     * @param pageNumber
     * @param pageSize
     * @param handler
     */
    default <VO> void scan(Wrapper<T> wrapper, Class<VO> resultClass, int pageNumber, int pageSize,
                               ScanHandler<VO> handler) {
        PageReader<VO> pageReader = pageReader(wrapper, resultClass);
        PageResponse<VO> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        for (EachPage<VO> page : pageResponse) {
            if (handler != null) {
                handler.onEachPage(page);
            }
        }
    }
}