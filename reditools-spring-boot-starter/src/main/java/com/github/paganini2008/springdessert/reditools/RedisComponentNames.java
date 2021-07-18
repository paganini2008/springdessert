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
package com.github.paganini2008.springdessert.reditools;

/**
 * 
 * RedisComponentNames
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class RedisComponentNames {

	public static final String REDIS_SERIALIZER = "reditools:redis-serializer";
	public static final String REDIS_TEMPLATE = "reditools:redis-template";
	public static final String REDIS_MESSAGE_SENDER = "reditools:redis-message-sender";
	public static final String REDIS_MESSAGE_PUBSUB_LISTENER = "reditools:redis-message-pubsub-listener";
	public static final String REDIS_MESSAGE_QUEUE_LISTENER = "reditools:redis-message-queue-listener";
	public static final String PUBSUB_REDIS_MESSAGE_DISPATCHER = "reditools:pubsub-redis-message-dispatcher";
	public static final String QUEUE_REDIS_MESSAGE_DISPATCHER = "reditools:queue-redis-message-dispatcher";
	public static final String REDIS_MESSAGE_LISTENER_CONTAINER = "reditools:redis-message-listener-container";
	public static final String REDIS_MESSAGE_EVENT_LISTENER = "reditools:redis-message-event-listener";
	public static final String REDIS_MESSAGE_EVENT_PUBLISHER = "reditools:redis-message-event-publisher";
	public static final String REDIS_KEY_EXPIRED_EVENT_PUBLISHER = "reditools:redis-key-expired-event-publisher";

}
