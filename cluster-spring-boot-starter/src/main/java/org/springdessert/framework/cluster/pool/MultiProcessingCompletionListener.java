package org.springdessert.framework.cluster.pool;

import org.springdessert.framework.cluster.ApplicationInfo;
import org.springdessert.framework.cluster.multicast.ApplicationMessageListener;
import org.springdessert.framework.reditools.messager.RedisMessageSender;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * MultiProcessingCompletionListener
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class MultiProcessingCompletionListener implements ApplicationMessageListener {

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Override
	public void onMessage(ApplicationInfo applicationInfo, String id, Object message) {
		redisMessageSender.sendMessage(((Return) message).getInvocation().getId(), message);
	}

	@Override
	public String getTopic() {
		return MultiProcessingCompletionListener.class.getName();
	}

}
