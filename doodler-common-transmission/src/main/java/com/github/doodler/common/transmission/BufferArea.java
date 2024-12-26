package com.github.doodler.common.transmission;

import java.util.Collection;

/**
 * 
 * @Description: BufferArea
 * @Author: Fred Feng
 * @Date: 26/12/2024
 * @Version 1.0.0
 */
public interface BufferArea {

    void put(String collection, Bucket bucket);

    long size(String collection);

    Bucket poll(String collection);

    Collection<Bucket> poll(String collection, int batchSize);

}
