/**
 *
 */

package org.fcrepo.fixity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.fcrepo.fixity.db.FixityDatabaseService;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.FixityStatistics;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.fcrepo.fixity.model.ObjectFixityResult.FixityResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author frank asseg
 *
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
        DatastreamFixityResult success =
                new DatastreamFixityResult(uri + "/testds1",
                        FixityResult.SUCCESS);
        result.setUri(uri);
        result.setSuccesses(Arrays.asList(success));
        result.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResult(result);
    }

    @Test
    public void testAddAndRetrieveResult() throws Exception {
        /* ceate a ObjectFixityResult to persist in the database */
        String uri = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result = new ObjectFixityResult();
        DatastreamFixityResult success =
                new DatastreamFixityResult(uri + "/testds1",
                        FixityResult.SUCCESS);
        result.setUri(uri);
        result.setSuccesses(Arrays.asList(success));
        result.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResult(result);

        /* fetch the result via the object uri */
        List<ObjectFixityResult> fetched = this.service.getResults(uri);

        assertEquals(1,fetched.size());
        assertEquals(uri, fetched.get(0).getUri());
    }

    @Test
    public void testAddResults() throws Exception {
        /* ceate two ObjectFixityResults to persist in the database */
        String uri_1 = "http://localhost:8080/rest/objects/testobj1";
        ObjectFixityResult result_1 = new ObjectFixityResult();
        DatastreamFixityResult success_1 =
                new DatastreamFixityResult(uri_1 + "/testds1",
                        FixityResult.SUCCESS);
        result_1.setUri(uri_1);
        result_1.setSuccesses(Arrays.asList(success_1));
        result_1.setTimeStamp(new Date());

        String uri_2 = "http://localhost:8080/rest/objects/testobj2";
        ObjectFixityResult result_2 = new ObjectFixityResult();
        DatastreamFixityResult success_2 =
                new DatastreamFixityResult(uri_2 + "/testds1",
                        FixityResult.SUCCESS);
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
        DatastreamFixityResult success_1 =
                new DatastreamFixityResult(uri_1 + "/testds1",
                        FixityResult.SUCCESS);
        result_1.setUri(uri_1);
        result_1.setSuccesses(Arrays.asList(success_1));
        result_1.setTimeStamp(new Date());

        String uri_2 = "http://localhost:8080/rest/objects/testobj2";
        ObjectFixityResult result_2 = new ObjectFixityResult();
        DatastreamFixityResult success_2 =
                new DatastreamFixityResult(uri_2 + "/testds1",
                        FixityResult.SUCCESS);
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
        DatastreamFixityResult success =
                new DatastreamFixityResult(uri + "/testds1",
                        FixityResult.SUCCESS);
        DatastreamFixityResult error =
                new DatastreamFixityResult(uri + "/testds1", FixityResult.ERROR);
        DatastreamFixityResult repair =
                new DatastreamFixityResult(uri + "/testds1",
                        FixityResult.REPAIRED);
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
        DatastreamFixityResult success =
                new DatastreamFixityResult(uri + "/testds1",
                        FixityResult.SUCCESS);
        DatastreamFixityResult error =
                new DatastreamFixityResult(uri + "/testds1", FixityResult.ERROR);
        DatastreamFixityResult repair =
                new DatastreamFixityResult(uri + "/testds1",
                        FixityResult.REPAIRED);
        result.setUri(uri);
        result.setSuccesses(Arrays.asList(success));
        result.setErrors(Arrays.asList(error));
        result.setRepairs(Arrays.asList(repair));
        result.setTimeStamp(new Date());

        /* persists the object by calling the service method */
        this.service.addResults(Arrays.asList(result));

        List<FixityStatistics> stats = this.service.getFixityStatistics();
        assertEquals(1, stats.size());
        assertEquals(1, stats.get(0).getErrorCount());
        assertEquals(1, stats.get(0).getSuccessCount());
        assertEquals(1, stats.get(0).getRepairCount());
    }
}
