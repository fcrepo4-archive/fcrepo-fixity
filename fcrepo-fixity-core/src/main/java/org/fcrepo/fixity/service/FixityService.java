/**
 *
 */

package org.fcrepo.fixity.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.fcrepo.fixity.client.FedoraFixityClient;
import org.fcrepo.fixity.db.FixityDatabaseService;
import org.fcrepo.fixity.model.DatastreamFixityError;
import org.fcrepo.fixity.model.DatastreamFixityRepaired;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.DatastreamFixitySuccess;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for producing and consuming fixity messages form the JMS Queue
 * Message production is achieved via a User's call to queueFixityCheck()
 * Messages are consumed in the private consumeMessage() method and corresponding fixity checks are run
 * @author frank asseg
 *
 */
@Service("fixityService")
public class FixityService {

    public static final String NAMESPACE_FIXITY =
            "http://fcrepo.org/fcrepo4/fixity";

    private String fedoraFolderUri;

    @Autowired
    private JmsTemplate fixityJmsTemplate;

    @Autowired
    private FedoraFixityClient fixityClient;

    @Autowired
    private FixityDatabaseService databaseService;

    private static final Logger logger = LoggerFactory
            .getLogger(FixityService.class);

    /**
     * @param fedoraFolderUri the Uri of the default parent folder in fedora 4
     */
    public void setFedoraFolderUri(String fedoraFolderUri) {
        this.fedoraFolderUri = fedoraFolderUri;
    }

    @PostConstruct
    private void afterPropertiesSet() throws IllegalStateException {
        if (fedoraFolderUri == null) {
            throw new IllegalStateException(
                    "fedoraFolderUri property has to be set via spring configuration");
        }
    }

    /**
     * Queue a List of object URIs for fixity checks
     * @param uri the uri of the object to queue
     */
    public void queueFixityChecks(List<String> uris) throws IOException {
        if (uris == null) {
            /* no pid was given, so queue all objects */
            uris = fixityClient.retrieveUris(this.fedoraFolderUri);
            if (uris == null) {
                logger.warn("Fixity check was requested for all objects, but no objects could be discovered in the repository at " +
                        this.fedoraFolderUri);
                return;
            }
        }
        for (String uri : uris) {
            this.queueFixityCheck(uri);
        }
    }

    /**
     * Queue a single object for a fixity check
     * @param uri the Uri of the Object to queue
     */
    public void queueFixityCheck(final String uri) {

        /* send a JMS message to the fixity queue for each object */
        this.fixityJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                /* create a message containing the object uri */
                return session.createTextMessage(uri);
            }
        });
    }

    /**
     * Consume a fixity message published to the JMS queue and act on fixity check requests
     * @param uri the text of the {@link Message} which is supposed to be a object uri
     */
    public void consumeFixityMessage(String uri) throws JMSException {
        logger.debug("received fixity request for object {}", uri);
        try {
            /*
             * queue a fixity check and retrieve the new results from the
             * repository
             */
            final ObjectFixityResult result = this.checkObjectFixity(uri);
            /* save the new result to the database */
            this.databaseService.addResult(result);
        } catch (IOException e) {
            /* rethrow the exception as a Spring JMS Exception */
            logger.error(e.getMessage(), e);
            throw new ListenerExecutionFailedException(e.getMessage(), e);
        }
    }

    /**
     * Request fixity check execution from the Fedora repository
     */
    private ObjectFixityResult checkObjectFixity(final String uri)
            throws IOException {
        /*
         * fetch a list of the object's datastreams for getting their fixity
         * information
         */
        final List<String> datastreamUris = this.fixityClient.retrieveUris(uri);
        logger.debug("discovered {} datastream URIs for Object {}",
                datastreamUris.size(), uri);

        /*
         * for each of the child datastreams queue a fixity check by calling the
         * corresponding Fedora endpoint
         */
        final List<DatastreamFixityResult> datastreamResults =
                this.fixityClient.requestFixityChecks(datastreamUris);
        final List<DatastreamFixityError> errors = new ArrayList<>();
        final List<DatastreamFixityRepaired> repairs = new ArrayList<>();
        final List<DatastreamFixitySuccess> successes = new ArrayList<>();
        for (DatastreamFixityResult dr : datastreamResults) {
            if (dr instanceof DatastreamFixitySuccess) {
                successes.add((DatastreamFixitySuccess) dr);
            } else if (dr instanceof DatastreamFixityRepaired) {
                repairs.add((DatastreamFixityRepaired) dr);
            } else if (dr instanceof DatastreamFixityError) {
                errors.add((DatastreamFixityError) dr);
            } else {
                logger.error(
                        "Unable to handle result type of datasstream fixity result: {}",
                        dr.getType());
            }
        }

        /* create the result object which gets persisted in the database */
        final ObjectFixityResult result = new ObjectFixityResult();
        result.setTimeStamp(new Date());
        result.setUri(uri);
        if (!errors.isEmpty()) {
            result.setErrors(errors);
        }
        if (!repairs.isEmpty()) {
            result.setRepairs(repairs);
        }
        if (!successes.isEmpty()) {
            result.setSuccesses(successes);
        }

        return result;
    }

}
