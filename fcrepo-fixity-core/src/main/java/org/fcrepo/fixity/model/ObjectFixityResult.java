/**
 *
 */

package org.fcrepo.fixity.model;

import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.fixity.service.FixityService;

/**
 * @author frank asseg
 */
@Entity
@Table(name = "FIXITY_OBJECT_RESULTS")
@XmlRootElement(name = "object-fixity-result",
        namespace = FixityService.FIXITY_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class ObjectFixityResult {

    private static final String COLUMN_OBJECT_FIXITY_ID = "OBJECT_FIXITY_ID";

    public static enum FixityResult {
        SUCCESS, ERROR, REPAIRED;
    }

    @Id
    @GeneratedValue
    @Column(name = COLUMN_OBJECT_FIXITY_ID)
    @XmlAttribute(name = "id")
    private long resultId;

    @Column(name = "OBJECT_FIXITY_STATE")
    @XmlAttribute(name = "state")
    private FixityResult state;

    @Column(name = "OBJECT_URI")
    @XmlAttribute(name = "uri")
    private String uri;

    @Column(name = "OBJECT_FIXITY_TIMESTAMP")
    @XmlAttribute(name = "timestamp")
    private Date timeStamp;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = COLUMN_OBJECT_FIXITY_ID)
    @XmlElement(name = "successes", namespace = FixityService.FIXITY_NAMESPACE,
            type = ArrayList.class)
    // NOSONAR
    private List<DatastreamFixitySuccess> successes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = COLUMN_OBJECT_FIXITY_ID)
    @XmlElement(name = "errors", namespace = FixityService.FIXITY_NAMESPACE,
            type = ArrayList.class)
    // NOSONAR
    private List<DatastreamFixityError> errors;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = COLUMN_OBJECT_FIXITY_ID)
    @XmlElement(name = "repairs", namespace = FixityService.FIXITY_NAMESPACE,
            type = ArrayList.class)
    // NOSONAR
    private List<DatastreamFixityRepaired> repairs;

    /**
     * TODO
     * 
     * @return
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * TODO
     * 
     * @param timeStamp
     */
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * TODO
     * 
     * @return
     */
    public long getResultId() {
        return resultId;
    }

    /**
     * TODO
     * 
     * @param resultId
     */
    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    /**
     * TODO
     * 
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * TODO
     * 
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * TODO
     * 
     * @return
     */
    public List<DatastreamFixitySuccess> getSuccesses() {
        return successes;
    }

    /**
     * TODO
     * 
     * @param successes
     */
    public void setSuccesses(List<DatastreamFixitySuccess> successes) {
        this.successes = successes;
    }

    /**
     * TODO
     * 
     * @return
     */
    public List<DatastreamFixityError> getErrors() {
        return errors;
    }

    /**
     * TODO
     * 
     * @param errors
     */
    public void setErrors(List<DatastreamFixityError> errors) {
        this.errors = errors;
    }

    /**
     * TODO
     * 
     * @return
     */
    public List<DatastreamFixityRepaired> getRepaired() {
        return repairs;
    }

    /**
     * TODO
     * 
     * @param repairs
     */
    public void setRepairs(List<DatastreamFixityRepaired> repairs) {
        this.repairs = repairs;
    }

    /**
     * TODO
     * 
     * @return
     */
    public FixityResult getState() {
        return state;
    }

    /**
     * TODO
     * 
     * @param state
     */
    public void setState(FixityResult state) {
        this.state = state;
    }

    /**
     * TODO
     * 
     * @return
     */
    public List<DatastreamFixityRepaired> getRepairs() {
        return repairs;
    }

    /**
     * Get the number of successes
     * 
     * @return the number of successes
     */
    public int getSuccessCount() {
        return (successes == null) ? 0 : successes.size();
    }

    /**
     * Get the number of errors
     * 
     * @return the number of errors
     */
    public int getErrorCount() {
        return (errors == null) ? 0 : errors.size();
    }

    /**
     * Get the number of repairs
     * 
     * @return the number of repairs
     */
    public int getRepairCount() {
        return (repairs == null) ? 0 : repairs.size();
    }
}
