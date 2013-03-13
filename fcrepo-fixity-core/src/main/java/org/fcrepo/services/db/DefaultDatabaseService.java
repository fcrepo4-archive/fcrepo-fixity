package org.fcrepo.services.db;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.fcrepo.services.fixity.model.DailyStatistics;
import org.fcrepo.services.fixity.model.DatastreamFixityResult;
import org.fcrepo.services.fixity.model.DatastreamFixityResult.ResultType;
import org.fcrepo.services.fixity.model.FixityResult;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
		int errors = res.getErrors().size();
		int successes = res.getSuccesses().size();
		addStat(successes, errors);
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	public long getResultCount() {
		Session sess = sessionFactory.openSession();
		long value = (Long) sess.createCriteria(FixityResult.class)
				.setProjection(Projections.rowCount())
				.uniqueResult();
		sess.close();
		return value;
	}

	@Override
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	public long getErrorCount() {
		Session sess = sessionFactory.openSession();
		Long value = (Long) sess.createCriteria(DatastreamFixityResult.class)
				.add(Restrictions.eq("type", ResultType.ERROR))
				.setProjection(Projections.rowCount())
				.uniqueResult();
		sess.close();
		return value;
	}

	@Override
	@Transactional
	public void addStat(int successCount, int errorCount) {
		Session sess = sessionFactory.openSession();
		DailyStatistics stat = getDailyStat(new Date());
		stat.setErrorCount(stat.getErrorCount() + errorCount);
		stat.setSuccessCount(stat.getSuccessCount() + successCount);
		sess.saveOrUpdate(stat);
		sess.flush();
		sess.close();
	}

	@Transactional(readOnly = true)
	private DailyStatistics getDailyStat(Date date) {
		Session sess = sessionFactory.openSession();
		DailyStatistics stat = (DailyStatistics) sess.createCriteria(DailyStatistics.class)
				.add(Restrictions.eq("date", date))
				.uniqueResult();
		if (stat == null) {
			stat = new DailyStatistics();
			stat.setDate(date);
			stat.setErrorCount(0);
			stat.setSuccessCount(0);
		}
		sess.close();
		return stat;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DailyStatistics> getDailyStatistics() {
		Session sess = sessionFactory.openSession();
		List<DailyStatistics> stats = sess.createCriteria(DailyStatistics.class)
				.addOrder(Order.asc("date"))
				.list();
		sess.close();
		return stats;
	}

	@Override
	@Transactional(readOnly = true)
	public FixityResult getResult(long recordId) {
		Session sess = sessionFactory.openSession();
		FixityResult result = (FixityResult) sess.get(FixityResult.class, recordId);
		Hibernate.initialize(result.getErrors());
		Hibernate.initialize(result.getSuccesses());
		sess.close();
		return result;
	}
}
