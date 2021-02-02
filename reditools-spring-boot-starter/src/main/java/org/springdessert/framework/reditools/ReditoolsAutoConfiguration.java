package org.springdessert.framework.reditools;

import org.springdessert.framework.reditools.common.RedisCommonConfig;
import org.springdessert.framework.reditools.messager.RedisMessageConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * ReditoolsAutoConfiguration
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@Import({ RedisMessageConfig.class, RedisCommonConfig.class })
public class ReditoolsAutoConfiguration {
}
