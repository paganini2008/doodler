package com.github.doodler.common.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * 
 * @Description: Filter
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public interface Filter {

    public Predicate toPredicate(Model<?> model, CriteriaBuilder builder);

}
