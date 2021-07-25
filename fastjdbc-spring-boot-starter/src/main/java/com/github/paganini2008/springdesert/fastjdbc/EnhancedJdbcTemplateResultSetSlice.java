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
package com.github.paganini2008.springdesert.fastjdbc;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * EnhancedJdbcTemplateResultSetSlice
 *
 * @author Fred Feng
 * @version 1.0
 */
@Slf4j
public class EnhancedJdbcTemplateResultSetSlice<T> implements ResultSetSlice<T> {

	private final SqlParameterSource sqlParameterSource;
	private final RowMapper<T> rowMapper;
	private final PageableSql pageableSql;
	private final NamedParameterJdbcOperations jdbcOperations;

	public EnhancedJdbcTemplateResultSetSlice(NamedParameterJdbcOperations jdbcOperations, PageableSql pageableSql,
			SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
		this.jdbcOperations = jdbcOperations;
		this.pageableSql = pageableSql;
		this.sqlParameterSource = sqlParameterSource;
		this.rowMapper = rowMapper;
	}

	@Override
	public int rowCount() {
		final String execution = pageableSql.countableSql();
		if (log.isTraceEnabled()) {
			log.trace("Execute Sql: " + execution);
		}
		return jdbcOperations.queryForObject(execution, sqlParameterSource, Integer.class);
	}

	@Override
	public List<T> list(int maxResults, int firstResult) {
		final String execution = pageableSql.pageableSql(maxResults, firstResult);
		if (log.isTraceEnabled()) {
			log.trace("Execute Sql: " + execution);
		}
		return jdbcOperations.query(execution, sqlParameterSource, rowMapper);
	}

	public NamedParameterJdbcOperations getJdbcOperations() {
		return jdbcOperations;
	}

}
