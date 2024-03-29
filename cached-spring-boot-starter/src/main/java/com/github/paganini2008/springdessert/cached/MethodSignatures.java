package com.github.paganini2008.springdessert.cached;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.springdessert.cached.base.Cache;
import com.github.paganini2008.springdessert.cached.base.Clear;
import com.github.paganini2008.springdessert.cached.base.Delete;
import com.github.paganini2008.springdessert.cached.base.Hit;
import com.github.paganini2008.springdessert.cached.base.Sort;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MethodSignatures
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
public abstract class MethodSignatures {

	private static final Map<String, String> cache = new HashMap<String, String>();
	private static final Map<String, String> hash = new HashMap<String, String>();
	private static final Map<String, String> set = new HashMap<String, String>();
	private static final Map<String, String> list = new HashMap<String, String>();

	static {
		List<Method> methods = MethodUtils.getMethodsWithAnnotation(Cache.ValueOperations.class, Sort.class);
		for (Method method : methods) {
			if (method.isAnnotationPresent(Hit.class)) {
				continue;
			}
			cache.put(method.getName(), method.toGenericString());
		}

		methods = MethodUtils.getMethodsWithAnnotation(Cache.ValueOperations.class, Delete.class);
		for (Method method : methods) {
			cache.put(method.getName(), method.toGenericString());
		}

		methods = MethodUtils.getMethodsWithAnnotation(Cache.ValueOperations.class, Clear.class);
		for (Method method : methods) {
			cache.put(method.getName(), method.toGenericString());
		}
		if (log.isTraceEnabled()) {
			for (Map.Entry<String, String> entry : cache.entrySet()) {
				log.trace("cache." + entry.getKey() + " -> " + entry.getValue());
			}
		}

		methods = MethodUtils.getMethodsWithAnnotation(Cache.HashOperations.class, Sort.class);
		for (Method method : methods) {
			if (method.isAnnotationPresent(Hit.class)) {
				continue;
			}
			hash.put(method.getName(), method.toGenericString());
		}
		if (log.isTraceEnabled()) {
			for (Map.Entry<String, String> entry : hash.entrySet()) {
				log.trace("hash." + entry.getKey() + " -> " + entry.getValue());
			}
		}

		methods = MethodUtils.getMethodsWithAnnotation(Cache.SetOperations.class, Sort.class);
		for (Method method : methods) {
			if (method.isAnnotationPresent(Hit.class)) {
				continue;
			}
			set.put(method.getName(), method.toGenericString());
		}
		if (log.isTraceEnabled()) {
			for (Map.Entry<String, String> entry : hash.entrySet()) {
				log.trace("set." + entry.getKey() + " -> " + entry.getValue());
			}
		}

		methods = MethodUtils.getMethodsWithAnnotation(Cache.ListOperations.class, Sort.class);
		for (Method method : methods) {
			if (method.isAnnotationPresent(Hit.class)) {
				continue;
			}
			list.put(method.getName(), method.toGenericString());
		}
		if (log.isTraceEnabled()) {
			for (Map.Entry<String, String> entry : hash.entrySet()) {
				log.trace("list." + entry.getKey() + " -> " + entry.getValue());
			}
		}

	}

	public static String parse(String dataType, String functionName) {
		Map<String, String> m;
		switch (dataType) {
		case "cache":
			m = cache;
			break;
		case "hash":
			m = hash;
			break;
		case "set":
			m = set;
			break;
		case "list":
			m = list;
			break;
		default:
			throw new UnsupportedOperationException(dataType);
		}
		if (!m.containsKey(functionName)) {
			throw new IllegalStateException("Invalid method signature: " + dataType + "." + functionName);
		}
		return m.get(functionName);
	}

	public static void main(String[] args) {
		System.out.println(parse("list", "addFirst"));
	}

}
