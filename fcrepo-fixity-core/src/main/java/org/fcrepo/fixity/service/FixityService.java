/**
 *
 */

package org.fcrepo.fixity.service;

import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.fcrepo.fixity.client.FedoraFixityClient;
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
@Service
public class FixityService {

    private final String defaultParentUri =
            "http://localhost:8080/rest/objects";

    @Autowired
    private JmsTemplate fixityJmsTemplate;

    @Autowired
    private FedoraFixityClient fixityClient;


    private static final Logger logger = LoggerFactory.getLogger(FixityService.class);

    /**
     * Queue a List of object URIs for fixity checks
     * @param uri the uri of the object to queue
     */
    public void queueFixityCheck(List<String> uris) throws IOException {
        if (uris == null) {
            /* no pid was given, so queue all objects */
            uris = fixityClient.retrieveUris(this.defaultParentUri);
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
            this.checkObjectFixity(uri);
        } catch (IOException e) {
            /* rethrow the exception as a Spring JMS Exception */
            logger.error(e.getMessage(), e);
            throw new ListenerExecutionFailedException(e.getMessage(), e);
        }
    }

    /**
     * Request fixity check execution from the Fedora repository
     */
    private void checkObjectFixity(String uri) throws IOException{
        /* fetch a list of the object's datastreams for getting their fixity information */
        List<String> datastreamUris = this.fixityClient.retrieveUris(uri);
        logger.debug("discovered {} datastream URIs for Object {}", datastreamUris.size(), uri);

        /* for each of the child datastreams queue a fixity check by calling the corresponding Fedora endpoint */
        this.fixityClient.requestFixityCheck(uri);
    }

}
