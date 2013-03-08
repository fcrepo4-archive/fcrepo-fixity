package org.fcrepo.services.db;

import java.util.Collection;
import java.util.List;

import org.fcrepo.services.fixity.model.FixityCheckResult;

public interface DatabaseService {
	void addResult(FixityCheckResult res);

	List<FixityCheckResult> getResults(String objectId);

	void addResults(Collection<FixityCheckResult> results);
}
