/**
 *
 */

package org.fcrepo.fixity.model;

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
    private long resultId;

    private FixityResult resultType;

    private String uri;

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
