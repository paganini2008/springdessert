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
 * @author Fred Feng
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
		Long result = customQuery.getSingleResult(builder -> {
			counter.select(builder.count(builder.toInteger(builder.literal(1))));
			return counter;
		});
		return result != null ? result.intValue() : 0;
	}

	@Override
	public List<R> list(int maxResults, int firstResult) {
		List<R> results = new ArrayList<R>();
		List<T> list = customQuery.getResultList(builder -> query, maxResults, firstResult);
		if (query.getResultType() == model.getRootType()) {
			for (T original : list) {
				R data = transformer.transfer(model, original);
				results.add(data);
			}
		} else {
			List<Selection<?>> selections = query.getSelection().getCompoundSelectionItems();
			for (T original : list) {
				R data = transformer.transfer(model, selections, original);
				results.add(data);
			}
		}
		return results;
	}

}
