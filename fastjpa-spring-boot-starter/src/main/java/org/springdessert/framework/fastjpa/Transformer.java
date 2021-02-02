package org.springdessert.framework.fastjpa;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Selection;

/**
 * 
 * Transformer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Transformer<E, T> {

	T transfer(Model<E> model, List<Selection<?>> selections, Tuple tuple);

}
