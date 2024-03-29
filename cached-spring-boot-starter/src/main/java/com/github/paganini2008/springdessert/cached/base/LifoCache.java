package com.github.paganini2008.springdessert.cached.base;

import java.util.Stack;

/**
 * 
 * LifoCache
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class LifoCache extends BasicCache {

	public LifoCache(int maxSize) {
		setKeyExpirationPolicy(new LifoKeyExpirationPolicy(maxSize));
	}

	private static class LifoKeyExpirationPolicy implements KeyExpirationPolicy {

		private final Stack<String> keys;
		private final int maxSize;

		LifoKeyExpirationPolicy(int maxSize) {
			this.keys = new Stack<String>();
			this.maxSize = maxSize;
		}

		@Override
		public void onSort(String key, Cache cache) {
			if (!keys.contains(key)) {
				keys.add(key);
				if (keys.size() > maxSize) {
					String oldestKey = keys.pop();
					cache.evict(oldestKey, RemovalReason.EVICTION);
				}
			}
		}

		@Override
		public void onDelete(String key, Cache cache) {
			keys.remove(key);
		}

		@Override
		public void onClear(Cache cache) {
			keys.clear();
		}

	}

}
