package com.github.doodler.common.id;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.InitializingBean;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RemoteStringIdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RemoteStringIdGenerator extends AbstractStringIdGenerator implements InitializingBean {

    private final RemoteIdBatchGenerator idBatchGenerator;
    private final int count;

    private final Queue<String> q = new ConcurrentLinkedQueue<>();

    @Override
    public String getNextId() {
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
        Collection<String> datas = idBatchGenerator.createManyStrings(count);
        if (datas != null) {
            q.addAll(datas);
        }
        return datas != null ? datas.size() : 0;
    }

}
