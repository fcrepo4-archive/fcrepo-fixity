package org.fcrepo.services.fixity;

import org.fcrepo.services.fixity.model.ObjectFixity;

public interface FixityCheck {
	public ObjectFixity check(String objectId) throws Exception;
}
