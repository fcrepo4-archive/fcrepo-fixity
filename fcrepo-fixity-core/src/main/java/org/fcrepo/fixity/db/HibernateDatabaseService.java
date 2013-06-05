/**
 *
 */

package org.fcrepo.fixity.db;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.fcrepo.fixity.model.DailyStatistics;
import org.fcrepo.fixity.model.DatastreamFixityError;
import org.fcrepo.fixity.model.DatastreamFixityRepaired;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.DatastreamFixitySuccess;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.fcrepo.fixity.model.Statistics;
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

    private static final Logger LOG = LoggerFactory
            .getLogger(HibernateDatabaseService.class);

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
        final Session sess = this.sessionFactory.openSession();
        try {
            /* save the fixity result */
            sess.save(res);
            /* update today's statistics */
            addFixityStatistics(res.getSuccessCount(), res.getErrorCount(), res
                    .getRepairCount());
            sess.flush();
        } finally {
            sess.close();
        }
    }

    @Override
    @Transactional
    public void addFixityStatistics(final int sucesses, final int errors,
            final int repairs) {
        final Session sess = this.sessionFactory.openSession();
        try {
            final DailyStatistics stat =
                    this.getFixityStatisticForDate(new Date());
            stat.setErrorCount(stat.getErrorCount() + errors);
            stat.setRepairCount(stat.getRepairCount() + repairs);
            stat.setSuccessCount(stat.getSuccessCount() + sucesses);
            sess.saveOrUpdate(stat);
            sess.flush();
        } finally {
            sess.close();
        }
    }

    @Override
    @Transactional
    public DailyStatistics getFixityStatisticForDate(Date date) {
        final Session sess = this.sessionFactory.openSession();
        try {
            DailyStatistics stat = (DailyStatistics) sess.createCriteria(
                    DailyStatistics.class)
                    .add(Restrictions.eq("statisticsDate", date))
                    .uniqueResult();
            if (stat == null) {
                stat = new DailyStatistics();
                stat.setStatisticsDate(new Date());
            }
            return stat;
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
            return sess.createCriteria(ObjectFixityResult.class)
                    .add(Restrictions.eq("uri", uri))
                    .list();
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
            return (Long) sess.createCriteria(DatastreamFixityError.class)
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
            return (Long) sess.createCriteria(DatastreamFixitySuccess.class)
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
            return (Long) sess.createCriteria(DatastreamFixityRepaired.class)
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
    public void addStat(DailyStatistics stat) {
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
    public List<DailyStatistics> getDailyStatistics() {
        final Session sess = sessionFactory.openSession();
        try {
            return  sess.createCriteria(DailyStatistics.class).list();
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllResults() {
        final Session sess = sessionFactory.openSession();
        try {
            /* truncate all tables */
            sess.createQuery(
                    String.format("delete from " +
                            DatastreamFixityResult.class.getSimpleName()))
                    .executeUpdate();
            sess.createQuery(
                    String.format("delete from " +
                            ObjectFixityResult.class.getSimpleName()))
                    .executeUpdate();
            sess.createQuery(
                    String.format("delete from " +
                            DailyStatistics.class.getSimpleName()))
                    .executeUpdate();
            sess.flush();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteResult(long resultId) {
        final Session sess = sessionFactory.openSession();
        try {
            sess.createQuery(
                    String.format(
                            "delete from ObjectFixityResult o where o.resultId='%s'",
                            resultId)).executeUpdate();
            sess.flush();
        } finally {
            sess.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getStatistics()
     */
    @Override
    public Statistics getStatistics() {
        Statistics stats = new Statistics();
        stats.setErrorCount(getErrorCount());
        stats.setNumObjects(getObjectCount());
        stats.setRepairCount(getRepairCount());
        stats.setSuccessCount(getSuccessCount());
        return stats;
    }

    /* (non-Javadoc)
     * @see org.fcrepo.fixity.db.FixityDatabaseService#getDatastreamFixityResult(long)
     */
    @Override
    public DatastreamFixityResult getDatastreamFixityResult(long id) {
        final Session sess = sessionFactory.openSession();
        try {
            return (DatastreamFixityResult) sess.get(DatastreamFixityResult.class, id);
        } finally {
            sess.close();
        }

    }
}
