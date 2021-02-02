package org.springdessert.framework.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;

/**
 * 
 * JpaUpdateCallback
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface JpaUpdateCallback<T> {

	CriteriaUpdate<T> doInJpa(CriteriaBuilder builder);

}
