package org.springtribe.framework.reditools;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springtribe.framework.reditools.common.RedisCommonConfig;
import org.springtribe.framework.reditools.messager.RedisMessageConfig;

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
