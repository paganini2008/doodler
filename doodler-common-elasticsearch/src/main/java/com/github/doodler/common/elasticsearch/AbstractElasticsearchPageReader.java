package com.github.doodler.common.elasticsearch;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import com.github.doodler.common.page.DefaultPageContent;
import com.github.doodler.common.page.PageContent;
import com.github.doodler.common.page.PageReader;
import com.github.doodler.common.utils.BeanCopyUtils;

/**
 * 
 * @Description: AbstractElasticsearchPageReader
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
public abstract class AbstractElasticsearchPageReader<D, V> implements PageReader<V> {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final Class<D> documentClass;
    private final Class<V> valueClass;

    protected AbstractElasticsearchPageReader(ElasticsearchRestTemplate elasticsearchRestTemplate,
            Class<D> documentClass, Class<V> valueClass) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
        this.documentClass = documentClass;
        this.valueClass = valueClass;
    }

    @Override
    public long rowCount() {
        NativeSearchQueryBuilder searchQueryBuilder = getNativeSearchQueryBuilder();
        NativeSearchQuery searchQuery = searchQueryBuilder.build();
        return elasticsearchRestTemplate.count(searchQuery, documentClass);
    }

    @Override
    public PageContent<V> list(int pageNumber, int offset, int limit, Object nextToken)
            throws Exception {
        NativeSearchQueryBuilder searchQueryBuilder = getNativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(PageRequest.of(pageNumber - 1, limit));
        SearchHits<D> hits =
                elasticsearchRestTemplate.search(searchQueryBuilder.build(), documentClass);
        if (hits.isEmpty()) {
            return new DefaultPageContent<>(null, nextToken);
        }
        List<V> dataList = getContent(hits);
        return new DefaultPageContent<>(dataList, nextToken);
    }

    protected List<V> getContent(SearchHits<D> hits) {
        List<V> dataList = new ArrayList<V>();
        for (SearchHit<D> hit : hits.getSearchHits()) {
            dataList.add(convertValueObject(hit.getContent(), hit));
        }
        return dataList;
    }

    protected V convertValueObject(D document, SearchHit<D> hit) {
        return BeanCopyUtils.copyBean(document, valueClass);
    }

    protected NativeSearchQueryBuilder getNativeSearchQueryBuilder() {
        return new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery());
    }


}
