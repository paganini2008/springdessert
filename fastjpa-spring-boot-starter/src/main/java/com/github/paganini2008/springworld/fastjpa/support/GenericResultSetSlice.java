package com.github.paganini2008.springworld.fastjpa.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * GenericResultSetSlice
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class GenericResultSetSlice<T> implements ResultSetSlice<T> {

	private final String sql;
	private final Object[] arguments;
	private final EntityManager em;

	protected GenericResultSetSlice(String sql, Object[] arguments, EntityManager em) {
		this.sql = sql;
		this.arguments = arguments;
		this.em = em;
	}

	public int rowCount() {
		Query query = em.createNativeQuery(rewriteCountableSql(sql), Integer.class);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		Object result = query.getSingleResult();
		return result instanceof Number ? ((Number) result).intValue() : 0;
	}

	public List<T> list(int maxResults, int firstResult) {
		Query query = em.createNativeQuery(sql);
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
		return getResultList(query);
	}
	
	protected String rewriteCountableSql(String sql) {
		return new StringBuilder("select count(1) as rowCount from (").append(sql).append(")").toString();
	}

	protected abstract List<T> getResultList(Query query);

}
