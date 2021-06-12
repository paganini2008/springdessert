/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaQuery;

/**
 * 
 * JpaQuery
 * 
 * @author Fred Feng
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

	JpaQueryResultSet<T> selectThis();

	JpaQueryResultSet<T> selectAlias(String... tableAliases);

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
