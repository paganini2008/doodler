package id.doodler.id;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 
 * @Description: SnowFlakeIdGenerator
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public class SnowFlakeIdGenerator implements IdGenerator {

    public SnowFlakeIdGenerator(long workerId, long datacenterId) {
        this.snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    }

    private Snowflake snowflake;
    private volatile long latestId;

    @Override
    public long currentId() {
        return latestId;
    }

    @Override
    public long generateId() {
        return (latestId = snowflake.nextId());
    }
}