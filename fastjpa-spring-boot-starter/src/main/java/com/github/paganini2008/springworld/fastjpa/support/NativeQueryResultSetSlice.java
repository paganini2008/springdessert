package com.github.paganini2008.springworld.fastjpa.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.query.QueryUtils;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * NativeQueryResultSetSlice
 *
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class NativeQueryResultSetSlice<E> implements ResultSetSlice<E> {

	NativeQueryResultSetSlice(String sql, Object[] arguments, Class<E> entityClass, EntityManager em) {
		this.sql = sql;
		this.arguments = arguments;
		this.em = em;
		this.entityClass = entityClass;
	}

	protected final String sql;
	protected final Object[] arguments;
	protected final EntityManager em;
	protected final Class<E> entityClass;

	public List<E> list(int maxResults, int firstResult) {
		Query query = em.createNativeQuery(sql, entityClass);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		if (firstResult >= 0) {
			query.setFirstResult(firstResult);
		}
		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
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
