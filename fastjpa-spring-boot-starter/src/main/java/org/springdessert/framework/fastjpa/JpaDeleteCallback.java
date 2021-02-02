package org.springdessert.framework.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

/**
 * 
 * JpaDeleteCallback
 * 
 * @author Jimmy Hoff
 * 
 */
public interface JpaDeleteCallback<T> {

	CriteriaDelete<T> doInJpa(CriteriaBuilder builder);

}
