/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.springdessert.config;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringValueResolver;

import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * InternalStringValueResolver
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class InternalStringValueResolver implements StringValueResolver, BeanFactoryAware {

	static final String DEFAULT_PLACEHOLDER_PREFIX = "@{";

	static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	private ConfigurableBeanFactory beanFactory;

	@Override
	public String resolveStringValue(String strVal) {
		if (strVal.startsWith(DEFAULT_PLACEHOLDER_PREFIX) && strVal.endsWith(DEFAULT_PLACEHOLDER_SUFFIX)) {
			String value = strVal.substring(2, strVal.length() - 1);
			strVal = "${" + value + "}";
			return beanFactory.resolveEmbeddedValue(strVal);
		}
		return strVal;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
		configurableBeanFactory.setTypeConverter(new EnhancedTypeConverter(configurableBeanFactory.getTypeConverter()));
		configurableBeanFactory.addEmbeddedValueResolver(this);
		this.beanFactory = configurableBeanFactory;
	}

	public ConfigurableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public static class EnhancedTypeConverter implements TypeConverter {

		private final TypeConverter delegate;

		EnhancedTypeConverter(TypeConverter delegate) {
			this.delegate = delegate;
		}

		@Override
		public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
			try {
				return delegate.convertIfNecessary(value, requiredType);
			} catch (TypeMismatchException e) {
				try {
					return ConvertUtils.convertValue(value, requiredType);
				} catch (RuntimeException ignored) {
				}
				throw e;
			}
		}

		@Override
		public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws TypeMismatchException {
			return delegate.convertIfNecessary(value, requiredType, methodParam);
		}

		@Override
		public <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field) throws TypeMismatchException {
			try {
				return delegate.convertIfNecessary(value, requiredType, field);
			} catch (TypeMismatchException e) {
				try {
					return ConvertUtils.convertValue(value, requiredType);
				} catch (RuntimeException ignored) {
				}
				throw e;
			}
		}

	}

}
