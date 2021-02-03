package org.springtribe.framework.fastjpa.support;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springtribe.framework.fastjpa.Filter;
import org.springtribe.framework.fastjpa.JpaDelete;
import org.springtribe.framework.fastjpa.JpaQuery;
import org.springtribe.framework.fastjpa.JpaUpdate;

/**
 * 
 * EntityDao
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@NoRepositoryBean
public interface EntityDao<E, ID> extends JpaRepositoryImplementation<E, ID>, NativeSqlOperations<E> {

	Class<E> getEntityClass();

	boolean exists(Filter filter);

	long count(Filter filter);

	List<E> findAll(Filter filter);

	List<E> findAll(Filter filter, Sort sort);

	Page<E> findAll(Filter filter, Pageable pageable);

	Optional<E> findOne(Filter filter);

	<T extends Comparable<T>> T max(String property, Filter filter, Class<T> requiredType);

	<T extends Comparable<T>> T min(String property, Filter filter, Class<T> requiredType);

	<T extends Number> T avg(String property, Filter filter, Class<T> requiredType);

	<T extends Number> T sum(String property, Filter filter, Class<T> requiredType);

	JpaUpdate<E> update();

	JpaDelete<E> delete();

	JpaQuery<E> select();

}
