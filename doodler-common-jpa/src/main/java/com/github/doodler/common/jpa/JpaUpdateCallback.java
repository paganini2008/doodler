package com.github.doodler.common.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;

/**
 * 
 * @Description: JpaUpdateCallback
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public interface JpaUpdateCallback<T> {

    CriteriaUpdate<T> doInJpa(CriteriaBuilder builder);

}
