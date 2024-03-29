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
package com.github.paganini2008.springdesert.fastjdbc;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.NotImplementedException;
import com.github.paganini2008.devtools.Provider;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Arg;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Args;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Batch;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Example;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Get;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Insert;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Query;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Select;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Sql;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Update;

/**
 * 
 * DaoProxyBean
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@SuppressWarnings("all")
public class DaoProxyBean<T> extends EnhancedJdbcDaoSupport implements InvocationHandler {

	private final Class<T> interfaceClass;
	private final Provider<Class<?>, Object> listenerProvider;
	protected final Logger log;

	public DaoProxyBean(DataSource dataSource, Class<T> interfaceClass, Provider<Class<?>, Object> listenerProvider) {
		Assert.isNull(dataSource, "DataSource must be required.");
		this.setDataSource(dataSource);
		this.interfaceClass = interfaceClass;
		this.listenerProvider = listenerProvider;
		this.log = LoggerFactory.getLogger(interfaceClass);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.isAnnotationPresent(Insert.class)) {
			return doInsert(method, args);
		} else if (method.isAnnotationPresent(Update.class)) {
			return doUpdate(method, args);
		} else if (method.isAnnotationPresent(Get.class)) {
			return doGet(method, args);
		} else if (method.isAnnotationPresent(Query.class)) {
			return doQuery(method, args);
		} else if (method.isAnnotationPresent(Select.class)) {
			return doSelect(method, args);
		} else if (method.isAnnotationPresent(Batch.class)) {
			return doBatch(method, args);
		}
		throw new NotImplementedException("Unknown target method: " + interfaceClass.getName() + "." + method.getName());
	}

	private Class<?> getMethodReturnTypeElementType(Method method) {
		Type returnType = method.getGenericReturnType();
		ParameterizedType parameterizedType = (ParameterizedType) returnType;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		Type firstType = actualTypeArguments[0];
		if (firstType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) firstType).getRawType();
		} else if (firstType instanceof Class) {
			return (Class<?>) firstType;
		}
		throw new UnsupportedOperationException(returnType.getTypeName());
	}

	private Object doQuery(Method method, Object[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		if (!List.class.isAssignableFrom(method.getReturnType())) {
			throw new IllegalArgumentException("Return type is only for List");
		}
		Query select = method.getAnnotation(Query.class);
		Class<?> elementType = getMethodReturnTypeElementType(method);
		String sql = select.value();
		StringBuilder sqlBuilder = new StringBuilder(sql);
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args, sqlBuilder);
		for (Class<?> listenerClass : select.listeners()) {
			DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
			daoListener.beforeExecution(startTime, sqlBuilder, args, this);
		}
		sql = sqlBuilder.toString();
		try {
			if (select.singleColumn()) {
				return getNamedParameterJdbcTemplate().queryForList(sql, sqlParameterSource, elementType);
			} else {
				if (Map.class.isAssignableFrom(elementType)) {
					return getNamedParameterJdbcTemplate().query(sql, sqlParameterSource, new NamedColumnMapRowMapper());
				} else {
					return getNamedParameterJdbcTemplate().query(sql, sqlParameterSource, new BeanPropertyRowMapper<>(elementType));
				}
			}
		} finally {
			for (Class<?> listenerClass : select.listeners()) {
				DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
				daoListener.afterExecution(startTime, sql, args, this);
			}
			printSql(sql, args, startTime);
		}
	}

	private Object doSelect(Method method, Object[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		if (!ResultSetSlice.class.isAssignableFrom(method.getReturnType())) {
			throw new IllegalArgumentException("Return type is only for ResultSetSlice");
		}
		Class<?> elementType = getMethodReturnTypeElementType(method);
		final Select query = method.getAnnotation(Select.class);
		StringBuilder sqlBuilder = new StringBuilder(query.value());
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args, sqlBuilder);
		for (Class<?> listenerClass : query.listeners()) {
			DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
			daoListener.beforeExecution(startTime, sqlBuilder, args, this);
		}
		String sql = sqlBuilder.toString();
		PageableSql pageableSql = BeanUtils.instantiate(query.pageableSql(), sql);
		try {
			if (query.singleColumn()) {
				return getNamedParameterJdbcTemplate().slice(pageableSql, sqlParameterSource, elementType);
			} else {
				if (Map.class.isAssignableFrom(elementType)) {
					return getNamedParameterJdbcTemplate().slice(pageableSql, sqlParameterSource);
				} else {
					return getNamedParameterJdbcTemplate().slice(pageableSql, sqlParameterSource, new BeanPropertyRowMapper<>(elementType));
				}
			}
		} finally {
			for (Class<?> listenerClass : query.listeners()) {
				DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
				daoListener.afterExecution(startTime, sql, args, this);
			}
			printSql(sql, args, startTime);
		}
	}

	private Object doGet(Method method, Object[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		Class<?> returnType = method.getReturnType();
		if (returnType == void.class || returnType == Void.class) {
			throw new IllegalArgumentException("Return type is blank");
		}
		Get getter = method.getAnnotation(Get.class);
		String sql = getter.value();
		StringBuilder sqlBuilder = new StringBuilder(sql);
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args, sqlBuilder);
		for (Class<?> listenerClass : getter.listeners()) {
			DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
			daoListener.beforeExecution(startTime, sqlBuilder, args, this);
		}
		sql = sqlBuilder.toString();
		try {
			if (getter.javaType()) {
				return getNamedParameterJdbcTemplate().queryForObject(sql, sqlParameterSource, returnType);
			} else {
				if (Map.class.isAssignableFrom(returnType)) {
					return getNamedParameterJdbcTemplate().queryForObject(sql, sqlParameterSource, new NamedColumnMapRowMapper());
				} else {
					return getNamedParameterJdbcTemplate().queryForObject(sql, sqlParameterSource, new BeanPropertyRowMapper<>(returnType));
				}
			}
		} finally {
			for (Class<?> listenerClass : getter.listeners()) {
				DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
				daoListener.afterExecution(startTime, sql, args, this);
			}
			printSql(sql, args, startTime);
		}
	}

	private Object doBatch(Method method, Object[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		Class<?> returnType = method.getReturnType();
		if (returnType == void.class || returnType == Void.class) {
			throw new IllegalArgumentException("Return type is blank");
		}
		Batch batch = method.getAnnotation(Batch.class);
		String sql = batch.value();
		StringBuilder sqlBuilder = new StringBuilder(sql);
		SqlParameterSource[] sqlParameterSources = getSqlParameterSources(method, args, sqlBuilder);
		for (Class<?> listenerClass : batch.listeners()) {
			DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
			daoListener.beforeExecution(startTime, sqlBuilder, args, this);
		}
		sql = sqlBuilder.toString();
		try {
			int[] effects = getNamedParameterJdbcTemplate().batchUpdate(sql, sqlParameterSources);
			int effectedRows = effects.length > 0 ? Ints.sum(effects) : 0;
			try {
				return returnType.cast(effectedRows);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(effectedRows, returnType);
			}
		} finally {
			for (Class<?> listenerClass : batch.listeners()) {
				DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
				daoListener.afterExecution(startTime, sql, args, this);
			}
			printSql(sql, args, startTime);
		}
	}

	private Object doInsert(Method method, Object[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		Class<?> returnType = method.getReturnType();
		if (returnType == void.class || returnType == Void.class) {
			throw new IllegalArgumentException("Return type is blank");
		}
		Insert insert = method.getAnnotation(Insert.class);
		String sql = insert.value();
		StringBuilder sqlBuilder = new StringBuilder(sql);
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args, sqlBuilder);
		for (Class<?> listenerClass : insert.listeners()) {
			DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
			daoListener.beforeExecution(startTime, sqlBuilder, args, this);
		}
		sql = sqlBuilder.toString();
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int effected = getNamedParameterJdbcTemplate().update(sql, sqlParameterSource, keyHolder);
			if (effected == 0) {
				throw new InvalidDataAccessResourceUsageException("Failed to insert a new record by sql: " + sql);
			}
			Map<String, Object> keys = keyHolder.getKeys();
			if (MapUtils.isEmpty(keys)) {
				throw new NoGeneratedKeyException(sql);
			}
			Object value = CollectionUtils.getFirst(keys.values());
			try {
				return returnType.cast(value);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(value, returnType);
			}
		} finally {
			for (Class<?> listenerClass : insert.listeners()) {
				DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
				daoListener.afterExecution(startTime, sql, args, this);
			}
			printSql(sql, args, startTime);
		}
	}

	private Object doUpdate(Method method, Object[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		Class<?> returnType = method.getReturnType();
		if (returnType == void.class || returnType == Void.class) {
			throw new IllegalArgumentException("Return type is blank");
		}
		Update update = method.getAnnotation(Update.class);
		String sql = update.value();
		StringBuilder sqlBuilder = new StringBuilder(sql);
		SqlParameterSource sqlParameterSource = getSqlParameterSource(method, args, sqlBuilder);
		for (Class<?> listenerClass : update.listeners()) {
			DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
			daoListener.beforeExecution(startTime, sqlBuilder, args, this);
		}
		sql = sqlBuilder.toString();
		try {
			int effectedRows = getNamedParameterJdbcTemplate().update(sql, sqlParameterSource);
			try {
				return returnType.cast(effectedRows);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(effectedRows, returnType);
			}
		} finally {
			for (Class<?> listenerClass : update.listeners()) {
				DaoListener daoListener = (DaoListener) listenerProvider.apply(listenerClass);
				daoListener.afterExecution(startTime, sql, args, this);
			}
			printSql(sql, args, startTime);
		}
	}

	private void printSql(String sql, Object[] args, long startTime) {
		if (log.isTraceEnabled()) {
			log.trace("Execute sql: {}, Take: {} ms", sql, System.currentTimeMillis() - startTime);
		}
	}

	private SqlParameterSource getSqlParameterSource(Method method, Object[] args, StringBuilder sqlBuilder) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Parameter[] methodParameters = method.getParameters();
		Parameter methodParameter;
		Annotation[] annotations;
		Annotation annotation;
		for (int i = 0; i < methodParameters.length; i++) {
			methodParameter = methodParameters[i];
			annotations = methodParameter.getAnnotations();
			if (ArrayUtils.isEmpty(annotations)) {
				continue;
			}
			annotation = annotations[0];
			if (annotation instanceof Arg) {
				String key = ((Arg) annotation).value();
				if (StringUtils.isBlank(key)) {
					key = methodParameter.getName();
				}
				parameters.put(key, args[i]);
			} else if (annotation instanceof Example) {
				if (args[i] instanceof Map) {
					parameters.putAll((Map<String, Object>) args[i]);
				} else {
					parameters.putAll(PropertyUtils.convertToMap(args[i]));
				}
				String[] excludedProperties = ((Example) annotation).excludedProperties();
				if (ArrayUtils.isNotEmpty(excludedProperties)) {
					MapUtils.removeKeys(parameters, excludedProperties);
				}
			} else if (annotation instanceof Sql) {
				if (args[i] instanceof CharSequence) {
					String sql = sqlBuilder.toString();
					int length = sql.length();
					sql = sql.replaceFirst("@sql", args[i].toString());
					sqlBuilder.delete(0, length);
					sqlBuilder.append(sql);
				}
			}
		}
		return new MapSqlParameterSource(parameters);
	}

	private SqlParameterSource[] getSqlParameterSources(Method method, Object[] args, StringBuilder sqlBuilder) {
		List<SqlParameterSource> sqlParameterList = new ArrayList<SqlParameterSource>();
		Parameter[] methodParameters = method.getParameters();
		Parameter methodParameter;
		Annotation[] annotations;
		Annotation annotation;
		for (int i = 0; i < methodParameters.length; i++) {
			methodParameter = methodParameters[i];
			annotations = methodParameter.getAnnotations();
			if (ArrayUtils.isEmpty(annotations)) {
				continue;
			}
			annotation = annotations[0];
			if (annotation instanceof Args) {
				if (args[i] instanceof Object[]) {
					for (Object object : (Object[]) args[i]) {
						sqlParameterList.add(getSqlParameterSource(object, null));
					}
				} else if (args[i] instanceof Collection) {
					for (Object object : (Collection) args[i]) {
						sqlParameterList.add(getSqlParameterSource(object, null));
					}
				}
			} else if (annotation instanceof Example) {
				SqlParameterSource sqlParameterSource = getSqlParameterSource(args[i], ((Example) annotation).excludedProperties());
				sqlParameterList.add(sqlParameterSource);
			} else if (annotation instanceof Sql) {
				if (args[i] instanceof CharSequence) {
					String sql = sqlBuilder.toString();
					int length = sql.length();
					sql = sql.replaceFirst("@sql", args[i].toString());
					sqlBuilder.delete(0, length);
					sqlBuilder.append(sql);
				}
			}
		}
		return sqlParameterList.toArray(new SqlParameterSource[0]);
	}

	private SqlParameterSource getSqlParameterSource(Object object, String[] excludedProperties) {
		Map<String, Object> parameters;
		if (object instanceof Map) {
			parameters = (Map<String, Object>) object;
		} else {
			parameters = PropertyUtils.convertToMap(object);
		}
		if (ArrayUtils.isNotEmpty(excludedProperties)) {
			MapUtils.removeKeys(parameters, excludedProperties);
		}
		return new MapSqlParameterSource(parameters);
	}

	public Class<T> getInterfaceClass() {
		return interfaceClass;
	}

	public String toString() {
		return interfaceClass.getName() + "$ProxyByJDK";
	}

}
