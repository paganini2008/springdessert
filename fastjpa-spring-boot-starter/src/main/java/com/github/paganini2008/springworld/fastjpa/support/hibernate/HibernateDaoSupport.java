package com.github.paganini2008.springworld.fastjpa.support.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.springworld.fastjpa.support.EntityDao;
import com.github.paganini2008.springworld.fastjpa.support.EntityDaoSupport;
import com.github.paganini2008.springworld.fastjpa.support.RowMapper;

/**
 * 
 * HibernateDaoSupport
 *
 * @author Fred Feng
 * @version 1.0
 */
public class HibernateDaoSupport<E, ID> extends EntityDaoSupport<E, ID> implements EntityDao<E, ID> {

	public HibernateDaoSupport(Class<E> entityClass, EntityManager em) {
		super(entityClass, em);
	}

	@Override
	public <T> ResultSetSlice<T> select(String sql, Object[] arguments, Class<T> resultClass) {
		return new HibernateNativeQueryResultSetSlice<T>(sql, arguments, em, new BeanPropertyQueryResultSetExtractor<T>(resultClass));
	}

	@Override
	public <T> ResultSetSlice<T> select(String sql, Object[] arguments, RowMapper<T> rowMapper) {
		return new HibernateNativeQueryResultSetSlice<T>(sql, arguments, em, new MappedQueryResultSetExtractor<T>(rowMapper));
	}

	@SuppressWarnings("unchecked")
	private static class MappedQueryResultSetExtractor<T> implements QueryResultSetExtractor<T> {

		private final RowMapper<T> rowMapper;

		MappedQueryResultSetExtractor(RowMapper<T> rowMapper) {
			this.rowMapper = rowMapper;
		}

		@Override
		public List<T> extractData(Session session, NativeQuery<?> query) {
			List<T> results = new ArrayList<T>();
			query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) query.getResultList();
			int index = 0;
			for (Map<String, Object> data : dataList) {
				T mappedResult = rowMapper.mapRow(index++, data);
				results.add(mappedResult);
			}
			return results;
		}

	}

	@SuppressWarnings("unchecked")
	private static class BeanPropertyQueryResultSetExtractor<T> implements QueryResultSetExtractor<T> {

		private final Class<T> resultClass;

		BeanPropertyQueryResultSetExtractor(Class<T> resultClass) {
			this.resultClass = resultClass;
		}

		@Override
		public List<T> extractData(Session session, NativeQuery<?> query) {
			query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(resultClass));
			return (List<T>) query.getResultList();
		}

	}

}
