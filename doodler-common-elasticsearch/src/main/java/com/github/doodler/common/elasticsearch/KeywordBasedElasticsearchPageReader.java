package com.github.doodler.common.elasticsearch;

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
 * @Description: KeywordBasedElasticsearchPageReader
 * @Author: Fred Feng
 * @Date: 23/12/2024
 * @Version 1.0.0
 */
public class KeywordBasedElasticsearchPageReader<D, V> extends BasicElasticsearchPageReader<D, V> {

    private final String keyword;
    private final Map<String, String> searchFields;
    private String[] highlightTags = {"<font color='red' class='searchKeyword'>", "</font>"};

    public KeywordBasedElasticsearchPageReader(ElasticsearchRestTemplate elasticsearchRestTemplate,
            Class<D> documentClass, Class<V> valueClass, String keyword,
            Map<String, String> searchFields) {
        super(elasticsearchRestTemplate, documentClass, valueClass);
        Assert.hasText(keyword, "No keyword specified");
        this.keyword = keyword;
        Assert.notEmpty(searchFields, "No search fields specified");
        this.searchFields = searchFields;
    }

    public void setHighlightTags(String[] highlightTags) {
        this.highlightTags = highlightTags;
    }

    public String getKeyword() {
        return keyword;
    }

    public String[] getHighlightTags() {
        return highlightTags;
    }

    public Map<String, String> getSearchFields() {
        return searchFields;
    }

    @Override
    protected NativeSearchQueryBuilder getNativeSearchQueryBuilder() {
        NativeSearchQueryBuilder searchQueryBuilder = super.getNativeSearchQueryBuilder();
        searchQueryBuilder
                .withHighlightFields(searchFields.keySet().stream()
                        .map(f -> new HighlightBuilder.Field(f)).toList())
                .withHighlightBuilder(
                        new HighlightBuilder().preTags(highlightTags[0]).postTags(highlightTags[1])
                                .fragmentSize(120).numOfFragments(5).noMatchSize(120));
        return searchQueryBuilder;
    }

    @Override
    protected QueryBuilder getQuery() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        String searchField, logic;
        QueryBuilder qb;
        for (Map.Entry<String, String> entry : searchFields.entrySet()) {
            searchField = entry.getKey();
            qb = QueryBuilders.matchQuery(searchField, keyword);
            logic = entry.getValue();
            switch (logic.toLowerCase()) {
                case "should":
                    queryBuilder.should(qb);
                    break;
                case "must":
                    queryBuilder.must(qb);
                    break;
                case "must not":
                    queryBuilder.mustNot(qb);
                    break;
                case "filter":
                    queryBuilder.filter(qb);
                    break;
                default:
                    break;
            }
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
