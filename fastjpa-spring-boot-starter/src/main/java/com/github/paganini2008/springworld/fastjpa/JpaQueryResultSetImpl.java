package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;

import com.github.paganini2008.devtools.jdbc.Listable;

/**
 * 
 * JpaQueryResultSetImpl
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class JpaQueryResultSetImpl<T> implements JpaQueryResultSet<T> {

	private final Model<?> model;
	private final CriteriaQuery<T> query;
	private final JpaCustomQuery<?> customQuery;

	JpaQueryResultSetImpl(Model<?> model, CriteriaQuery<T> query, JpaCustomQuery<?> customQuery) {
		this.model = model;
		this.query = query;
		this.customQuery = customQuery;
	}

	@Override
	public List<T> list(int maxResults, int firstResult) {
		return customQuery.getResultList(builder -> query, maxResults, firstResult);
	}

	@Override
	public <R> Listable<R> setTransformer(Transformer<T, R> transformer) {
		return new JpaQueryListable<T, R>(model, query, customQuery, transformer);
	}
}
