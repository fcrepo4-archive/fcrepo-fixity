/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */

package org.fcrepo.fixity.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.fixity.service.FixityService;

/**
 * @author frank asseg
 */
@Entity
@XmlRootElement(name = "repaired", namespace = FixityService.FIXITY_NAMESPACE)
public class DatastreamFixityRepaired extends DatastreamFixityResult {

    /**
     * TODO
     */
    public DatastreamFixityRepaired() {
        super(ResultType.REPAIRED);
    }

    /**
     * TODO
     * 
     * @param uri
     */
    public DatastreamFixityRepaired(String uri) {
        super(uri, ResultType.REPAIRED);
    }
}
