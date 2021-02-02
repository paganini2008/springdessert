package org.springdessert.framework.fastjpa.support.hibernate;

import javax.persistence.EntityManager;

import org.springdessert.framework.fastjpa.support.EntityDao;
import org.springdessert.framework.fastjpa.support.EntityDaoSupport;
import org.springdessert.framework.fastjpa.support.RowMapper;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * HibernateDaoSupport
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HibernateDaoSupport<E, ID> extends EntityDaoSupport<E, ID> implements EntityDao<E, ID> {

	public HibernateDaoSupport(Class<E> entityClass, EntityManager em) {
		super(entityClass, em);
	}

	public <T> ResultSetSlice<T> select(String sql, Object[] arguments, RowMapper<T> mapper) {
		return new HibernateRowMapperResultSetSlice<T>(sql, arguments, em, mapper);
	}

}
