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
package com.github.paganini2008.springdesert.fastjdbc.db4j;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.github.paganini2008.devtools.Provider;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.jdbc.DataSourceFactory;

/**
 * 
 * Db4jDaoProxyBeanFactory
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class Db4jDaoProxyBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {

	private final Class<T> interfaceClass;

	public Db4jDaoProxyBeanFactory(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@Autowired
	private DataSourceFactory dataSourceFactory;

	private ApplicationContext ctx;

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new Db4jDaoProxyBean<T>(new TransactionSynchronizationConnectionFactory(dataSourceFactory.getDataSource()), interfaceClass,
						new Provider<Class<?>, Object>() {
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

	/**
	 * 
	 * TransactionSynchronizationConnectionFactory
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class TransactionSynchronizationConnectionFactory implements ConnectionFactory {

		private final DataSource ds;

		public TransactionSynchronizationConnectionFactory(DataSource ds) {
			this.ds = ds;
		}

		@Override
		public Connection getConnection() throws SQLException {
			return DataSourceUtils.getConnection(ds);
		}

		@Override
		public void close(Connection connection) {
			DataSourceUtils.releaseConnection(connection, ds);
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
