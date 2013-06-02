/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.DiscriminatorOptions;

/**
 * @author frank asseg
 *
 */
@Entity
@Table(name="FIXITY_DATASTREAM_RESULTS")
@DiscriminatorColumn(name="RESULT_DISCRIMINATOR",discriminatorType=DiscriminatorType.INTEGER)
@DiscriminatorOptions(force=true)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class DatastreamFixityResult {

    public enum ResultType {
        SUCCESS, ERROR, REPAIRED;
    }

    @Id
    @GeneratedValue
    @Column(name = "RESULT_ID")
    private long resultId;

    @Column(name = "DS_URI")
    private String uri;

    @Column(name = "RESULT_TYPE")
    private ResultType type;

    protected DatastreamFixityResult() {
    }

    protected DatastreamFixityResult(ResultType type) {
        this.type = type;
    }

    protected DatastreamFixityResult(String uri, ResultType type) {
        this.type = type;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public ResultType getType() {
        return type;
    }
}
