package org.fcrepo.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.fcrepo.services.fixity.model.FixityCheckResult;
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
		

		/* tell the service to check a specific object */
		service.checkObject(obj.pid);

		/* let the daemon respond to the request */
		Thread.sleep(300);
		
		/* check if there is a result in the database */
		assertTrue(service.getResults(obj.pid).size() == 1);
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

		/* tell the service to check a specific object */
		service.checkObject(obj.pid);

		/* let the daemon respond to the request */
		Thread.sleep(300);
		List<FixityCheckResult> results =service.getResults(obj.pid); 
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getPid().equals(obj.pid));
		assertTrue(results.get(0).getErrors().size() == 1);
		assertTrue(results.get(0).getErrors().get(0).getDatastreamId().equals(ds.dsID));
		assertTrue(results.get(0).getErrors().get(0).getTimestamp() != null);
	}

}
