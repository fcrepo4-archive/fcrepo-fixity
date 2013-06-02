/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Entity;

/**
 * @author frank asseg
 *
 */
@Entity
public class DatastreamFixityRepaired extends DatastreamFixityResult {

    public DatastreamFixityRepaired() {
        super(ResultType.REPAIRED);
    }

    public DatastreamFixityRepaired(String uri) {
        super(uri, ResultType.REPAIRED);
    }
}
