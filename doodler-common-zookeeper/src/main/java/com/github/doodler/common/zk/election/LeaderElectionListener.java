package com.github.doodler.common.zk.election;

/**
 * 
 * @Description: LeaderElectionListener
 * @Author: Fred Feng
 * @Date: 02/08/2024
 * @Version 1.0.0
 */
public interface LeaderElectionListener {

    void whenTakeLeadership();

    default void whenAbdicateLeadership() {
    }

}
