package org.springdessert.framework.fastjpa.support;

import org.springdessert.framework.fastjpa.ResultSetExtractor;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * NativeSqlOperations
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface NativeSqlOperations<E> {

	ResultSetSlice<E> select(String sql, Object[] arguments);

	<T> T getSingleResult(String sql, Object[] arguments, Class<T> requiredType);

	default ResultSetSlice<Tuple> selectForTuple(String sql, Object[] arguments) {
		return select(sql, arguments, tuple -> tuple);
	}

	default <T> ResultSetSlice<T> select(String sql, Object[] arguments, Class<T> resultClass) {
		return select(sql, arguments, new BeanPropertyRowMapper<T>(resultClass));
	}

	<T> ResultSetSlice<T> select(String sql, Object[] arguments, RowMapper<T> mapper);

	<T> T execute(String sql, Object[] arguments, ResultSetExtractor<T> extractor);

	int executeUpdate(String sql, Object[] arguments);

}
