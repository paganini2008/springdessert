package com.github.paganini2008.springworld.jdbc.db4j;

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

/**
 * 
 * Db4jDaoProxyBeanFactory
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
public class Db4jDaoProxyBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {

	private final Class<T> interfaceClass;

	public Db4jDaoProxyBeanFactory(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@Autowired
	private DataSource dataSource;

	private ApplicationContext ctx;

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass }, new Db4jDaoProxyBean<T>(
				new TransactionSynchronizationConnectionFactory(dataSource), interfaceClass, new Provider<Class<?>, Object>() {
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
	 * @author Jimmy Hoff
	 * @since 1.0
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
