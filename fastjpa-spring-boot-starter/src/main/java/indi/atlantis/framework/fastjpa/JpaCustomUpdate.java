package indi.atlantis.framework.fastjpa;

/**
 * 
 * JpaCustomUpdate
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface JpaCustomUpdate<X> {

	JpaUpdate<X> update(Class<X> entityClass);

	JpaDelete<X> delete(Class<X> entityClass);

	int executeUpdate(JpaDeleteCallback<X> callback);

	int executeUpdate(JpaUpdateCallback<X> callback);
}
