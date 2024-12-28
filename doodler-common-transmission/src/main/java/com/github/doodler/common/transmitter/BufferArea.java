package com.github.doodler.common.transmitter;

import java.util.Collection;

/**
 * 
 * @Description: BufferArea
 * @Author: Fred Feng
 * @Date: 26/12/2024
 * @Version 1.0.0
 */
public interface BufferArea {

    void put(String collection, Packet bucket);

    long size(String collection);

    Packet poll(String collection);

    Collection<Packet> poll(String collection, int batchSize);

}
