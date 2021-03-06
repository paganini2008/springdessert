package indi.atlantis.framework.reditools.messager;

/**
 * 
 * RedisMessageHandler
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface RedisMessageHandler {

	String getChannel();

	void onMessage(String channel, Object message) throws Exception;

	default boolean isRepeatable() {
		return true;
	}

}
