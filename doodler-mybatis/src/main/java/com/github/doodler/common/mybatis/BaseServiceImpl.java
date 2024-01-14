package com.github.doodler.common.mybatis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.doodler.common.utils.MapUtils;

/**
 * 
 * @Description: BaseServiceImpl
 * @Author: Fred Feng
 * @Date: 14/03/2022
 * @Version 1.0.0
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
	
	@Autowired(required = false)
	private ConversionService conversionService;

    @Override
    public Map<String, Object> getOneMap(QueryWrapper<T> queryWrapper) {
        List<Map<String, Object>> listMap = this.listMaps(queryWrapper);
        if (listMap != null && listMap.size() > 0) {
            return listMap.get(0);
        }
        return null;
    }

    @Override
    public Map<String, Object> getOneMap(Map<String, Object> queryMap) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.allEq(queryMap);
        return this.getOneMap(queryWrapper);
    }

    @Override
    public Map<String, Object> getOneMap(T t) {
        return this.getOneMap(MapUtils.obj2Map(t));
    }

    @Override
    public IPage<T> pageQuery(PageDto pageInfo, T t) {
        return this.pageQuery(pageInfo, MapUtils.obj2Map(t));
    }

    @Override
    public IPage<T> pageQuery(PageDto pageInfo, QueryWrapper<T> queryWrapper) {
        Page<T> page = new Page<>(pageInfo.getCurrent(), pageInfo.getPageSize());
        if (null != pageInfo.getSorts() && pageInfo.getSorts().size() > 0) {
            pageInfo.getSorts().forEach(sortInfo -> {
                queryWrapper.orderBy(StringUtils.isNotBlank(sortInfo.getSortField()),
                        SqlKeyword.ASC.getSqlSegment().equalsIgnoreCase(sortInfo.getSortType()), sortInfo.getSortField());
            });
        }
        return this.page(page, queryWrapper);
    }

    @Override
    public IPage<T> pageQuery(PageDto pageInfo, Map<String, Object> queryMap) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.allEq(queryMap);
        return this.pageQuery(pageInfo, queryWrapper);
    }

    @Override
    public boolean updateByEntity(T t, List<String> conditionColumns, String... updateColumns) {
        Map<String, Object> entityMap = MapUtils.obj2Map(t);
        UpdateWrapper<T> updateWrapper = Wrappers.update();
        for (String conditionColumn : conditionColumns) {
            updateWrapper.eq(entityMap.get(conditionColumn) != null, conditionColumn,
                    entityMap.get(conditionColumn));
        }

        for (String updateColumn : updateColumns) {
            updateWrapper.set(updateColumn, entityMap.get(updateColumn));
        }

        return this.update(updateWrapper);
    }

    @Override
    public <VO> VO getOne(Wrapper<T> query, Class<VO> resultClass) {
        Map<String, Object> raw = super.getMap(query);
        return MapUtils.map2Obj(raw, resultClass);
    }

    @Override
    public <VO> List<VO> list(Wrapper<T> query, Class<VO> resultClass) {
        List<Map<String, Object>> raw = super.listMaps(query);
        return raw.stream().map(m -> MapUtils.map2Obj(m, resultClass, conversionService))
                .collect(Collectors.toList());
    }

    @Override
    public <VO> IPage<VO> pageQuery(PageDto pageInfo, Wrapper<T> query, Class<VO> resultClass) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getCurrent(), pageInfo.getPageSize(), pageInfo.isSearchCount());
        if (null != pageInfo.getSorts() && pageInfo.getSorts().size() > 0) {
            if (query instanceof QueryWrapper) {
                pageInfo.getSorts().forEach(sortInfo -> {
                    ((QueryWrapper<T>) query).orderBy(StringUtils.isNotBlank(sortInfo.getSortField()),
                            SqlKeyword.ASC.getSqlSegment().equalsIgnoreCase(sortInfo.getSortType()), sortInfo.getSortField());
                });
            }
        }
        final Page<Map<String, Object>> result = super.pageMaps(page, query);
        return new Page<VO>(result.getCurrent(), result.getSize(), result.getTotal()) {

            private static final long serialVersionUID = 1L;

            @Override
            public List<VO> getRecords() {
            	
                List<Map<String, Object>> raw = result.getRecords();
                return raw.stream().map(m -> MapUtils.map2Obj(m, resultClass, conversionService))
                        .collect(Collectors.toList());
            }
        };
    }
}