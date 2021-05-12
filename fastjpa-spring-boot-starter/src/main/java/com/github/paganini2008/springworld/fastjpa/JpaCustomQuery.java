package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

/**
 * 
 * JpaCustomQuery
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface JpaCustomQuery<X> {

	<T> T getSingleResult(JpaQueryCallback<T> callback);

	<T> List<T> getResultList(JpaQueryCallback<T> callback);

	<T> List<T> getResultList(JpaQueryCallback<T> callback, int limit, int offset);
}
