package indi.atlantis.framework.tx;

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
