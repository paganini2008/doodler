package com.github.doodler.common.elasticsearch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.Assert;
import com.github.doodler.common.utils.MapUtils;

/**
 * 
 * @Description: AbstractElasticsearchPageReader
 * @Author: Fred Feng
 * @Date: 23/12/2024
 * @Version 1.0.0
 */
public class KeywordBasedElasticsearchPageReader<D, V> extends BasicElasticsearchPageReader<D, V> {

    private final String keyword;
    private final String[] searchFields;
    private String[] highlightTags = {"<font color='red' class='searchKeyword'>", "</font>"};

    public KeywordBasedElasticsearchPageReader(ElasticsearchRestTemplate elasticsearchRestTemplate,
            Class<D> documentClass, Class<V> valueClass, String keyword, String[] searchFields) {
        super(elasticsearchRestTemplate, documentClass, valueClass);
        Assert.hasText(keyword, "No keyword specified");
        this.keyword = keyword;
        Assert.notEmpty(searchFields, "No search fields specified");
        this.searchFields = searchFields;
    }

    public void setHighlightTags(String[] highlightTags) {
        this.highlightTags = highlightTags;
    }

    @Override
    protected NativeSearchQueryBuilder getNativeSearchQueryBuilder() {
        NativeSearchQueryBuilder searchQueryBuilder = super.getNativeSearchQueryBuilder();
        searchQueryBuilder
                .withHighlightFields(Arrays.stream(searchFields)
                        .map(f -> new HighlightBuilder.Field(f)).toList())
                .withHighlightBuilder(
                        new HighlightBuilder().preTags(highlightTags[0]).postTags(highlightTags[1])
                                .fragmentSize(150).numOfFragments(5).noMatchSize(150));
        return searchQueryBuilder;
    }

    @Override
    protected QueryBuilder getQuery() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (String searchField : searchFields) {
            queryBuilder.should(QueryBuilders.matchQuery(searchField, keyword));
        }
        return queryBuilder;
    }

    @Override
    protected V convertValueObject(D document, SearchHit<D> hit) {
        V vo = super.convertValueObject(document, hit);
        Map<String, List<String>> map = hit.getHighlightFields();
        if (MapUtils.isEmpty(map)) {
            return vo;
        }
        String propertyName;
        List<String> fragments;
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            propertyName = entry.getKey();
            fragments = entry.getValue();
            try {
                PropertyUtils.setProperty(vo, propertyName,
                        String.join(" ", fragments.toArray(new String[0])));
            } catch (Exception ingored) {
            }
        }
        return vo;
    }



}
