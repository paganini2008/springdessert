package com.github.paganini2008.springdessert.cached;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * ApplicationClusterCacheAutoConfiguration
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Configuration
@Import(ApplicationClusterCacheConfig.class)
public class ApplicationClusterCacheAutoConfiguration {
}
