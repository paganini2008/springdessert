package indi.atlantis.framework.xmemcached.serializer;

/**
 * 
 * MemcachedSerializer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MemcachedSerializer {

	byte[] serialize(Object object) throws Exception;

	<T> T deserialize(byte[] bytes, Class<T> requiredType) throws Exception;

}
