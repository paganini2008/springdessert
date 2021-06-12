package com.github.paganini2008.springworld.fastjpa.support;

import java.util.Map;

/**
 * 
 * BeanPropertyRowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
public class BeanPropertyRowMapper<T> implements RowMapper<T> {

	private final BeanReflection<T> beanReflection;

	public BeanPropertyRowMapper(Class<T> resultClass, String... includedProperties) {
		this.beanReflection = new BeanReflection<T>(resultClass, includedProperties);
	}

	public T mapRow(int index, Map<String, Object> data) {
		T instance = beanReflection.instantiateBean();
		for (String propertyName : data.keySet()) {
			beanReflection.setProperty(instance, propertyName, data.get(propertyName));
		}
		return instance;
	}
}
