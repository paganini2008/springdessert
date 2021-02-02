package org.springdessert.framework.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Subquery;

/**
 * 
 * SubQueryBuilder
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface SubQueryBuilder<T> {

	Subquery<T> toSubquery(CriteriaBuilder builder);

}
