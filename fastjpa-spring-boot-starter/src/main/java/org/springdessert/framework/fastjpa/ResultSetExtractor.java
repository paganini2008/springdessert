package org.springdessert.framework.fastjpa;

import javax.persistence.Query;

/**
 * 
 * ResultSetExtractor
 * 
 * @author Jimmy Hoff
 * 
 */
public interface ResultSetExtractor<T> {

	T extractData(Query query);

}
