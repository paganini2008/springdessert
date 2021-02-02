package org.springdessert.framework.cluster.election;

import org.springdessert.framework.cluster.ApplicationClusterEvent;
import org.springdessert.framework.cluster.ApplicationInfo;
import org.springdessert.framework.cluster.HealthState;
import org.springframework.context.ApplicationContext;

/**
 * 
 * ApplicationClusterRefreshedEvent
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ApplicationClusterRefreshedEvent extends ApplicationClusterEvent {

	private static final long serialVersionUID = 3115067071903624457L;

	public ApplicationClusterRefreshedEvent(ApplicationContext applicationContext, ApplicationInfo leader) {
		super(applicationContext, HealthState.LEADABLE);
		this.leader = leader;
	}

	private final ApplicationInfo leader;

	public ApplicationInfo getLeaderInfo() {
		return leader;
	}

}
