package indi.atlantis.framework.fastjpa;

import javax.persistence.Query;

/**
 * 
 * ResultSetExtractor
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface ResultSetExtractor<T> {

	T extractData(Query query);

}
