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

package org.fcrepo.fixity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.fcrepo.fixity.db.FixityDatabaseService;
import org.fcrepo.fixity.model.DailyStatistics;
import org.fcrepo.fixity.model.DatastreamFixityError;
import org.fcrepo.fixity.model.DatastreamFixityRepaired;
import org.fcrepo.fixity.model.DatastreamFixitySuccess;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author frank asseg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/integration-tests/test-container.xml",
        "/integration-tests/fixity.xml"})
public class FixityDatabaseServiceIT {

    @Autowired
    private FixityDatabaseService service;

    @Before
    public void setup() {
        this.service.deleteAllResults();
    }

    @Test
    public void testAddResult() throws Exception {
        /* ceate a ObjectFixityResult to persist in the database */
        String uri = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result = new ObjectFixityResult();
        DatastreamFixitySuccess success =
                new DatastreamFixitySuccess(uri + "/testds1");
        result.setUri(uri);
        result.setSuccesses(Arrays.asList(success));
        result.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResult(result);
    }

    @Test
    public void testAddAndRetrieveResult() throws Exception {
        /* create a ObjectFixityResult to persist in the database */
        String uri = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result = new ObjectFixityResult();
        DatastreamFixitySuccess success =
                new DatastreamFixitySuccess(uri + "/testds1");
        result.setUri(uri);
        result.setSuccesses(Arrays.asList(success));
        result.setTimeStamp(new Date());

        /* persist the object by calling the service method */
        this.service.addResult(result);

        /* fetch the result via the object uri */
        List<ObjectFixityResult> fetched = this.service.getResults(uri);

        assertEquals(1, fetched.size());
        assertEquals(uri, fetched.get(0).getUri());
    }

    @Test
    public void testAddResults() throws Exception {
        /* ceate two ObjectFixityResults to persist in the database */
        String uri_1 = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result_1 = new ObjectFixityResult();
        DatastreamFixitySuccess success_1 =
                new DatastreamFixitySuccess(uri_1 + "/testds1");
        result_1.setUri(uri_1);
        result_1.setSuccesses(Arrays.asList(success_1));
        result_1.setTimeStamp(new Date());

        String uri_2 = "http://localhost:8080/rest/objects/testobj2";
        ObjectFixityResult result_2 = new ObjectFixityResult();
        DatastreamFixitySuccess success_2 =
                new DatastreamFixitySuccess(uri_2 + "/testds1");
        result_2.setUri(uri_2);
        result_2.setSuccesses(Arrays.asList(success_2));
        result_2.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResults(Arrays.asList(result_1, result_2));

    }

    @Test
    public void testAddAndRetrieveResults() throws Exception {
        /* ceate two ObjectFixityResults to persist in the database */
        String uri_1 = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result_1 = new ObjectFixityResult();
        DatastreamFixitySuccess success_1 =
                new DatastreamFixitySuccess(uri_1 + "/testds1");
        result_1.setUri(uri_1);
        result_1.setSuccesses(Arrays.asList(success_1));
        result_1.setTimeStamp(new Date());

        String uri_2 = "http://localhost:8080/rest/objects/testobj2";
        ObjectFixityResult result_2 = new ObjectFixityResult();
        DatastreamFixitySuccess success_2 =
                new DatastreamFixitySuccess(uri_2 + "/testds1");
        result_2.setUri(uri_2);
        result_2.setSuccesses(Arrays.asList(success_2));
        result_2.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResults(Arrays.asList(result_1, result_2));

        /* retrieve the objects from the service */
        List<ObjectFixityResult> fetched = this.service.getResults(0, 20);
        assertTrue(fetched.size() == 2);
        assertTrue(fetched.get(0).getSuccesses().size() == 1);
        assertTrue(fetched.get(1).getSuccesses().size() == 1);
    }

    @Test
    public void testRetrieveCount() throws Exception {
        /* ceate two ObjectFixityResults to persist in the database */
        String uri = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result = new ObjectFixityResult();
        DatastreamFixitySuccess success =
                new DatastreamFixitySuccess(uri + "/testds1");
        DatastreamFixityError error =
                new DatastreamFixityError(uri + "/testds1");
        DatastreamFixityRepaired repair =
                new DatastreamFixityRepaired(uri + "/testds1");
        result.setUri(uri);
        result.setSuccesses(Arrays.asList(success));
        result.setErrors(Arrays.asList(error));
        result.setRepairs(Arrays.asList(repair));
        result.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResults(Arrays.asList(result));

        long errorCount = this.service.getErrorCount();
        long successCount = this.service.getSuccessCount();
        long repairCount = this.service.getRepairCount();
        long objectCount = this.service.getObjectCount();

        assertEquals(1, errorCount);
        assertEquals(1, successCount);
        assertEquals(1, repairCount);
        assertEquals(1, objectCount);
    }

    @Test
    public void testRetrieveStatistics() throws Exception {
        /* ceate two ObjectFixityResults to persist in the database */
        String uri = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result = new ObjectFixityResult();
        DatastreamFixitySuccess success =
                new DatastreamFixitySuccess(uri + "/testds1");
        DatastreamFixityError error =
                new DatastreamFixityError(uri + "/testds1");
        DatastreamFixityRepaired repair =
                new DatastreamFixityRepaired(uri + "/testds1");
        result.setUri(uri);
        result.setSuccesses(Arrays.asList(success));
        result.setErrors(Arrays.asList(error));
        result.setRepairs(Arrays.asList(repair));
        result.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResults(Arrays.asList(result));

        List<DailyStatistics> stats = this.service.getDailyStatistics();
        assertEquals(1, stats.size());
        assertEquals(1, stats.get(0).getErrorCount());
        assertEquals(1, stats.get(0).getSuccessCount());
        assertEquals(1, stats.get(0).getRepairCount());
    }

    @Test
    public void testRetrieveEmptyStatistics() throws Exception {
        /* ceate two ObjectFixityResults to persist in the database */
        List<DailyStatistics> stats = this.service.getDailyStatistics();
        assertNotNull(stats);
        assertEquals(0, stats.size());
    }
}
