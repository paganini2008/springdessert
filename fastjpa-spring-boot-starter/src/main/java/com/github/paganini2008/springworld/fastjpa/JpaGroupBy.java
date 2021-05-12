package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaQuery;

/**
 * 
 * JpaGroupBy
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public interface JpaGroupBy<E, T> {

	JpaGroupBy<E, T> having(Filter expression);

	default JpaQueryResultSet<T> select(String... attributeNames) {
		return select(new ColumnList().addColumns(attributeNames));
	}

	default JpaQueryResultSet<T> select(String alias, String[] attributeNames) {
		return select(new ColumnList().addColumns(alias, attributeNames));
	}

	default JpaQueryResultSet<T> select(Column... columns) {
		return select(new ColumnList(columns));
	}

	default JpaQueryResultSet<T> select(Field<?>... fields) {
		return select(new ColumnList().addColumns(fields));
	}

	JpaQueryResultSet<T> select(ColumnList columnList);

	JpaGroupBy<E, T> sort(JpaSort... sorts);

	CriteriaQuery<T> query();
	
	Model<E> model();

}
