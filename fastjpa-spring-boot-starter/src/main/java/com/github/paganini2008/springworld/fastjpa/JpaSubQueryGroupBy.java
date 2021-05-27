package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaSubQueryGroupBy
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface JpaSubQueryGroupBy<E, T> {

	JpaSubQueryGroupBy<E, T> having(Filter filter);

	default JpaSubQueryGroupBy<E, T> select(String attributeName){
		return select(null, attributeName);
	}

	JpaSubQueryGroupBy<E, T> select(String alias, String attributeName);

	JpaSubQueryGroupBy<E, T> select(Field<T> field);

}
