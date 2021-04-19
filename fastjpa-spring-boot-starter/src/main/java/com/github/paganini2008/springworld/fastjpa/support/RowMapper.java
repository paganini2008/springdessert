package com.github.paganini2008.springworld.fastjpa.support;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * RowMapper
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface RowMapper<T> {

	T mapRow(Tuple tuple);

}
