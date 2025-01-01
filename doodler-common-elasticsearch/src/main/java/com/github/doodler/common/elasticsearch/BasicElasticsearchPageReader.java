package com.github.doodler.common.elasticsearch;

import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

/**
 * 
 * @Description: BasicElasticsearchPageReader
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
public class BasicElasticsearchPageReader<D, V> extends AbstractElasticsearchPageReader<D, V> {

    public BasicElasticsearchPageReader(ElasticsearchRestTemplate elasticsearchRestTemplate,
            Class<D> documentClass, Class<V> valueClass) {
        super(elasticsearchRestTemplate, documentClass, valueClass);
    }

    @Override
    protected NativeSearchQueryBuilder getNativeSearchQueryBuilder() {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(getQuery());
        QueryBuilder filterBuilder = getFilter();
        if (filterBuilder != null) {
            searchQueryBuilder.withFilter(filterBuilder);
        }
        FieldSortBuilder sortBuilder = getSort();
        if (sortBuilder != null) {
            searchQueryBuilder.withSorts(sortBuilder);
        }
        String[] fields = getFields();
        if (ArrayUtils.isNotEmpty(fields)) {
            searchQueryBuilder.withFields(fields);
        }
        return searchQueryBuilder;
    }

    protected QueryBuilder getQuery() {
        return QueryBuilders.matchAllQuery();
    }

    protected QueryBuilder getFilter() {
        return null;
    }

    protected FieldSortBuilder getSort() {
        return null;
    }

    protected String[] getFields() {
        return new String[0];
    }


}
