package org.fcrepo.services.db;

import java.util.Collection;
import java.util.List;

import org.fcrepo.services.fixity.model.DailyStatistics;
import org.fcrepo.services.fixity.model.FixityResult;
import org.fcrepo.services.fixity.model.GeneralStatistics;

public interface DatabaseService {
	void addResult(FixityResult res);

	List<FixityResult> getResults(String objectId);

	void addResults(Collection<FixityResult> results);

	List<FixityResult> getResults(int offset, int length);

	long getResultCount();

	long getErrorCount();

	long getSuccessCount();
	
	void addStat(int successCount, int errorCount);
	
	List<DailyStatistics> getDailyStatistics();

	FixityResult getResult(long recordId);
}