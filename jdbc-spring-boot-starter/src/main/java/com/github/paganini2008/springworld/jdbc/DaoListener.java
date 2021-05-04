package com.github.paganini2008.springworld.jdbc;

/**
 * 
 * DaoListener
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public interface DaoListener {

	default void beforeExecution(long startTime, StringBuilder sql, Object[] args, Object handler) {
	}

	default void afterExecution(long startTime, String sql, Object[] args, Object handler) {
	}

}
