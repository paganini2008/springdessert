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
package com.github.paganini2008.springworld.fastjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Property
 *
 * @author Fred Feng
 * @version 1.0
 */
public final class Property<T> implements Field<T> {

	private final String alias;
	private final String attributeName;
	private final Class<T> requiredType;

	Property(String alias, String attributeName, Class<T> requiredType) {
		this.alias = alias;
		this.attributeName = attributeName;
		this.requiredType = requiredType;
	}

	public Expression<T> toExpression(Model<?> model, CriteriaBuilder builder) {
		Expression<T> expression = StringUtils.isNotBlank(alias) ? model.getAttribute(alias, attributeName)
				: model.getAttribute(Model.ROOT, attributeName);
		if (requiredType != null) {
			return expression.as(requiredType);
		}
		return expression;
	}

	public String toString() {
		return String.format("%s.%s", alias, attributeName);
	}

	public static <T> Property<T> forName(String attributeName) {
		return forName(null, attributeName);
	}

	public static <T> Property<T> forName(String alias, String attributeName) {
		return forName(alias, attributeName, null);
	}

	public static <T> Property<T> forName(String attributeName, Class<T> requiredType) {
		return forName(null, attributeName, requiredType);
	}

	public static <T> Property<T> forName(String alias, String attributeName, Class<T> requiredType) {
		return new Property<T>(alias, attributeName, requiredType);
	}

}
