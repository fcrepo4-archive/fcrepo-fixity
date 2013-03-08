package org.fcrepo.services.db;

import java.util.Collection;
import java.util.List;

import org.fcrepo.services.fixity.model.FixityCheckResult;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DefaultDatabaseService implements DatabaseService {
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void addResult(FixityCheckResult res) {
		Session sess = sessionFactory.openSession();
		sess.save(res);
		sess.flush();
	}

	
	@Override
	public void addResults(Collection<FixityCheckResult> results) {
		for (FixityCheckResult result : results) {
			this.addResult(result);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<FixityCheckResult> getResults(String objectId) {
		Session sess = sessionFactory.openSession();
		List<FixityCheckResult> results = sess.createCriteria(FixityCheckResult.class)
				.add(Restrictions.eq("pid", objectId))
				.list();
		/* initialize the collections */
		for (FixityCheckResult r : results){
			Hibernate.initialize(r.getWarnings());
			Hibernate.initialize(r.getErrors());
		}
		return results;
	}

}
