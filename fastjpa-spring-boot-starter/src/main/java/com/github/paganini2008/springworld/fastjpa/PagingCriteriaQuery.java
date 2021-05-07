package com.github.paganini2008.springworld.fastjpa;

import java.util.Collections;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * PagingCriteriaQuery
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public final class PagingCriteriaQuery {

	private final CriteriaQuery<Tuple> query;

	PagingCriteriaQuery(CriteriaQuery<Tuple> query) {
		this.query = query;
	}

	public void where(Predicate restriction) {
		if (restriction != null) {
			query.where(restriction);
		}
	}

	public void groupBy(List<Expression<?>> grouping) {
		if (CollectionUtils.isNotEmpty(grouping)) {
			query.groupBy(grouping);
		}
	}

	public void having(Predicate restriction) {
		if (restriction != null) {
			query.having(restriction);
		}
	}

	public <X> Subquery<X> subquery(Class<X> clz) {
		return query.subquery(clz);
	}

	public void multiselect(List<Selection<?>> selectionList) {
		query.multiselect(selectionList);
	}

	public void distinct(boolean distinct) {
		query.distinct(distinct);
	}

	public void orderBy(List<Order> orders) {
		query.orderBy(orders);
	}

	public int rowCount(Model<?> model, JpaCustomQuery<?> customQuery) {
		final List<Selection<?>> selectionList = query.getSelection().getCompoundSelectionItems();
		final List<Order> orderList = query.getOrderList();
		List<Tuple> tupleList = customQuery.getResultList(builder -> {
			if (query.isDistinct()) {
				query.multiselect(Fields.countDistinct(Fields.root()).toExpression(model, builder));
			} else {
				query.multiselect(Fields.count(Fields.root()).toExpression(model, builder));
			}
			query.orderBy(Collections.<Order>emptyList());
			return query;
		});
		query.multiselect(selectionList);
		query.orderBy(orderList);
		if (CollectionUtils.isEmpty(tupleList)) {
			return 0;
		}
		if (CollectionUtils.isNotEmpty(query.getGroupList()) && tupleList.size() > 1) {
			return tupleList.size();
		} else {
			Tuple tuple = tupleList.get(0);
			Object result = tuple.get(0);
			return result instanceof Number ? ((Number) result).intValue() : 0;
		}
	}

	public List<Tuple> list(Model<?> model, JpaCustomQuery<?> customQuery, int maxResults, int firstResult) {
		return customQuery.getResultList(build -> query, maxResults, firstResult);
	}

	CriteriaQuery<Tuple> getQuery() {
		return query;
	}

}
