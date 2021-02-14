package indi.atlantis.framework.tx;

import indi.atlantis.framework.reditools.messager.MessageHandler;
import indi.atlantis.framework.reditools.messager.OnMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * XaTransactionCompletionHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
@MessageHandler("completion:*")
public class XaTransactionCompletionHandler {

	@OnMessage
	public void onMessage(String channel, Object message) {
		if (log.isTraceEnabled()) {
			log.trace(message.toString());
		}
	}

}
