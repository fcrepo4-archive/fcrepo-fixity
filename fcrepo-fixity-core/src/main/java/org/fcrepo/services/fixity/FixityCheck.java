package org.fcrepo.services.fixity;

import org.fcrepo.services.fixity.model.FixityResult;

public interface FixityCheck {
	public FixityResult check(String objectId) throws Exception;
}
