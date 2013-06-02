/**
 *
 */

package org.fcrepo.fixity.db;

import java.util.Collection;
import java.util.List;

import org.fcrepo.fixity.model.FixityStatistics;
import org.fcrepo.fixity.model.ObjectFixityResult;

/**
 * @author frank asseg
 *
 */
public interface FixityDatabaseService {

    /**
     * Add a fixity result to the database
     * @param res the result
     */
    void addResult(ObjectFixityResult res);

    /**
     * Retrieve a {@link List} of {@link ObjectFixityResult} for a given fedora object
     * @param uri the uri of the Object
     * @return
     */
    List<ObjectFixityResult> getResults(String uri);

    /**
     * Add a {@link Collection} of {@link ObjectFixityResult} to the database
     * @param results the {@link ObjectFixityResult} {@link Collection} to add to the database
     */
    void addResults(Collection<ObjectFixityResult> results);

    /**
     * Retrieve a {@link List} of {@link ObjectFixityResult} from a given offset with up to <b>length</b> elements
     * @param offset the offset
     * @param length the length
     * @return a {@link List} of {@link ObjectFixityResult}
     */
    List<ObjectFixityResult> getResults(int offset, int length);

    /**
     * Retrieve the number of {@link ObjectFixityResult}s in the database
     * @return the number of results
     */
    long getResultCount();

    /**
     * Retrieve the number of errors in the database
     * @return the number of errors
     */
    long getErrorCount();

    /**
     * Retrieve the number of successes in the database
     * @return the number of successes
     */
    long getSuccessCount();

    /**
     * Retrieve the number of repairs in the database
     * @return the number of repairs
     */
    long getRepairCount();

    /**
     * Retrieve the number of distinct fedora objects for which results are available in the database
     * @return the number of distinct objects
     */
    long getObjectCount();

    /**
     * Add a {@link FixityStatistics} record to the database
     * @param stat the {@link FixityStatistics} to add to the db
     */
    void addStat(FixityStatistics stat);

    /**
     * Retrieve a {@link List} of {@link FixityStatistics} form the database
     * @return The {@link FixityStatistics}s
     */
    List<FixityStatistics> getFixityStatistics();

    /**
     * Retrieve a distinct result from the database
     * @param resultId the id of the result to fetch from the database
     * @return the {@link ObjectFixityResult} associated with the resultId
     */
    ObjectFixityResult getResult(long resultId);

    /**
     * Remove a distinct result from the database
     * @param resultId the result id
     */
    void deleteResult(long resultId);

    /**
     * Delete All results from the database
     */
    void deleteAllResults();

}
