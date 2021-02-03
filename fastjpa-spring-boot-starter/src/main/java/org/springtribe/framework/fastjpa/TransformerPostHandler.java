package org.springtribe.framework.fastjpa;

import javax.persistence.Tuple;

/**
 * 
 * TransformerPostHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface TransformerPostHandler<T> {

	void handleAfterTransferring(Tuple tuple, T output);

}
