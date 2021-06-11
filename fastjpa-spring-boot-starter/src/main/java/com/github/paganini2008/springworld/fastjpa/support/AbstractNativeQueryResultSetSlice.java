package com.github.paganini2008.springworld.fastjpa.support;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.query.QueryUtils;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * AbstractNativeQueryResultSetSlice
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public abstract class AbstractNativeQueryResultSetSlice<T> implements ResultSetSlice<T> {

	protected final String sql;
	protected final Object[] arguments;
	protected final EntityManager em;

	protected AbstractNativeQueryResultSetSlice(String sql, Object[] arguments, EntityManager em) {
		this.sql = sql;
		this.arguments = arguments;
		this.em = em;
	}

	public int rowCount() {
		Query query = em.createNativeQuery(getCountQuerySqlString(sql));
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		Object result = query.getSingleResult();
		return result instanceof Number ? ((Number) result).intValue() : 0;
	}

	protected String getCountQuerySqlString(String sql) {
		return String.format(QueryUtils.COUNT_QUERY_STRING, "1", "(" + sql + ")");
	}

}
