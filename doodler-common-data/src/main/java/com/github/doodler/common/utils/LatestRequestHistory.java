package com.github.doodler.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @Description: LatestRequestHistory
 * @Author: Fred Feng
 * @Date: 17/04/2023
 * @Version 1.0.0
 */
public class LatestRequestHistory<T> extends BoundedList<T> {

    private static final long serialVersionUID = 6768520217160343963L;

    public LatestRequestHistory(int size) {
        super(size);
    }

    public List<T> display() {
        List<T> copy = new ArrayList<>(this);
        Collections.reverse(copy);
        return copy;
    }
}