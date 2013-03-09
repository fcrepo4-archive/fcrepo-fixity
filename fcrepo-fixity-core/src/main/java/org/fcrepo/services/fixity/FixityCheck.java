package org.fcrepo.services.fixity;

import org.fcrepo.services.fixity.model.FixityCheckResult;

public interface FixityCheck {
	public FixityCheckResult check(String objectId) throws Exception;
}
