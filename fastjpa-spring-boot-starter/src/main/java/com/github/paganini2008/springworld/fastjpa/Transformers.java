package com.github.paganini2008.springworld.fastjpa;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Selection;

/**
 * 
 * Transformers
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class Transformers {

	public static <T> Transformer<T, T> noop() {
		return new NoopTransformer<T>();
	}

	public static <T> Transformer<T, Map<String, Object>> asMap() {
		return new MapTransformer<T>();
	}

	public static <T> Transformer<T, List<Object>> asList() {
		return new ListTransformer<T>();
	}

	public static <T, R> Transformer<T, R> asBean(Class<R> resultClass) {
		return asBean(resultClass, null);
	}

	public static <T, R> Transformer<T, R> asBean(Class<R> resultClass, TransformerPostHandler<T, R> postHandler) {
		return asBean(resultClass, new String[0], postHandler);
	}

	public static <T, R> Transformer<T, R> asBean(Class<R> resultClass, String[] includedProperties,
			TransformerPostHandler<T, R> postHandler) {
		return new BeanPropertyTransformer<T, R>(resultClass, includedProperties, postHandler);
	}

	/**
	 * 
	 * NoopTransformer
	 * 
	 * @author Jimmy Hoff
	 *
	 * @version 1.0
	 */
	public static class NoopTransformer<T> implements Transformer<T, T> {

		NoopTransformer() {
		}

		@Override
		public T transfer(Model<?> model, T original) {
			return original;
		}

		@Override
		public T transfer(Model<?> model, List<Selection<?>> selections, T original) {
			return original;
		}

	}

	/**
	 * 
	 * MapTransformer
	 * 
	 * @author Jimmy Hoff
	 *
	 * @version 1.0
	 */
	public static class MapTransformer<T> extends AbstractTransformer<T, Map<String, Object>> {

		MapTransformer() {
		}

		@Override
		protected Map<String, Object> createObject(Model<?> model, int selectionSize, T original) {
			return new LinkedHashMap<String, Object>(selectionSize);
		}

		@Override
		protected void writeValue(Model<?> model, String attributeName, Class<?> attributeType, Object attributeValue,
				Map<String, Object> data) {
			data.put(attributeName, attributeValue);
		}

	}

	/**
	 * 
	 * ListTransformer
	 * 
	 * @author Jimmy Hoff
	 *
	 * @version 1.0
	 */
	public static class ListTransformer<T> extends AbstractTransformer<T, List<Object>> {

		ListTransformer() {
		}

		@Override
		protected List<Object> createObject(Model<?> model, int selectionSize, T original) {
			return new ArrayList<Object>(selectionSize);
		}

		@Override
		protected void writeValue(Model<?> model, String attributeName, Class<?> attributeType, Object attributeValue,
				List<Object> dataList) {
			dataList.add(attributeValue);
		}

	}

}
