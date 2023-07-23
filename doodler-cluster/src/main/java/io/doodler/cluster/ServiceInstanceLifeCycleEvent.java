package io.doodler.cluster;

/**
 * @Description: ServiceInstanceLifeCycleEvent
 * @Author: Fred Feng
 * @Date: 21/07/2023
 * @Version 1.0.0
 */
public class ServiceInstanceLifeCycleEvent extends ApplicationClusterEvent {

	private static final long serialVersionUID = 1547337967538767692L;

	public ServiceInstanceLifeCycleEvent(Object source, ServiceInstance serviceInstance,
			LifeCycleState state) {
		super(source);
		this.serviceInstance = serviceInstance;
		this.state = state;
	}

	private final ServiceInstance serviceInstance;
	private final LifeCycleState state;

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public LifeCycleState getState() {
		return state;
	}
}