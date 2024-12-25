package com.github.doodler.common.utils;

import java.util.function.Predicate;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: PredicateUtils
 * @Author: Fred Feng
 * @Date: 24/12/2024
 * @Version 1.0.0
 */
@UtilityClass
public class Predicates {

    public <T> Predicate<T> junction() {
        return t -> true;
    }

    public <T> Predicate<T> disjunction() {
        return t -> false;
    }

    public <T> Predicate<T> not(Iterable<Predicate<T>> it) {
        return and(it).negate();
    }

    public <T> Predicate<T> and(Predicate<T>... predicates) {
        Predicate<T> p = junction();
        for (Predicate<T> e : predicates) {
            p = p.and(e);
        }
        return p;
    }

    public <T> Predicate<T> and(Iterable<Predicate<T>> it) {
        Predicate<T> p = junction();
        for (Predicate<T> e : it) {
            p = p.and(e);
        }
        return p;
    }

    public <T> Predicate<T> or(Iterable<Predicate<T>> it) {
        Predicate<T> p = disjunction();
        for (Predicate<T> e : it) {
            p = p.or(e);
        }
        return p;
    }

}
