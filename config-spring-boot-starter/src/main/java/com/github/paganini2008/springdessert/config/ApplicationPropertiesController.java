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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ApplicationPropertiesController
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@RestController
@RequestMapping("/config")
public class ApplicationPropertiesController {

	@Autowired
	private ApplicationProperties applicationProperties;

	@GetMapping("")
	public ResponseEntity<Map<Object, Object>> config() {
		return ResponseEntity.ok(MapUtils.sort(applicationProperties, (left, right) -> {
			String leftKey = (String) left.getKey();
			String rightKey = (String) right.getKey();
			return leftKey.compareTo(rightKey);
		}));
	}

}
