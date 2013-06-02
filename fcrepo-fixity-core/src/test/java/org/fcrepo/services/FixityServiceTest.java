/**
 *
 */

package org.fcrepo.services;


import java.lang.reflect.Field;
import java.util.Arrays;

import org.fcrepo.fixity.client.FedoraFixityClient;
import org.fcrepo.fixity.db.FixityDatabaseService;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.fcrepo.fixity.service.FixityService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * @author frank asseg
 *
 */
public class FixityServiceTest {

    private final FixityService fixityService = new FixityService();

    @Test
    public void testQueueFixityCheck() throws Exception {
        JmsTemplate mockJms = Mockito.mock(JmsTemplate.class);
        FixityServiceTest.setField(this.fixityService, "fixityJmsTemplate", mockJms);

        this.fixityService.queueFixityCheck(Arrays.asList("/objects/testob1", "/objects/testob2"));

        /* check that a JMS message is actually queued via the service's JmsTemplate */
        Mockito.verify(mockJms, Mockito.times(2)).send(Mockito.any(MessageCreator.class));
    }

    @Test
    public void testConsumeFixityMessage() throws Exception {
        /* setup the mocks for the unit test */
        FedoraFixityClient mockClient = Mockito.mock(FedoraFixityClient.class);
        FixityDatabaseService mockDb= Mockito.mock(FixityDatabaseService.class);
        FixityServiceTest.setField(this.fixityService, "databaseService", mockDb);
        FixityServiceTest.setField(this.fixityService, "fixityClient", mockClient);

        /* setup an appropriate response from the mock */
        String parentUri = "http://localhost:8080/objects/testobj1";
        Mockito.when(mockClient.retrieveUris(Mockito.any(String.class))).thenReturn(Arrays.asList(parentUri + "/ds1", parentUri + "/ds2"));

        this.fixityService.consumeFixityMessage(parentUri);

        Mockito.verify(mockClient).retrieveUris(parentUri);
        Mockito.verify(mockDb).addResult(Mockito.any(ObjectFixityResult.class));
    }

    private static void setField(FixityService service, String name, Object obj) throws NoSuchFieldException {
        Field f = FixityService.class.getDeclaredField(name);
        f.setAccessible(true);
        try {
            f.set(service, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
