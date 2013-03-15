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

import org.fcrepo.client.FedoraClient;
import org.fcrepo.jaxb.responses.access.ObjectDatastreams;
import org.fcrepo.jaxb.responses.management.DatastreamProfile;
import org.fcrepo.services.fixity.model.DatastreamFixity;
import org.fcrepo.services.fixity.model.ObjectFixity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DatastreamChecksumCheck implements FixityCheck {
	
	@Inject
	private FedoraClient client;
	
	
	private static final Logger logger = LoggerFactory.getLogger(DatastreamChecksumCheck.class);

	public DatastreamChecksumCheck(FedoraClient client) {
		super();
		this.client = client;
	}

	public DatastreamChecksumCheck() {
		super();
	}

	public void setClient(FedoraClient client) {
		this.client = client;
	}

	@Override
	public ObjectFixity check(String objectId) throws IOException, NoSuchAlgorithmException {
		final List<DatastreamFixity> errors = new ArrayList<DatastreamFixity>();
		final List<DatastreamFixity> successes = new ArrayList<DatastreamFixity>();
		final ObjectDatastreams datastreams = client.getObjectDatastreams(objectId);
		for (ObjectDatastreams.DatastreamElement dsElement : datastreams.datastreams) {
			logger.debug("verifying checksum of object {} datastream {}", objectId, dsElement.dsid);
			final String dsId = dsElement.dsid;
			final org.fcrepo.jaxb.responses.management.DatastreamFixity ds = client.getDatastreamFixity(objectId, dsId);

			/* check for errors */
			boolean error = false;
			for (org.fcrepo.utils.FixityResult status: ds.statuses){
				if (!status.validChecksum) {
					error = errors.add(new DatastreamFixity(ds));
				}
                if (!status.validSize) {
					error = errors.add(new DatastreamFixity(ds));
                }
			}
			if (!error){
				successes.add(new DatastreamFixity(ds));
			}
		}
		return new ObjectFixity(objectId,new Date(), successes, errors);
	}

	private String createChecksum(DatastreamProfile ds, String dsChecksumType) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest;
		if (dsChecksumType.equalsIgnoreCase("sha-1")) {
			digest = MessageDigest.getInstance("SHA-1");
		} else if (dsChecksumType.equalsIgnoreCase("sha-256")) {
			digest = MessageDigest.getInstance("SHA-256");
		} else {
			throw new IllegalArgumentException("Unable to create checksums of type " + dsChecksumType);
		}
		InputStream src = client.getDatastreamContent(ds.pid, ds.dsID);
		if (src == null) {
			throw new IOException("Unable to open datastream " + ds.pid + " - " + ds.dsID);
		}
		byte[] buf = new byte[4096];
		int numRead;
		while ((numRead = src.read(buf)) > 0) {
			digest.update(buf, 0, numRead);
		}
		return new BigInteger(1, digest.digest()).toString(16);
	}

}
