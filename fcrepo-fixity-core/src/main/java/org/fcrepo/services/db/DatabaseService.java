package org.fcrepo.services.db;

import java.util.Collection;
import java.util.List;

import org.fcrepo.services.fixity.model.FixityResult;

public interface DatabaseService {
	void addResult(FixityResult res);

	List<FixityResult> getResults(String objectId);

	void addResults(Collection<FixityResult> results);
}
