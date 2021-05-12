package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.criteria.Selection;

/**
 * 
 * Transformer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Transformer<T, R> {

	R transfer(Model<?> model, List<Selection<?>> selections, T value);

}
