package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * TransformerPostHandler
 *
 * @author Fred Feng
 * @version 1.0
 */
@FunctionalInterface
public interface TransformerPostHandler<T, R> {

	void handleAfterTransferring(Model<?> model, T original, R destination);

}
