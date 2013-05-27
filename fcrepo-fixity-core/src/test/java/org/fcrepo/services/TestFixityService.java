package org.fcrepo.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.fcrepo.services.fixity.DatastreamChecksumCheck;
import org.fcrepo.services.fixity.FixityClient;
import org.fcrepo.services.fixity.FixityService;
import org.fcrepo.services.fixity.model.DatastreamFixity;
import org.fcrepo.services.fixity.model.DatastreamFixity.ResultType;
import org.fcrepo.services.fixity.model.FixityProblem;
import org.fcrepo.services.fixity.model.ObjectFixity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/context.xml")
public class TestFixityService {

	@Inject
	private FixityService service;
	@Inject
	private DatastreamChecksumCheck checksumCheck;

	private FixityClient client;

	@PostConstruct
	public void initMock() {
		/* inject the mock client to the datastream checksum check */
		this.client = mock(FixityClient.class);
		this.checksumCheck.setClient(this.client);

	}

	@Test
	public void testFixityService() throws Exception {
        /* Mock object profile */
        String ePid = "test:2";
        String eDsId = "testds:2";

        URI expectedChecksum = URI.create("urn:sha1:ABBA1");

        byte[] someData = new byte[16387];
        new Random().nextBytes(someData);

        /* setup the client mock */
        when(client.getDatastreamIds(ePid)).thenReturn(Arrays.asList(eDsId));

        DatastreamFixity fixity = new DatastreamFixity();
        fixity.setDatastreamId(eDsId);
        fixity.setTimestamp(new Date());
        when(client.getDatastreamFixity(ePid, eDsId)).thenReturn(fixity);

        /* tell the service to check a specific object */
        service.checkObject(ePid);

        /* let the daemon respond to the request give it 15 secs max*/
        long started = System.currentTimeMillis();
        do{
            if (System.currentTimeMillis() - started > 15000){
                throw new Exception("Timeout while waiting for JMS queue");
            }
            Thread.sleep(500);
        }while(service.getResults(ePid).size() == 0);

        verify(client).getDatastreamFixity(ePid, eDsId);

        List<ObjectFixity> results = service.getResults(ePid);
        assertEquals(1, results.size());
        assertEquals(ePid, results.get(0).getPid());
        assertEquals(1, results.get(0).getErrors().size());
        DatastreamFixity errorResult = results.get(0).getErrors().get(0);
        assertEquals(eDsId, errorResult.getDatastreamId());
        assertNotNull(errorResult.getTimestamp());
        int numCauses = errorResult.getProblems().size();
        assertEquals(2, numCauses);
        int repaired = 0;
        int errors = 0;
        for (FixityProblem p:errorResult.getProblems()){
            if (p.type == DatastreamFixity.ResultType.REPAIRED) {
                repaired++;
            } else errors++;
        }
        assertEquals(ResultType.ERROR, errorResult.getType());
        assertEquals(1, repaired);
        assertEquals(1, errors);
    }

	@Test
	public void testChecksumFixityServiceError() throws Exception {

		/* Mock object profile */
		String ePid = "test:2";
		String eDsId = "testds:2";

		URI expectedChecksum = URI.create("urn:sha1:ABBA1");

		byte[] someData = new byte[16387];
		new Random().nextBytes(someData);

		/* setup the client mock */
		when(client.getDatastreamIds(ePid)).thenReturn(Arrays.asList(eDsId));

		DatastreamFixity fixity = new DatastreamFixity();
		fixity.setDatastreamId(eDsId);
		fixity.setTimestamp(new Date());
		when(client.getDatastreamFixity(ePid, eDsId)).thenReturn(fixity);

		/* tell the service to check a specific object */
		service.checkObject(ePid);

		/* let the daemon respond to the request give it 15 secs max*/
		long started = System.currentTimeMillis();
		do{
			if (System.currentTimeMillis() - started > 15000){
				throw new Exception("Timeout while waiting for JMS queue");
			}
			Thread.sleep(500);
		}while(service.getResults(ePid).size() == 0);
		verify(client).getDatastreamFixity(ePid, eDsId);
		List<ObjectFixity> results = service.getResults(ePid);
		assertEquals(1, results.size());
		assertEquals(ePid, results.get(0).getPid());
		assertEquals(1, results.get(0).getErrors().size());
		DatastreamFixity errorResult = results.get(0).getErrors().get(0);
		assertEquals(eDsId, errorResult.getDatastreamId());
		assertNotNull(errorResult.getTimestamp());
		int numCauses = errorResult.getProblems().size();
		assertEquals(2, numCauses);
		int repaired = 0;
		int errors = 0;
		for (FixityProblem p:errorResult.getProblems()){
			if (p.type == DatastreamFixity.ResultType.REPAIRED) {
				repaired++;
			} else errors++;
		}
		assertEquals(ResultType.ERROR, errorResult.getType());
		assertEquals(1, repaired);
		assertEquals(1, errors);
	}

}
