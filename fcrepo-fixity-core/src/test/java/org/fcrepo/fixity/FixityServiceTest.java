/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.fixity;

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
 */
public class FixityServiceTest {

    private final FixityService fixityService = new FixityService();

    @Test
    public void testQueueFixityCheck() throws Exception {
        JmsTemplate mockJms = Mockito.mock(JmsTemplate.class);
        TestHelper.setField(this.fixityService, "fixityJmsTemplate", mockJms);

        this.fixityService.queueFixityChecks(Arrays.asList("/objects/testob1",
                "/objects/testob2"));

        /*
         * check that a JMS message is actually queued via the service's
         * JmsTemplate
         */
        Mockito.verify(mockJms, Mockito.times(2)).send(
                Mockito.any(MessageCreator.class));
    }

    @Test
    public void testConsumeFixityMessage() throws Exception {
        /* setup the mocks for the unit test */
        FedoraFixityClient mockClient = Mockito.mock(FedoraFixityClient.class);
        FixityDatabaseService mockDb =
                Mockito.mock(FixityDatabaseService.class);
        TestHelper.setField(this.fixityService, "databaseService", mockDb);
        TestHelper.setField(this.fixityService, "fixityClient", mockClient);

        /* setup an appropriate response from the mock */
        String parentUri = "http://localhost:8080/objects/testobj1";
        Mockito.when(
                mockClient.retrieveDatatstreamUris(Mockito.any(String.class)))
                .thenReturn(
                        Arrays.asList(parentUri + "/ds1", parentUri + "/ds2"));

        this.fixityService.consumeFixityMessage(parentUri);

        Mockito.verify(mockClient).retrieveDatatstreamUris(parentUri);
        Mockito.verify(mockDb).addResult(Mockito.any(ObjectFixityResult.class));
    }
}
