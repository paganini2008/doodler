package io.doodler.cluster;

/**
 * @Description: LeaderElectionEvent
 * @Author: Fred Feng
 * @Date: 22/07/2023
 * @Version 1.0.0
 */
public class LeaderElectionEvent extends ApplicationClusterEvent {

	private static final long serialVersionUID = 1828966853792723786L;

	public LeaderElectionEvent(Object source, ServiceInstance leaderInstance, boolean leader) {
		super(source);
		this.leaderInstance = leaderInstance;
		this.leader=leader;
	}

	private final ServiceInstance leaderInstance;
	private final boolean leader;

	public ServiceInstance getLeaderInstance() {
		return leaderInstance;
	}

	public boolean isLeader() {
		return leader;
	}
}