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
import org.fcrepo.services.fixity.model.DatastreamFixityResult;
import org.fcrepo.services.fixity.model.FixityResult;
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
	public FixityResult check(String objectId) throws IOException, NoSuchAlgorithmException {
		final List<DatastreamFixityResult> errors = new ArrayList<DatastreamFixityResult>();
		final List<DatastreamFixityResult> successes = new ArrayList<DatastreamFixityResult>();
		final ObjectDatastreams datastreams = client.getObjectDatastreams(objectId);
		for (ObjectDatastreams.DatastreamElement dsElement : datastreams.datastreams) {
			logger.debug("verifying checksum of object "+ objectId +" datastream " + dsElement.dsid);
			final String dsId = dsElement.dsid;
			final DatastreamProfile ds = client.getDatastreamProfile(objectId, dsId);
			final String calculated = createChecksum(ds, ds.dsChecksumType);

			/* strip the uri parts e.g. "urn:sha1:" of the checksum */
			String checksum = ds.dsChecksum.toASCIIString();
			checksum = checksum.substring(checksum.lastIndexOf(':') + 1);

			/* check for equality */
			if (!calculated.equals(checksum)) {
				String details = "The calculated checksums of type " + ds.dsChecksumType + " for the datastream " + ds.dsID
						+ " of the object "
						+ objectId + " does not match the saved value: [" + calculated + " != " + ds.dsChecksum.toASCIIString() + "]";
				DatastreamFixityResult err = DatastreamFixityResult.newError(dsId, new Date(), details);
				errors.add(err);
			} else {
				String details = "Success for Checksum: " + ds.dsChecksum.toASCIIString();
				DatastreamFixityResult success = DatastreamFixityResult.newSuccess(dsId, new Date(), details);
				successes.add(success);
			}
		}
		return new FixityResult(objectId,new Date(), successes, errors);
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
