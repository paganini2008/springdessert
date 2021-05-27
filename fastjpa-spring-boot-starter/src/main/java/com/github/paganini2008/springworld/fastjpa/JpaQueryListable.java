package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Selection;

import com.github.paganini2008.devtools.jdbc.Listable;

/**
 * 
 * JpaQueryListable
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class JpaQueryListable<T, R> implements Listable<R> {

	private final Model<?> model;
	private final CriteriaQuery<T> query;
	private final JpaCustomQuery<?> customQuery;
	private final Transformer<T, R> transformer;

	JpaQueryListable(Model<?> model, CriteriaQuery<T> query, JpaCustomQuery<?> customQuery, Transformer<T, R> transformer) {
		this.model = model;
		this.query = query;
		this.customQuery = customQuery;
		this.transformer = transformer;
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
