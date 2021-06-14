/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.springdesert.fastjdbc;

import java.lang.reflect.Proxy;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.paganini2008.devtools.Provider;
import com.github.paganini2008.devtools.beans.BeanUtils;

/**
 * 
 * DaoProxyBeanFactory
 *
 * @author Fred Feng
 * @since 1.0
 */
public class DaoProxyBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {

	private final Class<T> interfaceClass;

	public DaoProxyBeanFactory(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@Autowired
	private DataSource dataSource;

	private ApplicationContext ctx;

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new DaoProxyBean<T>(dataSource, interfaceClass, new Provider<Class<?>, Object>() {
					@Override
					protected Object createObject(Class<?> listenerClass) {
						try {
							return ctx.getBean(listenerClass);
						} catch (BeansException e) {
							return BeanUtils.instantiate(listenerClass);
						}
					}
				}));
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
