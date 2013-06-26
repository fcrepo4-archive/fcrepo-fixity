
package org.fcrepo.fixity.db;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.fcrepo.fixity.model.DailyStatistics;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.fcrepo.fixity.model.Statistics;

/**
 * @author frank asseg
 */
public interface FixityDatabaseService {

    /**
     * Add a fixity result to the database
     * 
     * @param res the result
     */
    void addResult(ObjectFixityResult res);

    /**
     * Retrieve a {@link List} of {@link ObjectFixityResult} for a given fedora
     * object
     * 
     * @param uri the uri of the Object
     * @return
     */
    List<ObjectFixityResult> getResults(String uri);

    /**
     * Add a {@link Collection} of {@link ObjectFixityResult} to the database
     * 
     * @param results the {@link ObjectFixityResult} {@link Collection} to add
     *        to the database
     */
    void addResults(Collection<ObjectFixityResult> results);

    /**
     * Retrieve a {@link List} of {@link ObjectFixityResult} from a given offset
     * with up to <b>length</b> elements
     * 
     * @param offset the offset
     * @param length the length
     * @return a {@link List} of {@link ObjectFixityResult}
     */
    List<ObjectFixityResult> getResults(int offset, int length);

    /**
     * Retrieve the number of {@link ObjectFixityResult}s in the database
     * 
     * @return the number of results
     */
    long getResultCount();

    /**
     * Retrieve the number of errors in the database
     * 
     * @return the number of errors
     */
    long getErrorCount();

    /**
     * Retrieve the number of successes in the database
     * 
     * @return the number of successes
     */
    long getSuccessCount();

    /**
     * Retrieve the number of repairs in the database
     * 
     * @return the number of repairs
     */
    long getRepairCount();

    /**
     * Retrieve the number of distinct fedora objects for which results are
     * available in the database
     * 
     * @return the number of distinct objects
     */
    long getObjectCount();

    /**
     * Add a {@link DailyStatistics} record to the database
     * 
     * @param stat the {@link DailyStatistics} to add to the db
     */
    void addStat(DailyStatistics stat);

    /**
     * Retrieve a {@link List} of {@link DailyStatistics} form the database
     * 
     * @return The {@link DailyStatistics}s
     */
    List<DailyStatistics> getDailyStatistics();

    /**
     * Retrieve a distinct result from the database
     * 
     * @param resultId the id of the result to fetch from the database
     * @return the {@link ObjectFixityResult} associated with the resultId
     */
    ObjectFixityResult getResult(long resultId);

    /**
     * Remove a distinct result from the database
     * 
     * @param resultId the result id
     */
    void deleteResult(long resultId);

    /**
     * Delete All results from the database
     */
    void deleteAllResults();

    /**
     * Retrieve the {@link DailyStatistics} for a certain day
     * 
     * @param date the date
     * @return a {@link DailyStatistics} objects
     */
    DailyStatistics getFixityStatisticForDate(Date date);

    /**
     * Retrieve the general {@link Statistics}
     * 
     * @return a {@link Statistics} objects
     */
    Statistics getStatistics();

    /**
     * Update the {@link DailyStatistics} for today
     * 
     * @param sucesses the success count to add
     * @param errors the errors count to add
     * @param repairs the repair count to add
     */
    void addFixityStatistics(int sucesses, int errors, int repairs);

    /**
     * @param id TODO
     * @return TODO
     */
    DatastreamFixityResult getDatastreamFixityResult(long id);

}
