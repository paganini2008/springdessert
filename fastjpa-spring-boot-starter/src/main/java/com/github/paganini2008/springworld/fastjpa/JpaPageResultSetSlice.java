package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Selection;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaPageResultSetSlice
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public class JpaPageResultSetSlice<T, R> implements ResultSetSlice<R> {

	private final Model<?> model;
	private final CriteriaQuery<T> query;
	private final CriteriaQuery<Long> counter;
	private final JpaCustomQuery<?> customQuery;
	private final Transformer<T, R> transformer;

	JpaPageResultSetSlice(Model<?> model, CriteriaQuery<T> query, CriteriaQuery<Long> counter, JpaCustomQuery<?> customQuery,
			Transformer<T, R> transformer) {
		this.model = model;
		this.query = query;
		this.counter = counter;
		this.customQuery = customQuery;
		this.transformer = transformer;
	}

	@Override
	public int rowCount() {
		List<Long> list = customQuery.getResultList(builder -> {
			counter.select(builder.toLong(builder.literal(1)));
			return counter;
		});
		return list != null ? list.size() : 0;
	}

	@Override
	public List<R> list(int maxResults, int firstResult) {
		List<T> list = customQuery.getResultList(builder -> query, maxResults, firstResult);
		List<R> results = new ArrayList<R>();
		List<Selection<?>> selections = query.getSelection().getCompoundSelectionItems();
		for (T t : list) {
			R data = transformer.transfer(model, selections, t);
			results.add(data);
		}
		return results;
	}

}
