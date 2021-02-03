package org.springtribe.framework.fastjpa;

/**
 * 
 * JpaAttributeDetail
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface JpaAttributeDetail {

	String getName();

	Class<?> getJavaType();

	boolean isId();

	boolean isVersion();

	boolean isOptional();

	boolean isAssociation();

	boolean isCollection();

}
