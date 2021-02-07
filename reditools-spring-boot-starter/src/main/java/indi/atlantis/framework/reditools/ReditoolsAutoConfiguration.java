package indi.atlantis.framework.reditools;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import indi.atlantis.framework.reditools.common.RedisCommonConfig;
import indi.atlantis.framework.reditools.messager.RedisMessageConfig;

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
