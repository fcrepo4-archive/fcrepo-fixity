/**
 *
 */

package org.fcrepo.fixity.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * @author frank asseg
 *
 */
@Entity
@Table(name="FIXITY_STATS")
public class FixityStatistics {

    @Id
    @GeneratedValue
    private long statisticId;

    private long successCount;

    private long errorCount;

    private long repairCount;

    @Type(type="date")
    private Date statisticsDate;

    public Date getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(Date statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

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
