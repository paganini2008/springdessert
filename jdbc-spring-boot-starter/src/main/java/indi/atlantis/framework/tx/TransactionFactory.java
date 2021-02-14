package indi.atlantis.framework.tx;

/**
 * 
 * TransactionFactory
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface TransactionFactory {

	Transaction newTransaction(String id);

}
