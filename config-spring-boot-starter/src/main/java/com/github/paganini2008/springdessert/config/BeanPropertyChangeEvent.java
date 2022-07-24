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

import org.springframework.context.ApplicationEvent;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

/**
 * 
 * BeanPropertyChangeEvent
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class BeanPropertyChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = 8898079156176355924L;

	public BeanPropertyChangeEvent(Object bean, String beanName, String propertyName, Object previousValue, Object currentValue) {
		super(bean);
		this.beanName = beanName;
		this.propertyName = propertyName;
		this.previousValue = previousValue;
		this.currentValue = currentValue;
	}

	private final String beanName;
	private final String propertyName;
	private final Object previousValue;
	private final Object currentValue;

	public String getBeanName() {
		return beanName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPreviousValue() {
		return previousValue;
	}

	public Object getCurrentValue() {
		return currentValue;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, new String[] { "source" });
	}

}
