package org.fcrepo.services.fixity;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.jaxb.responses.access.ObjectDatastreams;
import org.fcrepo.jaxb.responses.management.DatastreamProfile;
import org.fcrepo.services.fixity.model.FixityCheckResult;
import org.fcrepo.services.fixity.model.FixityError;

public class DatastreamChecksumCheck implements FixityCheck {
	private FedoraClient client;

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
	public FixityCheckResult check(String objectId) throws IOException, NoSuchAlgorithmException {
		final List<FixityError> errors = new ArrayList<FixityError>();
		final ObjectDatastreams datastreams = client.getObjectDatastreams(objectId);
		for (ObjectDatastreams.DatastreamElement dsElement : datastreams.datastreams) {
			final String dsId = dsElement.dsid;
			final DatastreamProfile ds = client.getDatastreamProfile(objectId, dsId);
			final String calculated = createChecksum(ds, ds.dsChecksumType);

			/* strip the uri parts e.g. "urn:sha1:" of the checksum */
			String checksum = ds.dsChecksum.toASCIIString();
			checksum = checksum.substring(checksum.lastIndexOf(':') + 1);

			/* check for equality */
			if (!calculated.equals(checksum)) {
				FixityError err = new FixityError();
				err.setTimestamp(new Date());
				err.setDatastreamId(dsId);
				err.setDetails("The calculated checksums of type " + ds.dsChecksumType + " for the datastream " + ds.dsID + " of the object "
						+ objectId + " does not match the saved value: [" + calculated + " != " + checksum + "]");
				errors.add(err);
			}
		}
		if (errors.isEmpty()) {
			return new FixityCheckResult(objectId, true, null, null);
		} else {
			return new FixityCheckResult(objectId, false, errors, null);
		}
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
