package org.springtribe.framework.fastjpa;

/**
 * 
 * PathMismatchedException
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class PathMismatchedException extends IllegalArgumentException {

	private static final long serialVersionUID = 6298298843692196713L;

	public PathMismatchedException(String alias, String attributeName) {
		super(alias + "." + attributeName);
	}

}
