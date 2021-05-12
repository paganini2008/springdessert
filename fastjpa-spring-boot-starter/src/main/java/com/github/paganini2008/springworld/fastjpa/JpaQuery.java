package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaQuery;

/**
 * 
 * JpaQuery
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public interface JpaQuery<E, T> {

	JpaQuery<E, T> filter(Filter filter);

	JpaQuery<E, T> sort(JpaSort... sorts);

	default JpaGroupBy<E, T> groupBy(String... attributeNames) {
		return groupBy(new FieldList().addFields(attributeNames));
	}

	default JpaGroupBy<E, T> groupBy(String alias, String[] attributeNames) {
		return groupBy(new FieldList().addFields(alias, attributeNames));
	}

	default JpaGroupBy<E, T> groupBy(Field<?>... fields) {
		return groupBy(new FieldList(fields));
	}

	JpaGroupBy<E, T> groupBy(FieldList fieldList);

	default JpaQueryResultSet<T> selectThis() {
		return selectAlias(Model.ROOT);
	}

	JpaQueryResultSet<T> selectAlias(String... tableAlias);

	default JpaQueryResultSet<T> select(String... attributeNames) {
		return select(new ColumnList().addColumns(attributeNames));
	}

	default JpaQueryResultSet<T> select(String alias, String... attributeNames) {
		return select(new ColumnList().addColumns(alias, attributeNames));
	}

	default JpaQueryResultSet<T> select(Column... columns) {
		return select(new ColumnList(columns));
	}

	default JpaQueryResultSet<T> select(Field<?>... fields) {
		return select(new ColumnList().addColumns(fields));
	}

	JpaQueryResultSet<T> select(ColumnList columnList);

	default T one(String attributeName) {
		return one(Column.forName(attributeName));
	}

	default T one(String alias, String attributeName) {
		return one(Column.forName(alias, attributeName));
	}

	default T one(Field<T> field) {
		return one(field.as(field.toString()));
	}

	T one(Column column);

	JpaQuery<E, T> distinct(boolean distinct);

	default <X> JpaQuery<X, T> join(String attributeName, String alias) {
		return join(attributeName, alias, null);
	}

	default <X> JpaQuery<X, T> leftJoin(String attributeName, String alias) {
		return leftJoin(attributeName, alias, null);
	}

	default <X> JpaQuery<X, T> rightJoin(String attributeName, String alias) {
		return rightJoin(attributeName, alias, null);
	}

	<X> JpaQuery<X, T> join(String attributeName, String alias, Filter on);

	<X> JpaQuery<X, T> leftJoin(String attributeName, String alias, Filter on);

	<X> JpaQuery<X, T> rightJoin(String attributeName, String alias, Filter on);

	<X> JpaSubQuery<X, X> subQuery(Class<X> entityClass, String alias);

	<X, Y> JpaSubQuery<X, Y> subQuery(Class<X> entityClass, String alias, Class<Y> resultClass);

	CriteriaQuery<T> query();

	Model<E> model();

}
