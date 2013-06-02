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
public class DatastreamFixitySuccess extends DatastreamFixityResult {

    public DatastreamFixitySuccess() {
        super(ResultType.SUCCESS);
    }

    public DatastreamFixitySuccess(String uri) {
        super(uri, ResultType.SUCCESS);
    }

}
