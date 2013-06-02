/**
 *
 */

package org.fcrepo.fixity.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author frank asseg
 *
 */
@Entity
@Table(name="FIXITY_OBJECTS")
public class ObjectFixityResult {

    public static enum FixityResult {
        SUCCESS, ERROR, REPAIRED;
    }

    @Id
    @GeneratedValue
    @Column(name="OBJECT_FIXITY_ID")
    private long resultId;

    @Column(name="OBJECT_URI")
    private String uri;

    @Column(name="TIMESTAMP")
    private Date timeStamp;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="OBJECT_FIXITY_ID")
    @Fetch(FetchMode.SUBSELECT)
    private List<DatastreamFixityResult> successes;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="OBJECT_FIXITY_ID")
    @Fetch(FetchMode.SUBSELECT)
    private List<DatastreamFixityResult> errors;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="OBJECT_FIXITY_ID")
    @Fetch(FetchMode.SUBSELECT)
    private List<DatastreamFixityResult> repairs;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<DatastreamFixityResult> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<DatastreamFixityResult> successes) {
        this.successes = successes;
    }

    public List<DatastreamFixityResult> getErrors() {
        return errors;
    }

    public void setErrors(List<DatastreamFixityResult> errors) {
        this.errors = errors;
    }

    public List<DatastreamFixityResult> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<DatastreamFixityResult> repairs) {
        this.repairs = repairs;
    }

    /**
     * Get the number of successes
     * @return the number of successes
     */
    public int getSuccessCount() {
        return (successes == null) ? 0 : successes.size();
    }

    /**
     * Get the number of errors
     * @return the number of errors
     */
    public int getErrorCount() {
        return (errors == null) ? 0 : errors.size();
    }

    /**
     * Get the number of repairs
     * @return the number of repairs
     */
    public int getRepairCount() {
        return (repairs == null) ? 0 : repairs.size();
    }
}
