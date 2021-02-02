package org.springdessert.framework.tx;

import org.springdessert.framework.tx.openfeign.OpenFeignConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * TransactionAutoConfiguration
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Configuration
@Import({ JdbcTransactionConfig.class, XaTransactionConfig.class, OpenFeignConfig.class })
public class TransactionAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(IdGenerator.class)
	public IdGenerator uuidIdGenerator() {
		return new UuidIdGenerator();
	}

	@Bean
	public TransactionEventPublisher transactionEventPublisher() {
		return new TransactionEventPublisher();
	}

	@Bean
	public TransactionEventListenerContainer transactionEventListenerContainer() {
		return new TransactionEventListenerContainer();
	}
}
