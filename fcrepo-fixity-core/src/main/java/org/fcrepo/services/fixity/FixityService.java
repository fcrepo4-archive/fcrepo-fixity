package org.fcrepo.services.fixity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.fcrepo.services.db.DatabaseService;
import org.fcrepo.services.fixity.model.DailyStatistics;
import org.fcrepo.services.fixity.model.GeneralStatistics;
import org.fcrepo.services.fixity.model.ObjectFixity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * A fixity service implementation This is a standalone service that checks the fixity values of a fedora datastream.
 *
 * <p>
 * It is meant to be run via an {@link ExecutorService}:
 *
 * <pre>
 * ExecutorService exec = Executors.newSingleThreadExecutor();
 * Future&lt;Integer&gt; task = exec.submit(fixtures);
 * </pre>
 *
 * @author fasseg
 *
 */
@Named("fixityService")
public class FixityService {

	private static final Logger logger = LoggerFactory
			.getLogger(FixityService.class);

	@Inject
	private JmsTemplate fixityJmsTemplate;

	@Inject
	private FixityCheck checksumFixityCheck;

	@Inject
	private DatabaseService databaseService;

	@Inject
	private FixityClient client;

	private boolean shutdown = false;

	private final ObjectMapper jsonMapper = new ObjectMapper();

	private final ObjectWriter jsonWriter = jsonMapper
			.writerWithDefaultPrettyPrinter();

	public FixityService() {
		super();
	}

	public void processRequest(Message message) throws Exception {
		try {
			final String pid = message.getStringProperty("pid");
			/* pop an object id from the queue to check it */
			final List<ObjectFixity> results = runChecks(pid);
			logger.debug("gathered " + results.size() +
					" fixity check results for object '" + pid + "'");
			databaseService.addResults(results);
			for (final ObjectFixity result : results) {
				if (result.isSuccess()) {
					logger.debug("Success for object " + pid + ":\n" + this.jsonWriter.writeValueAsString(result));
				} else {
					logger.error("Errors when checking fixity of object " +
							pid + ":\n" +
							this.jsonWriter.writeValueAsString(result));
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private List<ObjectFixity> runChecks(final String pid) {
		final List<ObjectFixity> results =
				new ArrayList<ObjectFixity>();
		try {
			results.add(checksumFixityCheck.check(pid));
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return results;
	}

	/**
	 * shutdown the fixity service gracefully after it has finished work
	 */
	@PreDestroy
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
	public synchronized void checkObject(String... pids) throws IOException {
		if (pids.length == 1 && pids[0] == null){
			/* no pids a re given so a list of all objects is queued */
			List<String> allpids = client.getPids();
			if (allpids == null){
				logger.warn("No objects could be discovered in fedora. Queued nothing.");
				return;
			}
			pids = allpids.toArray(new String[allpids.size()]);
		}
		logger.debug(pids.length + " objects have been queued for fixity checks");
		for (final String pid : pids) {
			fixityJmsTemplate.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					javax.jms.Message msg = session.createMessage();
					msg.setStringProperty("pid", pid.trim());
					return msg;
				}
			});
		}
	}

	/**
	 * retrieve the list of results for a certain object from the result database
	 *
	 * @param pid
	 * @return
	 */
	public List<ObjectFixity> getResults(String pid) {
		return databaseService.getResults(pid);
	}

	public List<ObjectFixity> getResults(int offset, int length) {
		return databaseService.getResults(offset, length);
	}

	public GeneralStatistics getStatistics(){
		GeneralStatistics stats = new GeneralStatistics();
		stats.setNumObjects(databaseService.getObjectCount());
		stats.setErrorCount(databaseService.getErrorCount());
		stats.setSuccessCount(databaseService.getSuccessCount());
		stats.setRepairCount(databaseService.getRepairCount());
		return stats;
	}

	public List<DailyStatistics> getDailyStatistics() {
		return databaseService.getDailyStatistics();
	}

	public ObjectFixity getResult(long recordId) {
		return databaseService.getResult(recordId);
	}


}
