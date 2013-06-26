
package org.fcrepo.fixity.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fcrepo.fixity.service.FixityService;

@XmlRootElement(name = "general-stat",
        namespace = FixityService.FIXITY_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Statistics {

    @XmlAttribute(name = "object-count")
    private long numObjects;

    @XmlAttribute(name = "error-count")
    private long errorCount;

    @XmlAttribute(name = "success-count")
    private long successCount;

    @XmlAttribute(name = "repair-count")
    private long repairCount;

    /**
     * TODO
     * 
     * @return
     */
    public long getNumObjects() {
        return numObjects;
    }

    /**
     * TODO
     * 
     * @param numObjects
     */
    public void setNumObjects(long numObjects) {
        this.numObjects = numObjects;
    }

    /**
     * TODO
     * 
     * @return
     */
    public long getNumErrors() {
        return errorCount;
    }

    /**
     * TODO
     * 
     * @param numErrors
     */
    public void setErrorCount(long numErrors) {
        this.errorCount = numErrors;
    }

    /**
     * TODO
     * 
     * @return
     */
    public long getSuccessCount() {
        return successCount;
    }

    /**
     * TODO
     * 
     * @param successCount
     */
    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    /**
     * TODO
     * 
     * @return
     */
    public long getRepairCount() {
        return this.repairCount;
    }

    /**
     * TODO
     * 
     * @param repairCount
     */
    public void setRepairCount(long repairCount) {
        this.repairCount = repairCount;
    }

}