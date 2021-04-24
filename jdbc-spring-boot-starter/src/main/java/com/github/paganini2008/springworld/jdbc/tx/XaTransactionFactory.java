package com.github.paganini2008.springworld.jdbc.tx;

/**
 * 
 * XaTransactionFactory
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface XaTransactionFactory {

	XaTransaction newTransaction(String xaId);
	
}
