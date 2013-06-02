/**
 *
 */

package org.fcrepo.services;


import java.lang.reflect.Field;
import java.util.Arrays;

import org.fcrepo.fixity.client.FedoraFixityClient;
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
        FixityServiceTest.setField(fixityService, "fixityJmsTemplate", mockJms);

        fixityService.queueFixityCheck(Arrays.asList("/objects/testob1",
                "/objects/testob2"));

        /* check that a JMS message is actually queued via the service's JmsTemplate */
        Mockito.verify(mockJms, Mockito.times(2)).send(Mockito.any(MessageCreator.class));
    }

    @Test
    public void testConsumeFixityMessage() throws Exception {
        FedoraFixityClient mockClient = Mockito.mock(FedoraFixityClient.class);
        String parentUri = "http://localhost:8080/objects/testobj1";

        FixityServiceTest.setField(fixityService, "fixityClient", mockClient);
        Mockito.when(mockClient.retrieveUris(Mockito.any(String.class))).thenReturn(Arrays.asList(parentUri + "/ds1", parentUri + "/ds2"));

        fixityService.consumeFixityMessage(parentUri);

        Mockito.verify(mockClient).retrieveUris(parentUri);
    }

    private static void
            setField(FixityService service, String name, Object obj)
                    throws Exception {
        Field f = FixityService.class.getDeclaredField(name);
        f.setAccessible(true);
        try {
            f.set(service, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
