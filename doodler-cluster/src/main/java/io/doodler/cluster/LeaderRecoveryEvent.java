package io.doodler.cluster;

/**
 * 
 * @Description: LeaderRecoveryEvent
 * @Author: Fred Feng
 * @Date: 23/07/2023
 * @Version 1.0.0
 */
public class LeaderRecoveryEvent extends ApplicationClusterEvent{
	
	private static final long serialVersionUID = 8528126433485672169L;

	public LeaderRecoveryEvent(Object source,ServiceInstance lostLeaderInstance) {
		super(source);
		this.lostLeaderInstance=lostLeaderInstance;
	}

	private final ServiceInstance lostLeaderInstance;

	public ServiceInstance getLostLeaderInstance() {
		return lostLeaderInstance;
	}
	
	
	
	

}
