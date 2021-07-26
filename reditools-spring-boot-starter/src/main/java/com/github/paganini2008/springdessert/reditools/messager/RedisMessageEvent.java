/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.springdessert.reditools.messager;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * RedisMessageEvent
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class RedisMessageEvent extends ApplicationEvent {

	private static final long serialVersionUID = 5563838735572037403L;

	public RedisMessageEvent(RedisMessageEntity entity) {
		super(entity);
	}

	public String getChannel() {
		return getSource().getChannel();
	}

	public Object getMessage() {
		return getSource().getMessage();
	}

	@Override
	public RedisMessageEntity getSource() {
		return (RedisMessageEntity) super.getSource();
	}

}
