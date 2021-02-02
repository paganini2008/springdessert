package org.springdessert.framework.cluster.http;

import org.springdessert.framework.cluster.ApplicationInfo;
import org.springdessert.framework.cluster.election.ApplicationClusterFollowerEvent;
import org.springdessert.framework.cluster.election.LeaderNotFoundException;
import org.springframework.context.ApplicationListener;

/**
 * 
 * LeaderRoutingAllocator
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class LeaderRoutingAllocator implements RoutingAllocator, ApplicationListener<ApplicationClusterFollowerEvent> {

	private ApplicationInfo leaderInfo;

	@Override
	public void onApplicationEvent(ApplicationClusterFollowerEvent event) {
		this.leaderInfo = event.getLeaderInfo();
	}

	@Override
	public String allocateHost(String provider, String path) {
		if (leaderInfo == null) {
			throw new LeaderNotFoundException();
		}
		return leaderInfo.getApplicationContextPath() + path;
	}

}
