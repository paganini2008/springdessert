package com.github.paganini2008.springworld.jdbc;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 
 * NamedColumnMapRowMapper
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class NamedColumnMapRowMapper extends ColumnMapRowMapper {

	@Override
	protected String getColumnKey(String columnName) {
		String name = super.getColumnKey(columnName);
		return JdbcUtils.convertUnderscoreNameToPropertyName(name);
	}

}
