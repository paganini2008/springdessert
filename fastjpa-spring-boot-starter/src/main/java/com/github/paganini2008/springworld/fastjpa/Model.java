package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

/**
 * 
 * Model
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Model<X> {

	static final String ROOT = "this";

	default String getDefaultAlias() {
		return ROOT;
	}

	EntityType<X> getEntityType();

	Class<?> getRootType();

	Class<X> getType();

	boolean isManaged(Class<?> type);

	Root<?> getRoot();

	<T> Path<T> getAttribute(String attributeName);

	<T> Path<T> getAttribute(String name, String attributeName);

	boolean hasAttribute(String name, String attributeName);

	@SuppressWarnings("unchecked")
	default <T> Selection<T> getSelection() {
		List<Selection<?>> selections = getSelections(ROOT);
		return (Selection<T>) selections.get(0);
	}

	List<Selection<?>> getSelections(String name);

	default List<JpaAttributeDetail> getAttributeDetails() {
		return getAttributeDetails(ROOT);
	}

	List<JpaAttributeDetail> getAttributeDetails(String name);

	<Y> Model<Y> join(String attributeName, String alias, Predicate on);

	<Y> Model<Y> leftJoin(String attributeName, String alias, Predicate on);

	<Y> Model<Y> rightJoin(String attributeName, String alias, Predicate on);

	<S> Model<S> sibling(Model<S> sibling);

	static <X> Model<X> forRoot(Root<X> root) {
		return forRoot(root, ROOT);
	}

	static <X> Model<X> forRoot(Root<X> root, String alias) {
		return new RootModel<X>(root, alias, null);
	}

}
