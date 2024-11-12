package com.github.doodler.common.id;

/**
 * 
 * @Description: IdGenerators
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public abstract class IdGenerators {

    public static class SimpleIdGeneratorFactoryBean implements IdGeneratorFactory {

        @Override
        public IdGenerator getObject() throws Exception {
            return new SimpleIdGenerator();
        }

        @Override
        public Class<?> getObjectType() {
            return SimpleIdGenerator.class;
        }

    }

    public static class SimpleStringIdGeneratorFactoryBean implements StringIdGeneratorFactory {

        @Override
        public StringIdGenerator getObject() throws Exception {
            return new SimpleStringIdGenerator();
        }

        @Override
        public Class<?> getObjectType() {
            return SimpleStringIdGenerator.class;
        }

    }

    public static class TimeBasedIdGeneratorFactoryBean implements IdGeneratorFactory {

        @Override
        public IdGenerator getObject() throws Exception {
            return new TimeBasedIdGenerator();
        }

        @Override
        public Class<?> getObjectType() {
            return TimeBasedIdGenerator.class;
        }

    }

    public static class TimeBasedStringIdGeneratorFactoryBean implements StringIdGeneratorFactory {

        @Override
        public StringIdGenerator getObject() throws Exception {
            return new TimeBasedStringIdGenerator();
        }

        @Override
        public Class<?> getObjectType() {
            return TimeBasedStringIdGenerator.class;
        }

    }
}
