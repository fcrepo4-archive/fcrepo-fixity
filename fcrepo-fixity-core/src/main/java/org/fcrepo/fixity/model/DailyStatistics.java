/**
 *
 */

package org.fcrepo.fixity.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.fixity.service.FixityService;

/**
 * @author frank asseg
 */
@Entity
@Table(name = "FIXITY_STATS")
@XmlRootElement(name = "statistics", namespace = FixityService.FIXITY_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class DailyStatistics {

    @Id
    @GeneratedValue
    @XmlAttribute(name = "id")
    private long statisticId;

    @XmlAttribute(name = "success-count")
    private long successCount;

    @XmlAttribute(name = "error-count")
    private long errorCount;

    @XmlAttribute(name = "repair-count")
    private long repairCount;

    @Temporal(TemporalType.DATE)
    @XmlAttribute(name = "date")
    private Date statisticsDate;

    /**
     * TODO
     * 
     * @return
     */
    public Date getStatisticsDate() {
        return statisticsDate;
    }

    /**
     * @param statisticsDate the Date to set
     */
    public void setStatisticsDate(Date statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

    /**
     * TODO
     * 
     * @return the number of successes
     */
    public long getSuccessCount() {
        return successCount;
    }

    /**
     * TODO
     * 
     * @param successCount
     */
    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    /**
     * TODO
     * 
     * @return the error count
     */
    public long getErrorCount() {
        return errorCount;
    }

    /**
     * TODO
     * 
     * @param errorCount
     */
    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    /**
     * @return the repair count
     */
    public long getRepairCount() {
        return repairCount;
    }

    /**
     * TODO
     * 
     * @param repairCount
     */
    public void setRepairCount(long repairCount) {
        this.repairCount = repairCount;
    }

    /**
     * TODO
     * 
     * @return
     */
    public long getStatisticId() {
        return statisticId;
    }

    /**
     * TODO
     * 
     * @param statisticId
     */
    public void setStatisticId(long statisticId) {
        this.statisticId = statisticId;
    }

}
