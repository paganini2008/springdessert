package com.github.paganini2008.springworld.fastjpa;

import com.github.paganini2008.devtools.jdbc.Listable;

/**
 * 
 * JpaQueryResultSet
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface JpaQueryResultSet<T> extends Listable<T> {

	<R> Listable<R> setTransformer(Transformer<T, R> transformer);
	
}
