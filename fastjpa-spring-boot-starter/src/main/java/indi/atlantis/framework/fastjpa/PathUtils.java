package indi.atlantis.framework.fastjpa;

import javax.persistence.criteria.Path;

/**
 * 
 * PathUtils
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class PathUtils {

	public static <X, T> Path<T> createPath(Path<X> root, String property) {
		Path<T> path = null;
		int index = property.indexOf(".");
		if (index > 0) {
			path = root.get(property.substring(0, index));
			String[] names = property.substring(index + 1).split("\\.");
			for (int i = 0; i < names.length; i++) {
				path = path.get(names[i]);
			}
		} else {
			path = root.get(property);
		}
		return path;
	}

}
