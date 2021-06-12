/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.springworld.jdbc;

import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.jdbc.DefaultPageableSql;
import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * EnhancedJdbcTemplate
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Component
public class EnhancedJdbcTemplate extends NamedParameterJdbcTemplate {

	public EnhancedJdbcTemplate(JdbcOperations jdbcOperations) {
		super(jdbcOperations);
	}

	public ResultSetSlice<Map<String, Object>> slice(String sql, SqlParameterSource sqlParameterSource) {
		return slice(sql, sqlParameterSource, new NamedColumnMapRowMapper());
	}

	public <T> ResultSetSlice<T> slice(String sql, SqlParameterSource sqlParameterSource, Class<T> elementClass) {
		return slice(sql, sqlParameterSource, new SingleColumnRowMapper<T>(elementClass));
	}

	public <T> ResultSetSlice<T> slice(String sql, SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
		return slice(new DefaultPageableSql(sql), sqlParameterSource, rowMapper);
	}

	public <T> ResultSetSlice<Map<String, Object>> slice(PageableSql pageableSql, SqlParameterSource sqlParameterSource) {
		return new EnhancedJdbcTemplateResultSetSlice<Map<String, Object>>(this, pageableSql, sqlParameterSource,
				new NamedColumnMapRowMapper());
	}

	public <T> ResultSetSlice<T> slice(PageableSql pageableSql, SqlParameterSource sqlParameterSource, Class<T> elementClass) {
		return new EnhancedJdbcTemplateResultSetSlice<T>(this, pageableSql, sqlParameterSource, new SingleColumnRowMapper<T>(elementClass));
	}

	public <T> ResultSetSlice<T> slice(PageableSql pageableSql, SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
		return new EnhancedJdbcTemplateResultSetSlice<T>(this, pageableSql, sqlParameterSource, rowMapper);
	}

}
