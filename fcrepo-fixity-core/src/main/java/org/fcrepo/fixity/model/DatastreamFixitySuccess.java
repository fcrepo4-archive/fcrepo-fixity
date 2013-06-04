/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.fixity.service.FixityService;

/**
 * @author frank asseg
 *
 */
@Entity
@XmlRootElement(name="success", namespace=FixityService.FIXITY_NAMESPACE)
public class DatastreamFixitySuccess extends DatastreamFixityResult {

    public DatastreamFixitySuccess() {
        super(ResultType.SUCCESS);
    }

    public DatastreamFixitySuccess(String uri) {
        super(uri, ResultType.SUCCESS);
    }

}
