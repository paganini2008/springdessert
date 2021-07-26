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
package com.github.paganini2008.springdessert.xmemcached;

import com.github.paganini2008.springdessert.xmemcached.serializer.KryoMemcachedSerializer;
import com.github.paganini2008.springdessert.xmemcached.serializer.MemcachedSerializer;
import com.google.code.yanf4j.core.impl.StandardSocketOption;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * 
 * MemcachedTemplate
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class MemcachedTemplate implements MemcachedOperations {

	MemcachedTemplate(MemcachedClient client, MemcachedSerializer serializer) {
		this.client = client;
		this.serializer = serializer;
	}

	private final MemcachedClient client;
	private final MemcachedQueue queue = new MemcachedQueue(this);
	private final MemcachedSerializer serializer;

	public boolean set(String key, int expiration, Object value) throws Exception {
		byte[] bytes = serializer.serialize(value);
		return client.set(key, expiration, bytes);
	}

	public <T> T get(String key, Class<T> requiredType) throws Exception {
		byte[] bytes = client.get(key);
		return serializer.deserialize(bytes, requiredType);
	}

	public boolean delete(String key) throws Exception {
		return client.delete(key);
	}

	public boolean push(String key, int expiration, Object value) throws Exception {
		return queue.push(key, expiration, value);
	}

	public <T> T pop(String key, Class<T> requiredType) throws Exception {
		return queue.pop(key, requiredType);
	}

	public MemcachedClient getClient() {
		return client;
	}

	/**
	 * 
	 * Builder
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static class Builder {

		private String address = "localhost:11211";
		private int connectionPoolSize = 8;
		private long sessionIdleTimeout = 10000;
		private int soTimeout = 60000;
		private MemcachedSerializer serializer;

		public Builder setAddress(String address) {
			this.address = address;
			return this;
		}

		public Builder setConnectionPoolSize(int connectionPoolSize) {
			this.connectionPoolSize = connectionPoolSize;
			return this;
		}

		public Builder setSessionIdleTimeout(long sessionIdleTimeout) {
			this.sessionIdleTimeout = sessionIdleTimeout;
			return this;
		}

		public Builder setSoTimeout(int soTimeout) {
			this.soTimeout = soTimeout;
			return this;
		}

		public Builder setSerializer(MemcachedSerializer serializer) {
			this.serializer = serializer;
			return this;
		}

		public MemcachedTemplate build() throws Exception {
			XMemcachedClientBuilder clientBuilder = new XMemcachedClientBuilder(AddrUtil.getAddresses(address));
			clientBuilder.setConnectionPoolSize(connectionPoolSize);
			clientBuilder.getConfiguration().setSessionIdleTimeout(sessionIdleTimeout);
			clientBuilder.getConfiguration().setSoTimeout(soTimeout);

			clientBuilder.setSocketOption(StandardSocketOption.SO_RCVBUF, 64 * 1024);
			clientBuilder.setSocketOption(StandardSocketOption.SO_SNDBUF, 32 * 1024);
			clientBuilder.setSocketOption(StandardSocketOption.TCP_NODELAY, false);

			clientBuilder.setFailureMode(true);
			clientBuilder.setCommandFactory(new BinaryCommandFactory());
			if (serializer == null) {
				serializer = new KryoMemcachedSerializer();
			}
			return new MemcachedTemplate(clientBuilder.build(), serializer);
		}

	}

}
