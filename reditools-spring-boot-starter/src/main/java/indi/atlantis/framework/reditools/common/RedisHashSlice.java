package indi.atlantis.framework.reditools.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * RedisHashSlice
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public class RedisHashSlice<T> implements ResultSetSlice<T> {

	private final String key;
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisHashSlice(String key, RedisTemplate<String, Object> redisTemplate) {
		this.key = key;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public int rowCount() {
		Number number = redisTemplate.opsForHash().size(key);
		return number != null ? number.intValue() : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(int maxResults, int firstResult) {
		List<T> dataList = new ArrayList<T>();
		Set<Object> hashKeys = redisTemplate.opsForHash().keys(key);
		List<Object> hashKeyList = ListUtils.slice(new ArrayList<Object>(hashKeys), maxResults, firstResult);
		for (Object hashKey : hashKeyList) {
			dataList.add((T) redisTemplate.opsForHash().get(key, hashKey));
		}
		return dataList;
	}

}
