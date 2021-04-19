package com.github.paganini2008.springworld.fastjpa;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaResultSet
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface JpaResultSet<E> extends ResultSetSlice<E> {

	<T> T getSingleResult(Class<T> requiredType);

	<T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer);

}
