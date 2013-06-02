/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author frank asseg
 *
 */
@Entity
public class DatastreamFixityError extends DatastreamFixityResult {

    public DatastreamFixityError() {
        super(ResultType.ERROR);
    }

    public DatastreamFixityError(String uri) {
        super(uri, ResultType.ERROR);
    }

    @Column(name = "ERROR_DETAILS")
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
