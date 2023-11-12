package io.doodler.common.mybatis;

import com.github.yulichang.base.MPJBaseService;
import com.github.yulichang.interfaces.MPJBaseJoin;

import io.doodler.common.jdbc.page.EachPage;
import io.doodler.common.jdbc.page.PageReader;
import io.doodler.common.jdbc.page.PageRequest;
import io.doodler.common.jdbc.page.PageResponse;
import io.doodler.common.mybatis.utils.MPJGenericQueryPageReader;
import io.doodler.common.mybatis.utils.MPJMapQueryPageReader;
import io.doodler.common.mybatis.utils.ScanHandler;

import java.util.Map;

/**
 * @Description: EnhancedMPJBaseService
 * @Author: Fred Feng
 * @Date: 23/03/2023
 * @Version 1.0.0
 */
public interface EnhancedMPJBaseService<T> extends MPJBaseService<T> {

    /**
     * Get a PageReader based on customized object class
     *
     * @param <VO>
     * @param query
     * @param resultClass
     * @return
     */
    default <VO> PageReader<VO> pageReader(MPJBaseJoin<T> query, Class<VO> resultClass) {
        return new MPJGenericQueryPageReader<>(this, query, resultClass);
    }

    /**
     * Get a PageReader based on map
     *
     * @param query
     * @return
     */
    default PageReader<Map<String, Object>> pageReaderForMap(MPJBaseJoin<T> query) {
        return new MPJMapQueryPageReader<>(this, query);
    }

    /**
     * Handle data rows page by page
     *
     * @param query
     * @param pageNumber
     * @param pageSize
     * @param handler
     */
    default void scan(MPJBaseJoin<T> query, int pageNumber, int pageSize,
                                ScanHandler<Map<String, Object>> handler) {
        PageReader<Map<String, Object>> pageReader = pageReaderForMap(query);
        PageResponse<Map<String, Object>> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        for (EachPage<Map<String, Object>> page : pageResponse) {
            if (handler != null) {
                handler.onEachPage(page);
            }
        }
    }

    /**
     * Handle data rows page by page
     *
     * @param <VO>
     * @param query
     * @param resultClass
     * @param pageNumber
     * @param pageSize
     * @param handler
     */
    default <VO> void scan(MPJBaseJoin<T> query, Class<VO> resultClass, int pageNumber, int pageSize,
                               ScanHandler<VO> handler) {
        PageReader<VO> pageReader = pageReader(query, resultClass);
        PageResponse<VO> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        for (EachPage<VO> page : pageResponse) {
            if (handler != null) {
                handler.onEachPage(page);
            }
        }
    }
}