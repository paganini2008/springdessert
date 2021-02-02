package org.springdessert.framework.cluster.election;

import org.springdessert.framework.cluster.ApplicationClusterEvent;
import org.springdessert.framework.cluster.HealthState;
import org.springframework.context.ApplicationContext;

/**
 * 
 * ApplicationClusterLeaderEvent
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ApplicationClusterLeaderEvent extends ApplicationClusterEvent {

	private static final long serialVersionUID = -2932470508571995512L;

	public ApplicationClusterLeaderEvent(ApplicationContext applicationContext) {
		this(applicationContext, HealthState.LEADABLE);
	}

	public ApplicationClusterLeaderEvent(ApplicationContext applicationContext, HealthState healthState) {
		super(applicationContext, healthState);
	}

}
