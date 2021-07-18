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
package com.github.paganini2008.springdessert.xmemcached.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.github.paganini2008.devtools.io.SerializationException;

/**
 * 
 * KryoMemcachedSerializer
 *
 * @author Fred Feng
 * @version 1.0
 */
public class KryoMemcachedSerializer implements MemcachedSerializer {

	public static final int DEFAULT_POOL_SIZE = 16;
	private static final int DEFAULT_IO_POOL_SIZE = 128;
	private static final int DEFAULT_POOL_BUFFER_SIZE = 8192;

	private final Pool<Kryo> pool;
	private final Pool<Output> outputPool;
	private final Pool<Input> inputPool;

	public KryoMemcachedSerializer() {
		this(DEFAULT_POOL_SIZE, DEFAULT_IO_POOL_SIZE, DEFAULT_IO_POOL_SIZE, DEFAULT_POOL_BUFFER_SIZE);
	}

	public KryoMemcachedSerializer(int poolSize, int outputSize, int inputSize, int bufferSize) {
		this.pool = KryoHelper.getPool(poolSize);
		this.outputPool = KryoHelper.getOutputPool(outputSize, bufferSize);
		this.inputPool = KryoHelper.getInputPool(inputSize, bufferSize);
	}

	@Override
	public byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		Kryo kryo = pool.obtain();
		Output output = outputPool.obtain();
		try {
			output.reset();
			kryo.writeObject(output, object);
			return output.getBuffer();
		} catch (Exception e) {
			throw new SerializationException(e);
		} finally {
			outputPool.free(output);
			pool.free(kryo);
		}
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> requiredType) throws Exception {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		Kryo kryo = pool.obtain();
		Input input = inputPool.obtain();
		try {
			input.setBuffer(bytes);
			return kryo.readObject(input, requiredType);
		} catch (Exception e) {
			throw new SerializationException(e);
		} finally {
			inputPool.free(input);
			pool.free(kryo);
		}
	}

}
