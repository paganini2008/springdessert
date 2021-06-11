package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Selection;

/**
 * 
 * Column
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Column {

	Selection<?> toSelection(Model<?> model, CriteriaBuilder builder);

	static Column forName(String attributeName) {
		return forName(null, attributeName);
	}

	static Column forName(String alias, String attributeName) {
		return forName(alias, attributeName, null);
	}

	static Column forName(String attributeName, Class<?> requiredType) {
		return forName(null, attributeName, requiredType);
	}

	static Column forName(String alias, String attributeName, Class<?> requiredType) {
		return Property.forName(alias, attributeName, requiredType).as(attributeName);
	}

	static Column forSubQuery(SubQueryBuilder<?> subQueryBuilder) {
		return new Column() {

			@Override
			public Selection<?> toSelection(Model<?> model, CriteriaBuilder builder) {
				return subQueryBuilder.toSubquery(builder).getSelection();
			}
		};
	}

	static Column construct(Class<?> resultClass, String alias, String[] attributeNames) {
		return new Column() {

			@Override
			public Selection<?> toSelection(Model<?> model, CriteriaBuilder builder) {
				List<Selection<?>> selections = model.getSelections(alias, attributeNames);
				return builder.construct(resultClass, selections.toArray(new Selection[0]));
			}
		};
	}

}
