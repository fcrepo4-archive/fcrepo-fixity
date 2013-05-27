/**
 * 
 */
package org.fcrepo.services.fixity.model;

public class DatastreamChecksum {

    private final String pid;

    private final String dsId;

    private final String checksumType;

    private final String checksum;

    public DatastreamChecksum(String pid, String dsId, String checksumType,
            String checksum) {
        super();
        this.pid = pid;
        this.dsId = dsId;
        this.checksumType = checksumType;
        this.checksum = checksum;
    }

    public String getPid() {
        return pid;
    }

    public String getDsId() {
        return dsId;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public String getChecksum() {
        return checksum;
    }

}