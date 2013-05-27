
package org.fcrepo.services.fixity;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.fcrepo.services.fixity.model.DatastreamChecksum;
import org.fcrepo.services.fixity.model.DatastreamFixity;
import org.fcrepo.services.fixity.model.DatastreamFixity.ResultType;
import org.fcrepo.services.fixity.model.ObjectFixity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DatastreamChecksumCheck implements FixityCheck {

    private static final Logger logger = LoggerFactory
            .getLogger(DatastreamChecksumCheck.class);

    @Inject
    private FixityClient client;

    public void setClient(FixityClient client) {
        this.client = client;
    }

    @Override
    public ObjectFixity check(String objectId) throws IOException,
            NoSuchAlgorithmException {
        final List<DatastreamFixity> errors = new ArrayList<DatastreamFixity>();
        final List<DatastreamFixity> successes =
                new ArrayList<DatastreamFixity>();

        final List<String> datastreamIds = client.getDatastreamIds(objectId);
        if (datastreamIds == null || datastreamIds.size() == 0) {
            logger.warn("There are no datastreams available for pid: " +
                    objectId);
        } else {
            for (final String dsId : datastreamIds) {
                logger.debug("verifying checksum of object {} datastream {}",
                        objectId, dsId);
                final DatastreamFixity fixity =
                        client.getDatastreamFixity(objectId, dsId);
                if (fixity.getType() != ResultType.SUCCESS) {
                    errors.add(fixity);
                } else {
                    successes.add(fixity);
                }
            }
        }
        return new ObjectFixity(objectId, new Date(), successes, errors);
    }

    private String createChecksum(DatastreamChecksum dsCheck,
            String dsChecksumType) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest;
        if (dsChecksumType.equalsIgnoreCase("sha-1")) {
            digest = MessageDigest.getInstance("SHA-1");
        } else if (dsChecksumType.equalsIgnoreCase("sha-256")) {
            digest = MessageDigest.getInstance("SHA-256");
        } else {
            throw new IllegalArgumentException(
                    "Unable to create checksums of type " + dsChecksumType);
        }
        InputStream src =
                client.getDatastreamContent(dsCheck.getPid(), dsCheck.getDsId());
        if (src == null) {
            throw new IOException("Unable to open datastream " +
                    dsCheck.getPid() + " - " + dsCheck.getDsId());
        }
        byte[] buf = new byte[4096];
        int numRead;
        while ((numRead = src.read(buf)) > 0) {
            digest.update(buf, 0, numRead);
        }
        return new BigInteger(1, digest.digest()).toString(16);
    }

}
