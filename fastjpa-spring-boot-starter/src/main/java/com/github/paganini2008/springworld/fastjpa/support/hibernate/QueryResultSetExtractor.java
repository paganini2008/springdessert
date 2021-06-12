package com.github.paganini2008.springworld.fastjpa.support.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

/**
 * 
 * QueryResultSetExtractor
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface QueryResultSetExtractor<T> {

	List<T> extractData(Session session, NativeQuery<?> query);

}
