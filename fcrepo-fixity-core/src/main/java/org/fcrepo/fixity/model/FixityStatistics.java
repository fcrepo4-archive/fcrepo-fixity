/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author frank asseg
 *
 */
@Entity
public class FixityStatistics {

    @Id
    @GeneratedValue
    private long statisticId;

    private long successCount;

    private long errorCount;

    private long repairCount;

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public long getRepairCount() {
        return repairCount;
    }

    public void setRepairCount(long repairCount) {
        this.repairCount = repairCount;
    }

}
