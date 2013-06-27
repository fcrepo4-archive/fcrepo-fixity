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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.fcrepo.fixity.service.FixityService;
import org.hibernate.annotations.DiscriminatorOptions;

/**
 * @author frank asseg
 */
@Entity
@Table(name = "FIXITY_DATASTREAM_RESULTS")
@DiscriminatorColumn(name = "RESULT_DISCRIMINATOR",
        discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorOptions(force = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement(name = "datastream-fixity-result",
        namespace = FixityService.FIXITY_NAMESPACE)
@XmlSeeAlso({DatastreamFixitySuccess.class, DatastreamFixityError.class,
        DatastreamFixityRepaired.class})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DatastreamFixityResult {

    public enum ResultType {
        SUCCESS, ERROR, REPAIRED;
    }

    @Id
    @GeneratedValue
    @Column(name = "RESULT_ID")
    private long resultId;

    @Column(name = "DS_URI")
    private String uri;

    @Column(name = "RESULT_TYPE")
    private ResultType type;

    protected DatastreamFixityResult() {
    }

    protected DatastreamFixityResult(ResultType type) {
        this.type = type;
    }

    protected DatastreamFixityResult(String uri, ResultType type) {
        this.type = type;
        this.uri = uri;
    }

    /**
     * TODO
     * 
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * TODO
     * 
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * TODO
     * 
     * @return
     */
    public long getResultId() {
        return resultId;
    }

    /**
     * TODO
     * 
     * @param resultId
     */
    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    /**
     * TODO
     * 
     * @return
     */
    public ResultType getType() {
        return type;
    }
}
