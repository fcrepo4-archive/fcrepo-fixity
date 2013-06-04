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
@XmlRootElement(name="repaired", namespace=FixityService.FIXITY_NAMESPACE)
public class DatastreamFixityRepaired extends DatastreamFixityResult {

    public DatastreamFixityRepaired() {
        super(ResultType.REPAIRED);
    }

    public DatastreamFixityRepaired(String uri) {
        super(uri, ResultType.REPAIRED);
    }
}
