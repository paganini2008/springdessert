package org.springdessert.framework.cluster.consistency;

import org.springdessert.framework.cluster.ApplicationInfo;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * ConsistencyRequestCompletionEvent
 *
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ConsistencyRequestCompletionEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6200632827461240339L;

	public ConsistencyRequestCompletionEvent(ConsistencyRequest consistencyRequest, ApplicationInfo applicationInfo) {
		super(consistencyRequest);
		this.applicationInfo = applicationInfo;
	}

	private final ApplicationInfo applicationInfo;

	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

}
