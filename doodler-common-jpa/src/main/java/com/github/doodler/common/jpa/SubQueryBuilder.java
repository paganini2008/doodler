package com.github.doodler.common.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Subquery;

/**
 * 
 * @Description: SubQueryBuilder
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public interface SubQueryBuilder<T> {

    Subquery<T> toSubquery(CriteriaBuilder builder);

}
