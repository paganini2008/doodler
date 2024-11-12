package com.github.doodler.common.jpa;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

/**
 * 
 * @Description: Model
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public interface Model<X> {

    static final String ROOT = "this";

    EntityType<X> getEntityType();

    Class<?> getRootType();

    Class<X> getType();

    boolean isManaged(Class<?> type);

    Root<?> getRoot();

    String getAlias();

    <T> Path<T> getAttribute(String attributeName);

    <T> Path<T> getAttribute(String name, String attributeName);

    boolean hasAttribute(String name, String attributeName);

    Selection<?> getSelection(String alias);

    List<Selection<?>> getSelections(String alias, String[] attributeNames);

    default List<JpaAttributeDetail> getAttributeDetails() {
        return getAttributeDetails(ROOT);
    }

    List<JpaAttributeDetail> getAttributeDetails(String alias);

    <Y> Model<Y> join(String attributeName, String alias, Predicate on);

    <Y> Model<Y> leftJoin(String attributeName, String alias, Predicate on);

    <Y> Model<Y> rightJoin(String attributeName, String alias, Predicate on);

    <S> Model<S> sibling(Model<S> sibling);

    static <X> Model<X> forRoot(Root<X> root) {
        return forRoot(root, ROOT);
    }

    static <X> Model<X> forRoot(Root<X> root, String alias) {
        return new RootModel<X>(root, alias, null);
    }

}
