package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.Tuple;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaResultSetImpl
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class JpaResultSetImpl<E> implements JpaResultSet<E> {

	private final Model<E> model;
	private final PagingCriteriaQuery query;
	private final JpaCustomQuery<?> customQuery;

	JpaResultSetImpl(Model<E> model, PagingCriteriaQuery query, JpaCustomQuery<?> customQuery) {
		this.model = model;
		this.query = query;
		this.customQuery = customQuery;
	}

	public <T> T getSingleResult(Class<T> requiredType) {
		Tuple tuple = customQuery.getSingleResult(builder -> query.getQuery());
		Object result = tuple.get(0);
		if (result != null) {
			try {
				return requiredType.cast(result);
			} catch (RuntimeException e) {
				result = ConvertUtils.convertValue(result, requiredType);
				return requiredType.cast(result);
			}
		}
		return null;
	}

	public int rowCount() {
		return query.rowCount(model, customQuery);
	}

	public List<E> list(int maxResults, int firstResult) {
		return setTransformer(Transformers.asBean(model.getType())).list(maxResults, firstResult);
	}

	public <T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer) {
		return new JpaResultSetSlice<E, T>(model, query, customQuery, transformer);
	}
}
