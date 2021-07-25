/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * GitConfigProperties
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class GitConfigProperties extends GitRepoProperties {

	private static final long serialVersionUID = 3977102427571801020L;

	private final Map<String, String> defaultProperties;

	public GitConfigProperties() {
		this(new HashMap<String, String>());
	}

	public GitConfigProperties(Map<String, String> defaultProperties) {
		this.defaultProperties = defaultProperties;
	}

	protected Properties createObject() throws Exception {
		Properties p = super.createObject();
		for (Map.Entry<String, String> entry : defaultProperties.entrySet()) {
			p.setProperty(entry.getKey(), entry.getValue());
		}
		return p;
	}

	public Map<String, String> getDefaultProperties() {
		return defaultProperties;
	}

}
