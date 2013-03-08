package org.fcrepo.services.fixity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.fcrepo.jaxb.responses.access.ObjectProfile;
import org.fcrepo.services.fixity.model.FixityCheckResult;

public interface FixityCheck {
	public FixityCheckResult check(String objectId) throws Exception;
}
