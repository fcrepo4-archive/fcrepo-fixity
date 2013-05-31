/**
 *
 */

package org.fcrepo.fixity.model;

import java.util.List;

/**
 * @author frank asseg
 *
 */
public class ObjectFixityResult {

    public static enum FixityResult{
        SUCCESS,ERROR,REPAIRED;
    }

    private long resultId;

    private String uri;

    private List<DatastreamFixityResult> successes;

    private List<DatastreamFixityResult> errors;

    private List<DatastreamFixityResult> repairs;


    public long getResultId() {
        return resultId;
    }


    public void setResultId(long resultId) {
        this.resultId = resultId;
    }


    public String getUri() {
        return uri;
    }


    public void setUri(String uri) {
        this.uri = uri;
    }


    public List<DatastreamFixityResult> getSuccesses() {
        return successes;
    }


    public void setSuccesses(List<DatastreamFixityResult> successes) {
        this.successes = successes;
    }


    public List<DatastreamFixityResult> getErrors() {
        return errors;
    }


    public void setErrors(List<DatastreamFixityResult> errors) {
        this.errors = errors;
    }


    public List<DatastreamFixityResult> getRepairs() {
        return repairs;
    }


    public void setRepairs(List<DatastreamFixityResult> repairs) {
        this.repairs = repairs;
    }
}
