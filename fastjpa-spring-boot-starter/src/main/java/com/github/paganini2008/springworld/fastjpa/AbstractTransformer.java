package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Selection;

import com.github.paganini2008.devtools.beans.PropertyUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * AbstractTransformer
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public abstract class AbstractTransformer<T, R> implements Transformer<T, R> {

	public R transfer(Model<?> model, List<Selection<?>> selections, T original) {
		final R destination = createObject(model, selections, original);
		for (Selection<?> selection : selections) {
			setAttributeValue(model, selection.getAlias(), selection.getJavaType(), original, destination);
		}
		afterTransferring(model, original, destination);
		return destination;
	}

	protected void setAttributeValue(Model<?> model, String attributeName, Class<?> attributeType, T original, R destination) {
		final Object result = readValue(model, attributeName, attributeType, original);
		if (model.isManaged(attributeType)) {
			String realAttributeName;
			Object attributeValue;
			for (JpaAttributeDetail attributeDetail : model.getAttributeDetails(attributeName)) {
				realAttributeName = attributeDetail.getName();
				try {
					attributeValue = PropertyUtils.getProperty(result, realAttributeName);
					writeValue(model, realAttributeName, attributeDetail.getJavaType(), attributeValue, destination);
				} catch (Exception ignored) {
					if (log.isTraceEnabled()) {
						log.trace("'{}' cannot be set value.", attributeType + "#" + realAttributeName);
					}
				}
			}
		} else {
			writeValue(model, attributeName, attributeType, result, destination);
		}
	}

	protected Object readValue(Model<?> model, String attributeName, Class<?> attributeType, T original) {
		if (original instanceof Tuple) {
			return ((Tuple) original).get(attributeName);
		} else {
			return PropertyUtils.getProperty(original, attributeName);
		}
	}

	protected abstract void writeValue(Model<?> model, String attributeName, Class<?> attributeType, Object attributeValue, R destination);

	protected abstract R createObject(Model<?> model, List<Selection<?>> selections, T original);

	protected void afterTransferring(Model<?> model, T value, R object) {
	}

}
