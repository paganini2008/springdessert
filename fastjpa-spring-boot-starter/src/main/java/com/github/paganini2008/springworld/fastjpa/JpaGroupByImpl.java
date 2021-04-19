package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Selection;

import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JpaGroupByImpl
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class JpaGroupByImpl<E> implements JpaGroupBy<E>, JpaResultSet<E> {

	private final Model<E> model;
	private final PagingCriteriaQuery query;
	private final CriteriaBuilder builder;
	private final JpaResultSet<E> resultSet;

	JpaGroupByImpl(Model<E> model, PagingCriteriaQuery query, CriteriaBuilder builder, JpaResultSet<E> resultSet) {
		this.model = model;
		this.query = query;
		this.builder = builder;
		this.resultSet = resultSet;
	}

	public JpaGroupBy<E> having(Filter filter) {
		query.having(filter.toPredicate(model, builder));
		return this;
	}

	public JpaResultSet<E> select(ColumnList columnList) {
		if (columnList != null) {
			List<Selection<?>> selections = new ArrayList<Selection<?>>();
			for (Column column : columnList) {
				selections.add(column.toSelection(model, builder));
			}
			query.multiselect(selections);
		}
		return this;
	}

	public JpaGroupBy<E> sort(JpaSort... sorts) {
		List<Order> orders = new ArrayList<Order>();
		for (JpaSort sort : sorts) {
			orders.add(sort.toOrder(model, builder));
		}
		if (!orders.isEmpty()) {
			query.orderBy(orders);
		}
		return this;
	}

	public <T> T getSingleResult(Class<T> requiredType) {
		return resultSet.getSingleResult(requiredType);
	}

	public int rowCount() {
		return resultSet.rowCount();
	}

	public List<E> list(int maxResults, int firstResult) {
		return resultSet.list(maxResults, firstResult);
	}

	public PageResponse<E> list(PageRequest pageRequest) {
		return resultSet.list(pageRequest);
	}

	public <T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer) {
		return resultSet.setTransformer(transformer);
	}

}
