package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaPageResultSetImpl
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public class JpaPageResultSetImpl<T> implements JpaPageResultSet<T> {

	private final Model<?> model;
	private final CriteriaQuery<T> query;
	private final CriteriaQuery<Long> counter;
	private final JpaCustomQuery<?> customQuery;

	JpaPageResultSetImpl(Model<?> model, CriteriaQuery<T> query, CriteriaQuery<Long> counter, JpaCustomQuery<?> customQuery) {
		this.model = model;
		this.query = query;
		this.counter = counter;
		this.customQuery = customQuery;
	}

	@Override
	public List<T> list(int maxResults, int firstResult) {
		return customQuery.getResultList(builder -> query, maxResults, firstResult);
	}

	@Override
	public int rowCount() {
		Long result = customQuery.getSingleResult(builder -> {
			counter.select(builder.count(builder.toInteger(builder.literal(1))));
			return counter;
		});
		return result != null ? result.intValue() : 0;
	}

	@Override
	public <R> ResultSetSlice<R> setTransformer(Transformer<T, R> transformer) {
		return new JpaPageResultSetSlice<T, R>(model, query, counter, customQuery, transformer);
	}

}
