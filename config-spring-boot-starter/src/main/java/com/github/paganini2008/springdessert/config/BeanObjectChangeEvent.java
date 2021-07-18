/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
 * BeanObjectChangeEvent
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class BeanObjectChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4317527666421114782L;

	public BeanObjectChangeEvent(Object bean, String beanName) {
		super(bean);
		this.beanName = beanName;
	}

	private final String beanName;

	public String getBeanName() {
		return beanName;
	}

	public Class<?> getBeanClass() {
		return getSource().getClass();
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, new String[] { "source" });
	}

}
