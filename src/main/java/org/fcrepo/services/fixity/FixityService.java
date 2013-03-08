package org.fcrepo.services.fixity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.fcrepo.services.db.DatabaseService;
import org.fcrepo.services.fixity.model.FixityCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

/**
 * A fixity service implementation This is a standalone service that checks the fixity values of a fedora datastream It
 * is meant to be run via an {@link ExecutorService}:
 * 
 * <pre>
 * ExecutorService exec = Executors.newSingleThreadExecutor();
 * Future&lt;Integer&gt; task = exec.submit(fixtures);
 * </pre>
 * 
 * @author fasseg
 * 
 */
public class FixityService implements Callable<Integer> {
	private static final Logger logger = LoggerFactory.getLogger(FixityService.class);

	private boolean shutdown = false;
	private final Queue<String> workQueue = new ConcurrentLinkedQueue<String>();
	private final ObjectMapper jsonMapper = new ObjectMapper();
	private final ObjectWriter jsonWriter = jsonMapper.defaultPrettyPrintingWriter();

	private Map<String, FixityCheck> fixityChecks;

	private DatabaseService databaseService;

	public FixityService(Map<String, FixityCheck> fixityChecks) {
		super();
		this.fixityChecks = fixityChecks;
	}

	public FixityService() {
		super();
	}

	public void setFixityChecks(Map<String, FixityCheck> fixityChecks) {
		this.fixityChecks = fixityChecks;
	}

	public void setDatabaseService(DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	/**
	 * The loop checking for fixity checks to be done which have been submitted via {@link #checkObject(String)} or
	 * {@link #checkObjects(Collection)}
	 */
	@Override
	public Integer call() throws Exception {
		try {
			while (!workQueue.isEmpty()) {
				/* pop an object id from the queue to check it */
				final String id = workQueue.poll();
				final List<FixityCheckResult> results = runChecks(id);
				logger.debug("gathered " + results.size() + " fixity check results for object '" + id + "'");
				databaseService.addResults(results);
				for (final FixityCheckResult result : results) {
					if (result.isSuccess()) {
						logger.debug("Success for object " + id);
					} else {
						logger.error("Errors when checking fixity of object " + id + ":\n" + this.jsonWriter.writeValueAsString(result));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return 1;
	}

	private List<FixityCheckResult> runChecks(final String pid) {
		final List<FixityCheckResult> results = new ArrayList<FixityCheckResult>();
		for (final FixityCheck check : fixityChecks.values()) {
			try {
				results.add(check.check(pid));
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		return results;
	}

	/**
	 * shutdown the fixity service gracefully after it has finished work
	 */
	public synchronized void shutdown() {
		logger.info("shutting down fixity service...");
		this.shutdown = true;
	}

	/**
	 * Request to check an object. The objectId will be queued for work by the {@link FixityService}
	 * 
	 * @param objectId
	 * @throws IOException
	 */
	public void checkObject(final String... pids) throws IOException {
		for (String pid : pids) {
			if (!this.workQueue.offer(pid)) {
				/* queue is full */
				throw new IOException("Unable to queue check for object " + pid + ". Most likely the queue is full");
			} else {
				logger.debug("added object " + pid + " to work queue");
			}
		}
	}

	/**
	 * retrieve the list of results for a certain object from the result database
	 * 
	 * @param pid
	 * @return
	 */
	public List<FixityCheckResult> getResults(String pid) {
		return databaseService.getResults(pid);
	}

}
