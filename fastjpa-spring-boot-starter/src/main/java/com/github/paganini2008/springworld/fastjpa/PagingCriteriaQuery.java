package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

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
	private final CriteriaQuery<Tuple> count;

	PagingCriteriaQuery(CriteriaQuery<Tuple> query, CriteriaQuery<Tuple> count) {
		this.query = query;
		this.count = count;
	}

	public void where(Predicate restriction) {
		if (restriction != null) {
			query.where(restriction);
			count.where(restriction);
		}
	}

	public void groupBy(List<Expression<?>> grouping) {
		query.groupBy(grouping);
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
		Tuple tuple = customQuery.getSingleResult(builder -> {
			if (query.isDistinct()) {
				count.multiselect(Fields.countDistinct(Fields.root()).toExpression(model, builder));
			} else {
				count.multiselect(Fields.count(Fields.root()).toExpression(model, builder));
			}
			return count;
		});
		Object result = tuple.get(0);
		return result instanceof Number ? ((Number) result).intValue() : 0;
	}

	public List<Tuple> list(Model<?> model, JpaCustomQuery<?> customQuery, int maxResults, int firstResult) {
		return customQuery.getResultList(build -> query, maxResults, firstResult);
	}

	CriteriaQuery<Tuple> getQuery() {
		return query;
	}

	CriteriaQuery<Tuple> getCount() {
		return count;
	}

}
