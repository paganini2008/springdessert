package com.github.paganini2008.springworld.fastjpa.support;

import java.util.Map;

import com.github.paganini2008.devtools.collection.CaseInsensitiveMap;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * NativeSqlOperations
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface NativeSqlOperations<E> {

	ResultSetSlice<E> select(String sql, Object[] arguments);

	<T> T getSingleResult(String sql, Object[] arguments, Class<T> requiredType);

	<T> ResultSetSlice<T> select(String sql, Object[] arguments, Class<T> resultClass);

	<T> ResultSetSlice<T> select(String sql, Object[] arguments, RowMapper<T> rowMapper);

	default ResultSetSlice<Map<String, Object>> selectForMap(String sql, Object[] arguments) {
		return select(sql, arguments, (index, data) -> new CaseInsensitiveMap<>(data));
	}

	<T> T execute(String sql, Object[] arguments, ResultSetExtractor<T> extractor);

	int executeUpdate(String sql, Object[] arguments);

}
