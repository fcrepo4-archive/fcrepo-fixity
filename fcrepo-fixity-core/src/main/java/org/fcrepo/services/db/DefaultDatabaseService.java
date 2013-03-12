package org.fcrepo.services.db;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.fcrepo.services.fixity.model.DatastreamFixityResult;
import org.fcrepo.services.fixity.model.DatastreamFixityResult.ResultType;
import org.fcrepo.services.fixity.model.FixityResult;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

@Named("databaseService")
public class DefaultDatabaseService implements DatabaseService {
	@Inject
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@Transactional
	public void addResult(FixityResult res) {
		Session sess = sessionFactory.openSession();
		sess.save(res);
		sess.flush();
		sess.close();
	}

	@Override
	public void addResults(Collection<FixityResult> results) {
		for (FixityResult result : results) {
			this.addResult(result);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<FixityResult> getResults(String objectId) {
		Session sess = sessionFactory.openSession();
		List<FixityResult> results = sess.createCriteria(FixityResult.class)
				.add(Restrictions.eq("pid", objectId))
				.list();
		/* initialize the collections */
		for (FixityResult r : results) {
			Hibernate.initialize(r.getSuccesses());
			Hibernate.initialize(r.getErrors());
		}
		sess.close();
		return results;
	}

	@Override
	@Transactional
	public List<FixityResult> getResults(int offset, int length) {
		Session sess = sessionFactory.openSession();
		List<FixityResult> results = sess.createCriteria(FixityResult.class)
				.setFirstResult(offset)
				.setFetchSize(length)
				.list();
		/* initialize the collections */
		for (FixityResult r : results) {
			Hibernate.initialize(r.getSuccesses());
			Hibernate.initialize(r.getErrors());
		}
		sess.close();
		return results;
	}

	@Override
	@Transactional
	public long getResultCount() {
		Session sess = sessionFactory.openSession();
		long value = (Long) sess.createCriteria(FixityResult.class)
				.setProjection(Projections.rowCount())
				.uniqueResult();
		sess.close();
		return value;
	}

	@Override
	@Transactional
	public long getSuccessCount() {
		Session sess = sessionFactory.openSession();
		long value = (Long) sess.createCriteria(DatastreamFixityResult.class)
				.add(Restrictions.eq("type", ResultType.SUCCESS))
				.setProjection(Projections.rowCount())
				.uniqueResult();
		sess.close();
		return value;
	}

	@Override
	@Transactional
	public long getErrorCount() {
		Session sess = sessionFactory.openSession();
		Long value = (Long) sess.createCriteria(DatastreamFixityResult.class)
				.add(Restrictions.eq("type", ResultType.ERROR))
				.setProjection(Projections.rowCount())
				.uniqueResult();
		sess.close();
		return value;
	}

}
