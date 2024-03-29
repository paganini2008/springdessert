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
package com.github.paganini2008.springdessert.reditools.messager;

import java.io.Serializable;
import java.util.UUID;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ObjectUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * RedisMessageEntity
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Getter
@Setter
public class RedisMessageEntity implements Serializable {

	private static final long serialVersionUID = -8864323748615322076L;

	public static final RedisMessageEntity EMPTY = RedisMessageEntity.of("EMPTY", null);

	private String id;
	private String channel;
	private Object message;
	private long timestamp;

	public RedisMessageEntity() {
	}

	protected RedisMessageEntity(String channel, Object message) {
		Assert.hasNoText(channel, "Channel must be required for redis message");
		this.id = UUID.randomUUID().toString();
		this.channel = channel;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
	}

	public static RedisMessageEntity of(String channel, Object message) {
		return new RedisMessageEntity(channel, message);
	}

	public String toString() {
		return "[RedisMessageEntity] channel: " + channel + ", message: " + ObjectUtils.toStringSelectively(message);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		return prime + prime * id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RedisMessageEntity) {
			if (obj == this) {
				return true;
			}
			RedisMessageEntity messageEntity = (RedisMessageEntity) obj;
			return messageEntity.getId().equals(getId());
		}
		return false;
	}

}
