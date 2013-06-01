/**
 *
 */
package org.fcrepo.fixity.integration.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.fcrepo.fixity.client.FedoraFixityClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author frank asseg
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-tests/test-container.xml")
public class FedoraFixityClientIT{

    private final FedoraFixityClient fixityClient = new FedoraFixityClient();

    private static final String serverAddress = "http://localhost:8080/";

    private final HttpClient httpClient = new DefaultHttpClient();

    @Test
    public void testRetrievePids() throws Exception {
        /* create a new item in the objects folder */
        HttpPost postObject = new HttpPost(serverAddress + "rest/objects/fcr:new");
        HttpResponse resp = new DefaultHttpClient().execute(postObject);
        assertEquals("Unable to create Object",201, resp.getStatusLine().getStatusCode());

        String createdPid = EntityUtils.toString(resp.getEntity());

        List<String> uris = this.fixityClient.retrieveUris(serverAddress + "rest/objects");
        assertTrue("created pid not found in fixity client request", uris.contains(serverAddress + "rest" + createdPid));
    }

    @Test
    public void testQueueFixityCheck() throws Exception {
        /* create a new item in the objects folder */
        HttpPost postObject = new HttpPost(serverAddress + "rest/objects/fcr:new");
        HttpResponse resp = this.httpClient.execute(postObject);
        assertEquals("Unable to create Object",201, resp.getStatusLine().getStatusCode());
        String objectUri = serverAddress + "rest" + EntityUtils.toString(resp.getEntity());
        postObject.releaseConnection();

        /* add two datastreams to the object for checksumming tests */
        HttpPost postDs =new HttpPost(objectUri + "/ds1/fcr:content");
        postDs.setEntity(new StringEntity("foo"));
        resp=this.httpClient.execute(postDs);
        assertEquals("Unable to create datastream",201, resp.getStatusLine().getStatusCode());
        postDs.releaseConnection();
        postDs =new HttpPost(objectUri + "/ds2/fcr:content");
        postDs.setEntity(new StringEntity("bar"));
        resp=this.httpClient.execute(postDs);
        assertEquals("Unable to create datastream",201, resp.getStatusLine().getStatusCode());
        postDs.releaseConnection();

        /* queue a fixity check for the datastream uris */
        this.fixityClient.requestFixityCheck(objectUri + "/ds1");
        this.fixityClient.requestFixityCheck(objectUri + "/ds1");
    }
}
