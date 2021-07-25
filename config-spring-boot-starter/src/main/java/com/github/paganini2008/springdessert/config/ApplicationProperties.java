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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.github.paganini2008.devtools.collection.EventBasedRefreshingProperties;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.io.PathUtils;

/**
 * 
 * ApplicationProperties
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class ApplicationProperties extends EventBasedRefreshingProperties {

	private static final long serialVersionUID = -4386392261078899614L;

	protected String applicationName;
	protected String env = "dev";

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	@Override
	public void store(File outputFile, String comments) throws IOException {
		OutputStream out = null;
		try {
			String baseName = PathUtils.getBaseName(outputFile.getName());
			String extension = PathUtils.getExtension(outputFile.getName());
			String fileName = baseName + "_" + env + "." + extension;
			out = FileUtils.openOutputStream(new File(outputFile.getParentFile(), fileName), false);
			delegate.store(out, comments);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

}
