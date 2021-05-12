package com.github.paganini2008.springworld.fastjpa;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaPageResultSet
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface JpaPageResultSet<T> extends ResultSetSlice<T> {

	<R> ResultSetSlice<R> setTransformer(Transformer<T, R> transformer);

}
