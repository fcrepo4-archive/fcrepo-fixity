/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.fcrepo.fixity.model.ObjectFixityResult.FixityResult;

/**
 * @author frank asseg
 *
 */
@Entity
public class DatastreamFixityResult {

    @Id
    @GeneratedValue
    @Column(name = "DS_FIXITY_ID")
    private long resultId;

    @Column(name = "DS_FIXITY_RESULT_TYPE")
    private FixityResult resultType;

    @Column(name = "DS_URI")
    private String uri;

    public DatastreamFixityResult(){
        super();
    }

    public DatastreamFixityResult(String uri, FixityResult resultType) {
        super();
        this.resultType = resultType;
        this.uri = uri;
    }

    public FixityResult getResultType() {
        return resultType;
    }

    public void setResultType(FixityResult resultType) {
        this.resultType = resultType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
