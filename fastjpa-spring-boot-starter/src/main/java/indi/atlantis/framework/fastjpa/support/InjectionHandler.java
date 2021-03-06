package indi.atlantis.framework.fastjpa.support;

/**
 * 
 * InjectionHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface InjectionHandler {

	Object inject(Object original, String targetProperty, Class<?> targetPropertyType);

}
