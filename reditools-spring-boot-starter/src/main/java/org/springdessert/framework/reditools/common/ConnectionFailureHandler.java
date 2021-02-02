package org.springdessert.framework.reditools.common;

/**
 * 
 * ConnectionFailureHandler
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface ConnectionFailureHandler {

	void handleException(Throwable e);
	
}
