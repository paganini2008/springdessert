package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * TransformerPostHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@FunctionalInterface
public interface TransformerPostHandler<T, R> {

	void handleAfterTransferring(Model<?> model, T original, R destination);

}
