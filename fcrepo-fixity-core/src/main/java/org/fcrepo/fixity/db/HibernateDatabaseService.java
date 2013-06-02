/**
 *
 */

package org.fcrepo.fixity.db;

import java.util.Collection;
import java.util.List;

import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.FixityStatistics;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author frank asseg
 *
 */
@Service
public class HibernateDatabaseService implements FixityDatabaseService {

    @Autowired
    private SessionFactory sessionFactory;

    /*
     * (non-Javadoc)
     * @see
     * org.fcrepo.fixity.db.FixityDatabaseService#addResult(org.fcrepo.fixity
     * .model.ObjectFixityResult)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addResult(final ObjectFixityResult res) {
        Session sess = sessionFactory.openSession();
        try {
            sess.save(res);
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.fcrepo.fixity.db.FixityDatabaseService#getResults(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<ObjectFixityResult> getResults(String uri) {
        Session sess = sessionFactory.openSession();
        try {
            return sess.createCriteria(ObjectFixityResult.class).add(
                    Restrictions.eq("uri", uri)).list();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.fcrepo.fixity.db.FixityDatabaseService#addResults(java.util.Collection
     * )
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addResults(Collection<ObjectFixityResult> results) {
        for (ObjectFixityResult res : results) {
            this.addResult(res);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getResults(int, int)
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<ObjectFixityResult> getResults(int offset, int length) {
        Session sess = sessionFactory.openSession();
        try {
            return sess.createCriteria(ObjectFixityResult.class)
                    .setMaxResults(length)
                    .setFirstResult(offset)
                    .list();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getResultCount()
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getResultCount() {
        final Session sess = sessionFactory.openSession();
        try {
            return (Long) sess.createCriteria(ObjectFixityResult.class)
                    .setProjection(Projections.rowCount())
                    .uniqueResult();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getErrorCount()
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getErrorCount() {
        final Session sess = sessionFactory.openSession();
        try {
            return (Long) sess.createCriteria(DatastreamFixityResult.class)
                    .add(Restrictions.eq("resultType", "ERROR"))
                    .setProjection(Projections.rowCount())
                    .uniqueResult();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getSuccessCount()
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getSuccessCount() {
        final Session sess = sessionFactory.openSession();
        try {
            return (Long) sess.createCriteria(DatastreamFixityResult.class)
                    .add(Restrictions.eq("resultType", "SUCCESS"))
                    .setProjection(Projections.rowCount())
                    .uniqueResult();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getRepairCount()
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getRepairCount() {
        final Session sess = sessionFactory.openSession();
        try {
            return (Long) sess.createCriteria(DatastreamFixityResult.class)
                    .add(Restrictions.eq("resultType", "REPAIR"))
                    .setProjection(Projections.rowCount())
                    .uniqueResult();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getObjectCount()
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getObjectCount() {
        final Session sess = sessionFactory.openSession();
        try {
            return (Long) sess.createCriteria(ObjectFixityResult.class)
                    .setProjection(Projections.rowCount())
                    .uniqueResult();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#addStat(int, int, int)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addStat(FixityStatistics stat) {
        final Session sess = sessionFactory.openSession();
        try {
            sess.save(stat);
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getFixityStatistics()
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<FixityStatistics> getFixityStatistics() {
        final Session sess = sessionFactory.openSession();
        try {
            return sess.createCriteria(FixityStatistics.class)
                    .list();
        } finally {
            sess.close();
        }

    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getResult(long)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public ObjectFixityResult getResult(long resultId) {
        final Session sess = sessionFactory.openSession();
        try {
            return (ObjectFixityResult) sess.get(ObjectFixityResult.class,
                    resultId);
        } finally {
            sess.close();
        }
    }

}
