package com.github.doodler.common.id;

import java.util.concurrent.atomic.LongAdder;

/**
 * 
 * @Description: SimpleIdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public class SimpleIdGenerator extends AbstractIdGenerator {

    private final LongAdder longAdder;
    private final long startValue;
    private final long endValue;

    public SimpleIdGenerator() {
        this(0, Long.MAX_VALUE - 1);
    }

    public SimpleIdGenerator(long startValue, long endValue) {
        this.longAdder = new LongAdder();
        this.longAdder.add(startValue);
        this.startValue = startValue;
        this.endValue = endValue;
    }

    @Override
    public Long getNextId() {
        long currentValue = longAdder.longValue();
        if (currentValue + 1 == endValue) {
            longAdder.reset();
            longAdder.add(startValue);
            currentValue += 1;
        } else {
            longAdder.add(1);
        }
        return (lastId = currentValue);

    }


}
