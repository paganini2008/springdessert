/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.springdessert.config;

import java.io.IOError;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.devtools.io.ResourceUtils;

/**
 * 
 * RemoteConfigurationSpringApplication
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public abstract class RemoteConfigurationSpringApplication extends SpringApplication {

	private static final String DEFAULT_CONFIG_NAME = "settings.properties";
	private static final String DEFAULT_CONFIG_NAME_FORMAT = "settings-%s.properties";
	private static final String CURRENT_APPLICATION_PROFILES = "spring.profiles.active";
	private static final String CURRENT_APPLICATION_NAME = "spring.application.name";
	static final String DEFAULT_BOOTSTRAP_CONFIG_NAME = "applicationBootstrapConfig";

	protected RemoteConfigurationSpringApplication(Class<?>... mainClasses) {
		super(mainClasses);
	}

	@Override
	protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
		super.configureEnvironment(environment, args);

		final Map<String, String> bootstrapConfig;
		try {
			bootstrapConfig = ResourceUtils.getResource(DEFAULT_CONFIG_NAME);
		} catch (Exception e) {
			throw new IOError(e);
		}

		String[] activeProfiles = environment.getActiveProfiles();
		String env = activeProfiles != null && activeProfiles.length > 0 ? activeProfiles[0] : "";
		if (StringUtils.isBlank(env)) {
			env = SystemPropertyUtils.getString(CURRENT_APPLICATION_PROFILES);
		}
		if (StringUtils.isBlank(env)) {
			env = bootstrapConfig.get(CURRENT_APPLICATION_PROFILES);
		}
		if (StringUtils.isBlank(env)) {
			env = "dev";
		}

		System.setProperty(CURRENT_APPLICATION_PROFILES, env);
		bootstrapConfig.put(CURRENT_APPLICATION_PROFILES, env);
		try {
			Map<String, String> localConfig = ResourceUtils.getResource(String.format(DEFAULT_CONFIG_NAME_FORMAT, env));
			bootstrapConfig.putAll(localConfig);
		} catch (Exception ignored) {
		}

		String applicationName = environment.getProperty(CURRENT_APPLICATION_NAME);
		if (StringUtils.isBlank(applicationName)) {
			applicationName = bootstrapConfig.get(CURRENT_APPLICATION_NAME);
		}
		if (StringUtils.isBlank(applicationName)) {
			throw new IllegalArgumentException("System property '" + CURRENT_APPLICATION_NAME + "' must be required.");
		}

		environment.getPropertySources().addLast(new OriginTrackedMapPropertySource(DEFAULT_BOOTSTRAP_CONFIG_NAME, bootstrapConfig));
		try {
			applySettings(applicationName, env, environment);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to apply settings from remote for this application", e);
		}

	}

	protected abstract void applySettings(String applicationName, String env, ConfigurableEnvironment environment) throws Exception;

}
