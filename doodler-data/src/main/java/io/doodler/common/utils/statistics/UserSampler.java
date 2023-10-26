package io.doodler.common.utils.statistics;

/**
 * @Description: UserSampler
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public class UserSampler<T> implements Sampler<T> {

	private final long timestamp;
	private final T sample;

	public UserSampler(long timestamp, T sample) {
		this.timestamp = timestamp;
		this.sample = sample;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public T getSample() {
		return sample;
	}
}