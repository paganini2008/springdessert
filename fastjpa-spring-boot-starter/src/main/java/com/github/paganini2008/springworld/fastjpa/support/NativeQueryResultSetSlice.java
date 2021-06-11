package com.github.paganini2008.springworld.fastjpa.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * 
 * NativeQueryResultSetSlice
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class NativeQueryResultSetSlice<T> extends AbstractNativeQueryResultSetSlice<T> {

	protected NativeQueryResultSetSlice(String sql, Object[] arguments, EntityManager em) {
		super(sql, arguments, em);
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

	protected abstract List<T> getResultList(Query query);

}
