/**
 *
 */

package org.fcrepo.fixity.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.fixity.service.FixityService;
import org.hibernate.annotations.Type;

/**
 * @author frank asseg
 *
 */
@Entity
@Table(name="FIXITY_STATS")
@XmlRootElement(name="statistics",namespace=FixityService.NAMESPACE_FIXITY)
@XmlAccessorType(XmlAccessType.FIELD)
public class DailyStatistics {

    @Id
    @GeneratedValue
    @XmlAttribute(name="id")
    private long statisticId;

    @XmlAttribute(name="success-count")
    private long successCount;

    @XmlAttribute(name="error-count")
    private long errorCount;

    @XmlAttribute(name="repair-count")
    private long repairCount;

    @Type(type="date")
    @XmlAttribute(name="date")
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
