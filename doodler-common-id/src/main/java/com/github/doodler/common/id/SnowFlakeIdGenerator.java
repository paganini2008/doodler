package com.github.doodler.common.id;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 
 * @Description: SnowFlakeIdGenerator
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public class SnowFlakeIdGenerator extends AbstractIdGenerator {

    public SnowFlakeIdGenerator(long workerId, long datacenterId) {
        this.snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    }

    private Snowflake snowflake;

    @Override
    public Long getNextId() {
        return (lastId = snowflake.nextId());
    }
}
