package com.github.paganini2008.springworld.jdbc;

import java.sql.SQLException;

/**
 * 
 * NoGeneratedKeyException
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class NoGeneratedKeyException extends SQLException {

	private static final long serialVersionUID = -1903213976808407478L;

	public NoGeneratedKeyException(String sql) {
		super(sql);
	}

}
