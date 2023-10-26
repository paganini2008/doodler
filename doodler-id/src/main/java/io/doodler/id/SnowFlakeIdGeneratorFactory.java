package io.doodler.id;

/**
 * 
 * @Description: SnowFlakeIdGeneratorFactory
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
public class SnowFlakeIdGeneratorFactory implements IdGeneratorFactory {

    public SnowFlakeIdGeneratorFactory() {
        this(0, 0);
    }

    public SnowFlakeIdGeneratorFactory(long workerId, long datacenterId) {
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    private long workerId;
    private long datacenterId;

    @Override
    public IdGenerator getObject() throws Exception {
        return new SnowFlakeIdGenerator(workerId, datacenterId);
    }

    @Override
    public Class<?> getObjectType() {
        return SnowFlakeIdGenerator.class;
    }
}