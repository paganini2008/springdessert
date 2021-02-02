package org.springdessert.framework.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * 
 * Filter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Filter {

	public Predicate toPredicate(Model<?> model, CriteriaBuilder builder);

}
