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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author frank asseg
 *
 */
@Service("fixityDatabaseService")
public class HibernateDatabaseService implements FixityDatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(HibernateDatabaseService.class);

    @Autowired
    private SessionFactory sessionFactory;

    /*
     * (non-Javadoc)
     * @see
     * org.fcrepo.fixity.db.FixityDatabaseService#addResult(org.fcrepo.fixity
     * .model.ObjectFixityResult)
     */
    @Override
    @Transactional
    public void addResult(final ObjectFixityResult res) {
        Session sess = sessionFactory.openSession();
        try {
            sess.save(res);
            sess.flush();
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
    @Transactional(readOnly = true)
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
    @Transactional
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
            sess.flush();
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public ObjectFixityResult getResult(long resultId) {
        final Session sess = sessionFactory.openSession();
        try {
            return (ObjectFixityResult) sess.get(ObjectFixityResult.class,
                    resultId);
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#deleteAllResults()
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void deleteAllResults() {
        final Session sess = sessionFactory.openSession();
        try {
            /* truncate the whole table and let hibernate remove the orphans */
            sess.createQuery(String.format("delete from DatastreamFixityResult")).executeUpdate();
            sess.createQuery(String.format("delete from ObjectFixityResult")).executeUpdate();
            sess.flush();
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            throw e;
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#deleteResult(long)
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void deleteResult(long resultId) {
        final Session sess = sessionFactory.openSession();
        try {
            sess.createQuery(String.format("delete from ObjectFixityResult o where o.resultId='%s'",resultId)).executeUpdate();
            sess.flush();
        } finally {
            sess.close();
        }
    }
}
