package io.doodler.common.context;

/**
 * @Description: RequestContextExchanger
 * @Author: Fred Feng
 * @Date: 21/09/2023
 * @Version 1.0.0
 */
public interface RequestContextExchanger<T> {

	T get();

	void set(T obj);

	void reset();
}