package com.github.paganini2008.springworld.fastjpa.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * 
 * SimpleNativeQueryResultSetSlice
 *
 * @author Fred Feng
 * @version 1.0
 */
public class SimpleNativeQueryResultSetSlice<E> extends AbstractNativeQueryResultSetSlice<E> {

	public SimpleNativeQueryResultSetSlice(String sql, Object[] arguments, EntityManager em, Class<E> entityClass) {
		super(sql, arguments, em);
		this.entityClass = entityClass;
	}

	private final Class<E> entityClass;

	@SuppressWarnings("unchecked")
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

	public Class<E> getEntityClass() {
		return entityClass;
	}

}
