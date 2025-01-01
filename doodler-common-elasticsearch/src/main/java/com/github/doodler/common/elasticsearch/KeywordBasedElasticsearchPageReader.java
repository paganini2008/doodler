package com.github.doodler.common.elasticsearch;

import java.util.Arrays;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.Assert;

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
    private String[] highlightTags = {"<font color=\"red\" class=\"searchKeyword\">", "</font>"};

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
                                .fragmentSize(120).numOfFragments(5).noMatchSize(120));
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

}
