package indi.atlantis.framework.fastjpa;

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

	<T> T getResult(Class<T> requiredType);

	<T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer);

}
