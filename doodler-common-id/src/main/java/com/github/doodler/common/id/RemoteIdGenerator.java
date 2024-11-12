package com.github.doodler.common.id;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.InitializingBean;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RemoteIdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RemoteIdGenerator extends AbstractIdGenerator implements InitializingBean {

    private final RemoteIdBatchGenerator idBatchGenerator;
    private final int count;

    private final Queue<Long> q = new ConcurrentLinkedQueue<>();

    @Override
    public Long getNextId() {
        lastId = q.poll();
        if (lastId == null) {
            if (refill() == 0) {
                throw new IdRunoutException();
            }
        }
        return lastId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refill();
    }

    private int refill() {
        Collection<Long> datas = idBatchGenerator.createManyLongs(count);
        if (datas != null) {
            q.addAll(datas);
        }
        return datas != null ? datas.size() : 0;
    }

}
