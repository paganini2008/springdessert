package org.springdessert.framework.cluster.multicast;

import static org.springdessert.framework.cluster.Constants.APPLICATION_CLUSTER_NAMESPACE;

import org.springdessert.framework.cluster.ApplicationInfo;
import org.springdessert.framework.cluster.InstanceId;
import org.springdessert.framework.reditools.messager.RedisMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 
 * ApplicationMulticastStarter
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ApplicationMulticastStarter implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Autowired
	private InstanceId instanceId;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationInfo applicationInfo = instanceId.getApplicationInfo();
		final String channel = APPLICATION_CLUSTER_NAMESPACE + clusterName + ":active";
		redisMessageSender.sendMessage(channel, applicationInfo);
	}

}
