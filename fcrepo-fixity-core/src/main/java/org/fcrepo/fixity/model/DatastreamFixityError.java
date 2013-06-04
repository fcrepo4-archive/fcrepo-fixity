/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.fixity.service.FixityService;

/**
 * @author frank asseg
 *
 */
@Entity
@XmlRootElement(name="error", namespace=FixityService.FIXITY_NAMESPACE)
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
