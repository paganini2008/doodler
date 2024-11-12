package com.github.doodler.common.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * 
 * @Description: PredicateBuilder
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public interface PredicateBuilder<T> {

    default String getDefaultAlias() {
        return Model.ROOT;
    }

    Predicate toPredicate(Model<?> model, Expression<T> expression, CriteriaBuilder builder);

}
