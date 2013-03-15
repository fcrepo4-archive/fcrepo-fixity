package org.fcrepo.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.jaxb.responses.access.ObjectDatastreams;
import org.fcrepo.jaxb.responses.access.ObjectDatastreams.DatastreamElement;
import org.fcrepo.jaxb.responses.access.ObjectProfile;
import org.fcrepo.jaxb.responses.management.DatastreamProfile;
import org.fcrepo.services.fixity.DatastreamChecksumCheck;
import org.fcrepo.services.fixity.FixityService;
import org.fcrepo.services.fixity.model.ObjectFixity;
import org.fcrepo.utils.FixityResult;
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
	private FedoraClient client;

	@PostConstruct
	public void initMock() {
		/* inject the mock client to the datastream checksum check */
		this.client = mock(FedoraClient.class);
		this.checksumCheck.setClient(this.client);
		
	}

	@Test
	public void testFixityService() throws Exception {
		/* Mock object profile */
		ObjectProfile obj = new ObjectProfile();
		obj.pid = "test:1";

		/* Mock datastreams */
		ObjectDatastreams datastreams = new ObjectDatastreams();
		DatastreamElement dsElement = new DatastreamElement();
		dsElement.dsid = "testds:1";
		datastreams.datastreams = new HashSet<DatastreamElement>();
		datastreams.datastreams.add(dsElement);

		/* mock datastream profile */
		DatastreamProfile ds = new DatastreamProfile();
		ds.dsID = dsElement.dsid;
		ds.dsChecksumType = "SHA-256";
		ds.pid = obj.pid;

		/* setup some data which gets checksummed */
		byte[] someData = new byte[16387];
		new Random().nextBytes(someData);
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(someData);
		ds.dsChecksum = URI.create("urn:sha-256:" + new BigInteger(1, digest.digest()).toString(16));

		/* setup the client mock */
		when(client.getObjectDatastreams(obj.pid)).thenReturn(datastreams);
		when(client.getDatastreamProfile(obj.pid, dsElement.dsid)).thenReturn(ds);
		when(client.getDatastreamContent(obj.pid, dsElement.dsid)).thenReturn(new ByteArrayInputStream(someData));
		org.fcrepo.jaxb.responses.management.DatastreamFixity fixity = new org.fcrepo.jaxb.responses.management.DatastreamFixity();
		fixity.dsId = dsElement.dsid;
		fixity.objectId = obj.pid;
		fixity.timestamp = new java.util.Date();
		fixity.statuses = new java.util.ArrayList<FixityResult>();
		FixityResult success = new FixityResult(someData.length, ds.dsChecksum);
		success.dsSize = someData.length;
		success.dsChecksum = ds.dsChecksum;
		success.validChecksum = true;
		success.validSize = true;
		fixity.statuses.add(success);
		
		when(client.getDatastreamFixity(obj.pid, dsElement.dsid)).thenReturn(fixity);

		/* tell the service to check a specific object */
		service.checkObject(obj.pid);

		/* let the daemon respond to the request give it 15 secs max*/
		long started = System.currentTimeMillis();
		do{
			if (System.currentTimeMillis() - started > 15000){
				throw new Exception("Timeout while waiting for JMS queue");
			}
			Thread.sleep(500);
		}while(service.getResults(obj.pid).size() == 0);
		
		/* check if there is a result in the database */
		List<ObjectFixity> results =service.getResults(obj.pid); 
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getErrors().size() == 0);
		assertTrue(results.get(0).getSuccesses().size() == 1);
	}

	@Test
	public void testChecksumFixityServiceError() throws Exception {

		/* Mock object profile */
		ObjectProfile obj = new ObjectProfile();
		obj.pid = "test:2";

		/* Mock datastreams */
		ObjectDatastreams datastreams = new ObjectDatastreams();
		DatastreamElement dsElement = new DatastreamElement();
		dsElement.dsid = "testds:2";
		datastreams.datastreams = new HashSet<DatastreamElement>();
		datastreams.datastreams.add(dsElement);

		/* mock datastream profile */
		DatastreamProfile ds = new DatastreamProfile();
		ds.dsID = dsElement.dsid;
		ds.dsChecksum = URI.create("urn:sha1:" + "ABBA1");
		ds.dsChecksumType = "SHA-1";
		ds.pid = obj.pid;

		byte[] someData = new byte[16387];
		new Random().nextBytes(someData);

		/* setup the client mock */
		when(client.getObjectDatastreams(obj.pid)).thenReturn(datastreams);
		when(client.getDatastreamProfile(obj.pid, dsElement.dsid)).thenReturn(ds);
		when(client.getDatastreamContent(obj.pid, dsElement.dsid)).thenReturn(new ByteArrayInputStream(someData));

		org.fcrepo.jaxb.responses.management.DatastreamFixity fixity = new org.fcrepo.jaxb.responses.management.DatastreamFixity();
		fixity.dsId = dsElement.dsid;
		fixity.objectId = obj.pid;
		fixity.timestamp = new java.util.Date();
		fixity.statuses = new java.util.ArrayList<FixityResult>();
		FixityResult error = new FixityResult(someData.length - 2, ds.dsChecksum);
		error.validChecksum = true;
		error.validSize = false;
		error.dsSize = someData.length;
		error.dsChecksum = ds.dsChecksum;
		fixity.statuses.add(error);
		
		
		when(client.getDatastreamFixity(obj.pid, dsElement.dsid)).thenReturn(fixity);

		/* tell the service to check a specific object */
		service.checkObject(obj.pid);
        
		/* let the daemon respond to the request give it 15 secs max*/
		long started = System.currentTimeMillis();
		do{
			if (System.currentTimeMillis() - started > 15000){
				throw new Exception("Timeout while waiting for JMS queue");
			}
			Thread.sleep(500);
		}while(service.getResults(obj.pid).size() == 0);
		verify(client).getDatastreamFixity(obj.pid, dsElement.dsid);
		List<ObjectFixity> results =service.getResults(obj.pid); 
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getPid().equals(obj.pid));
		System.out.println(results.get(0).getErrors().size());
		assertTrue(results.get(0).getErrors().size() == 1);
		assertTrue(results.get(0).getErrors().get(0).getDatastreamId().equals(ds.dsID));
		assertTrue(results.get(0).getErrors().get(0).getTimestamp() != null);
	}

}
