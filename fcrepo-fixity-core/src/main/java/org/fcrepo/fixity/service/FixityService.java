
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
import org.fcrepo.fixity.model.ObjectFixityResult.FixityResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for producing and consuming fixity messages form
 * the JMS Queue Message production is achieved via a User's call to
 * queueFixityCheck() Messages are consumed in the private consumeMessage()
 * method and corresponding fixity checks are run
 * 
 * @author frank asseg
 */
@Service("fixityService")
public class FixityService {

    public static final String FIXITY_NAMESPACE =
            "http://fcrepo.org/fcrepo4/fixity";

    @Value("#{systemProperties['org.fcrepo.fixity.fcrepo.url']}")
    private String fedoraFolderUri;

    @Autowired
    private JmsTemplate fixityJmsTemplate;

    @Autowired
    private FedoraFixityClient fixityClient;

    @Autowired
    private FixityDatabaseService databaseService;

    private static final Logger LOG = LoggerFactory
            .getLogger(FixityService.class);

    /**
     * @param fedoraFolderUri the Uri of the default parent folder in fedora 4
     */
    public void setFedoraFolderUri(String fedoraFolderUri) {
        this.fedoraFolderUri = fedoraFolderUri;
    }

    /**
     * TODO
     */
    @PostConstruct
    public void afterPropertiesSet() {
        if (fedoraFolderUri == null) {
            throw new IllegalStateException("fedoraFolderUri property must "
                    + "be set via spring configuration or the system property "
                    + "'org.fcrepo.fixity.fcrepo.url' to e.g. "
                    + "'http://{fedora-host}:{port}/rest/objects");
        }
    }

    /**
     * Queue a List of object URIs for fixity checks
     * 
     * @param uris the URIs of the objects to queue
     */
    public void queueFixityChecks(final List<String> uris) throws IOException {
        List<String> queueElements = uris;
        if (queueElements == null) {
            /* no pid was given, so queue all objects */
            queueElements = fixityClient.retrieveUris(this.fedoraFolderUri);
            if (queueElements == null) {
                LOG.warn(
                        "Fixity check was requested for all objects, "
                        + "but no objects could be discovered in the "
                        + "repository at {}", this.fedoraFolderUri);
                return;
            }
        } else {
            queueElements = uris;
        }
        for (String uri : queueElements) {
            this.queueFixityCheck(uri);
        }
    }

    /**
     * Queue a single object for a fixity check
     * 
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
     * Consume a fixity message published to the JMS queue and act on fixity
     * check requests
     * 
     * @param uri the text of the {@link Message} which is supposed to be a
     *        object uri
     */
    public void consumeFixityMessage(String uri) throws JMSException {
        LOG.debug("received fixity request for object {}", uri);
        try {
            /*
             * queue a fixity check and retrieve the new results from the
             * repository
             */
            final ObjectFixityResult result = this.checkObjectFixity(uri);
            /* save the new result to the database */
            if (result != null) {
                this.databaseService.addResult(result);
            }
        } catch (IOException e) {
            /* rethrow the exception as a Spring JMS Exception */
            LOG.error(e.getMessage(), e);
            throw new ListenerExecutionFailedException(e.getMessage(), e);
        }
    }

    /**
     * Request fixity check execution from the Fedora repository
     * 
     * @return the {@link ObjectFixityResult} or null if no result could be
     *         created
     */
    private ObjectFixityResult checkObjectFixity(final String uri)
        throws IOException {
        /*
         * fetch a list of the object's datastreams for getting their fixity
         * information
         */
        final List<String> datastreamUris =
                this.fixityClient.retrieveDatatstreamUris(uri);
        LOG.debug("discovered {} datastream URIs for Object {}", datastreamUris
                .size(), uri);

        if (datastreamUris.isEmpty()) {
            LOG.warn("Unable to generate fixity result for object without datastreams");
            return null;
        }
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
                LOG.error("Unable to handle result type of datasstream "
                        + "fixity result: {}", dr.getType());
            }
        }

        /* create the result object which gets persisted in the database */
        final ObjectFixityResult result = new ObjectFixityResult();
        result.setTimeStamp(new Date());
        result.setUri(uri);

        /*
         * order is important here , since errors override repaires and both
         * override successes
         */
        if (!successes.isEmpty()) {
            result.setSuccesses(successes);
            result.setState(FixityResult.SUCCESS);
        }
        if (!repairs.isEmpty()) {
            result.setRepairs(repairs);
            result.setState(FixityResult.REPAIRED);
        }
        if (!errors.isEmpty()) {
            result.setErrors(errors);
            result.setState(FixityResult.ERROR);
        }

        return result;
    }

}
