package indi.atlantis.framework.fastjpa;

/**
 * 
 * JpaDelete
 * 
 * @author Jimmy Hoff
 * 
 */
public interface JpaDelete<E> extends Executable{

	JpaDelete<E> filter(Filter filter);

	<X> JpaSubQuery<X, X> subQuery(Class<X> entityClass);

}
