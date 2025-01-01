package com.github.doodler.common.elasticsearch;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: BasicElasticsearchCrudService
 * @Author: Fred Feng
 * @Date: 23/12/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public abstract class BasicElasticsearchCrudService<T> {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    public T save(T entity, String indexName) {
        return elasticsearchTemplate.save(entity, IndexCoordinates.of(indexName));
    }

    public Iterable<T> saveAll(Iterable<T> entities, String indexName) {
        return elasticsearchTemplate.save(entities, IndexCoordinates.of(indexName));
    }

    public void updateById(String id, T entity, String indexName) {
        UpdateQuery.Builder builder = UpdateQuery.builder(id)
                .withDocument(Document.from(BeanUtil.beanToMap(entity, false, true)));
        elasticsearchTemplate.update(builder.build(), IndexCoordinates.of(indexName));
    }

    public String deleteById(String id, String indexName) {
        return elasticsearchTemplate.delete(id, IndexCoordinates.of(indexName));
    }

    public String delete(Object entity, String indexName) {
        return elasticsearchTemplate.delete(entity, IndexCoordinates.of(indexName));
    }

    public long delete(Query query, Class<T> entityClass) {
        ByQueryResponse response = elasticsearchTemplate.delete(query, entityClass);
        return response.getDeleted();
    }

    public long deleteAll(Class<T> entityClass) {
        return delete(elasticsearchTemplate.matchAllQuery(), entityClass);
    }

    public boolean exists(String id, String indexName) {
        return elasticsearchTemplate.exists(id, IndexCoordinates.of(indexName));
    }

    public boolean exists(String id, Class<T> entityClass) {
        return elasticsearchTemplate.exists(id, entityClass);
    }

    public T getById(String id, Class<T> entityClass) {
        return elasticsearchTemplate.get(id, entityClass);
    }

    public T getById(String id, Class<T> entityClass, String indexName) {
        return elasticsearchTemplate.get(id, entityClass, IndexCoordinates.of(indexName));
    }

    public long getCount(Query query, String indexName) {
        return elasticsearchTemplate.count(query, IndexCoordinates.of(indexName));
    }

    public long getCount(Query query, Class<T> entityClass) {
        return elasticsearchTemplate.count(query, entityClass);
    }

    public T searchOne(Query query, Class<T> entityClass) {
        SearchHit<T> searchHit = elasticsearchTemplate.searchOne(query, entityClass);
        return searchHit != null ? searchHit.getContent() : null;
    }

    public List<T> search(Query query, Class<T> entityClass) {
        SearchHits<T> searchHits = elasticsearchTemplate.search(query, entityClass);
        if (searchHits.isEmpty()) {
            return Collections.emptyList();
        }
        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

}
