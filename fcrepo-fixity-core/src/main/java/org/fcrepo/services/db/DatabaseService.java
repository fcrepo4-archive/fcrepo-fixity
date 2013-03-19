package org.fcrepo.services.db;

import java.util.Collection;
import java.util.List;

import org.fcrepo.services.fixity.model.DailyStatistics;
import org.fcrepo.services.fixity.model.ObjectFixity;
import org.fcrepo.services.fixity.model.GeneralStatistics;

public interface DatabaseService {
	void addResult(ObjectFixity res);

	List<ObjectFixity> getResults(String objectId);

	void addResults(Collection<ObjectFixity> results);

	List<ObjectFixity> getResults(int offset, int length);

	long getResultCount();

	long getErrorCount();

	long getSuccessCount();
	
	long getRepairCount();
	
	long getObjectCount();

	void addStat(int successCount, int errorCount, int repairCount);
	
	List<DailyStatistics> getDailyStatistics();

	ObjectFixity getResult(long recordId);
}