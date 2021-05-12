package com.github.paganini2008.springworld.fastjpa.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;
import com.github.paganini2008.devtools.reflection.FieldUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * BeanReflection
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public final class BeanReflection<T> {

	public BeanReflection(Class<T> requiredType, String[] includedProperties) {
		this.requiredType = requiredType;
		if (includedProperties != null && includedProperties.length > 0) {
			includedPropertyNames = new HashSet<String>(Arrays.asList(includedProperties));
		}
	}

	private final Class<T> requiredType;
	private Set<String> includedPropertyNames;

	public void setProperty(T object, String attributeName, Object attributeValue) {
		final String propertyName = attributeName;
		if (hasPropertyValue(propertyName)) {
			try {
				PropertyUtils.setProperty(object, propertyName, attributeValue);
			} catch (Exception e) {
				try {
					FieldUtils.writeField(object, propertyName, attributeValue);
				} catch (Exception ignored) {
					if (log.isTraceEnabled()) {
						log.trace("Attribute '{}' cannot be assigned value.", requiredType.getName() + "#" + propertyName);
					}
				}
			}
		}

	}

	public T instantiateBean() {
		try {
			return ConstructorUtils.invokeConstructor(requiredType, (Object[]) null);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private boolean hasPropertyValue(String propertyName) {
		return includedPropertyNames == null || includedPropertyNames.contains(propertyName);
	}

}
