/**
 *
 */
package org.fcrepo.web.fixity.integration;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;


public class FixityResultIT {
    private static final String serverUri = "http://localhost:8080";

    private final HttpClient client = new DefaultHttpClient();

    @Test
    public void testGetResult() throws Exception {
        HttpGet get = new HttpGet(serverUri + "/fixity");
        HttpResponse resp = client.execute(get);
        assertEquals(200, resp.getStatusLine().getStatusCode());
        get.releaseConnection();
    }

    @Test
    public void testQueueAllObjects() throws Exception {
        HttpPost post = new HttpPost(serverUri + "/fixity/rest/results/queue");
        HttpResponse resp = client.execute(post);
        assertEquals(200, resp.getStatusLine().getStatusCode());
        post.releaseConnection();
    }


    @Test
    public void testCreateAndQueueObject() throws Exception {
        HttpPost postNew = new HttpPost(serverUri + "/fcrepo/rest/fcr:new");
        HttpResponse resp = client.execute(postNew);
        assertEquals(201, resp.getStatusLine().getStatusCode());
        postNew.releaseConnection();

        HttpPost post = new HttpPost(serverUri + "/fixity/rest/results/queue");
        resp = client.execute(post);
        assertEquals(200, resp.getStatusLine().getStatusCode());
        post.releaseConnection();
    }

    @Test
    public void testCreateAndQueueObjectAndDatastream() throws Exception {
        String pid = UUID.randomUUID().toString();
        String dsId = "datastream-1";
        HttpPost postNew = new HttpPost(serverUri + "/fcrepo/rest/objects/" + pid + "/fcr:new");
        HttpResponse resp = client.execute(postNew);
        assertEquals(201, resp.getStatusLine().getStatusCode());
        postNew.releaseConnection();

        HttpPost postDs = new HttpPost(serverUri + "/fcrepo/rest/objects/" + pid + "/" + dsId + "/fcr:content");
        postDs.setEntity(new StringEntity("null-content"));
        resp = client.execute(postDs);
        assertEquals(201, resp.getStatusLine().getStatusCode());
        postDs.releaseConnection();


        HttpPost post = new HttpPost(serverUri + "/fixity/rest/results/queue");
        resp = client.execute(post);
        assertEquals(200, resp.getStatusLine().getStatusCode());
        post.releaseConnection();
    }
}
