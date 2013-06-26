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
 */
@Entity
@XmlRootElement(name = "error", namespace = FixityService.FIXITY_NAMESPACE)
public class DatastreamFixityError extends DatastreamFixityResult {

    /**
     * TODO
     */
    public DatastreamFixityError() {
        super(ResultType.ERROR);
    }

    /**
     * TODO
     * 
     * @param uri
     */
    public DatastreamFixityError(String uri) {
        super(uri, ResultType.ERROR);
    }

    /**
     * TODO
     * 
     * @param uri
     * @param details
     */
    public DatastreamFixityError(String uri, String details) {
        super(uri, ResultType.ERROR);
        this.details = details;
    }

    @Column(name = "ERROR_DETAILS")
    private String details;

    /**
     * TODO
     * 
     * @return
     */
    public String getDetails() {
        return details;
    }

    /**
     * TODO
     * 
     * @param details
     */
    public void setDetails(String details) {
        this.details = details;
    }

}
