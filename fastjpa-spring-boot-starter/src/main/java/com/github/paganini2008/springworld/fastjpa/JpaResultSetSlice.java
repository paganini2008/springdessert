package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Selection;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaResultSetSlice
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class JpaResultSetSlice<E, T> implements ResultSetSlice<T> {

	private final Model<E> model;
	private final PagingCriteriaQuery query;
	private final JpaCustomQuery<?> customQuery;
	private final Transformer<E, T> transformer;

	JpaResultSetSlice(Model<E> model, PagingCriteriaQuery query, JpaCustomQuery<?> customQuery, Transformer<E, T> transformer) {
		this.model = model;
		this.query = query;
		this.customQuery = customQuery;
		this.transformer = transformer;
	}

	public int rowCount() {
		return query.rowCount(model, customQuery);
	}

	public List<T> list(int maxResults, int firstResult) {
		List<Tuple> tuples = query.list(model, customQuery, maxResults, firstResult);
		List<T> results = new ArrayList<T>();
		List<Selection<?>> selections = query.getQuery().getSelection().getCompoundSelectionItems();
		for (Tuple tuple : tuples) {
			T entity = transformer.transfer(model, selections, tuple);
			results.add(entity);
		}
		return results;
	}
}
