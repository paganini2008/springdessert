package com.github.paganini2008.springworld.fastjpa.support;

import java.util.Map;

/**
 * 
 * RowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
@FunctionalInterface
public interface RowMapper<T> {

	T mapRow(int index, Map<String, Object> map);

}
