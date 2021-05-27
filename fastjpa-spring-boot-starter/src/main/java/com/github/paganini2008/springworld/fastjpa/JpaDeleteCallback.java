package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

/**
 * 
 * JpaDeleteCallback
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface JpaDeleteCallback<T> {

	CriteriaDelete<T> doInJpa(CriteriaBuilder builder);

}
