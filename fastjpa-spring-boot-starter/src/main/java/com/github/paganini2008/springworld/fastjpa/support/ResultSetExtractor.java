package com.github.paganini2008.springworld.fastjpa.support;

import javax.persistence.Query;

/**
 * 
 * ResultSetExtractor
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ResultSetExtractor<T> {

	T extractData(Query query);

}
