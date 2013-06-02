/**
 *
 */

package org.fcrepo.fixity.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * @author frank asseg
 *
 */
@Entity
public class ObjectFixityResult {

    public static enum FixityResult {
        SUCCESS, ERROR, REPAIRED;
    }

    @Id
    @GeneratedValue
    private long resultId;

    private String uri;

    private Date timeStamp;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="RESULT_ID")
    private List<DatastreamFixityResult> successes;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="RESULT_ID")
    private List<DatastreamFixityResult> errors;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="RESULT_ID")
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
}
