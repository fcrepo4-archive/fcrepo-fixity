/**
 *
 */
package org.fcrepo.fixity.integration.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
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
@ContextConfiguration("/test-container.xml")
public class FedoraFixityClientIT{

    private final FedoraFixityClient client = new FedoraFixityClient();

    private static final String serverAddress = "http://localhost:8080/";

    @Test
    public void testRetrievePids() throws Exception {
        /* create a new item in the objects folder */
        HttpPost postObject = new HttpPost(serverAddress + "rest/objects/fcr:new");
        HttpResponse resp = new DefaultHttpClient().execute(postObject);
        assertEquals("Unable to create Object",201, resp.getStatusLine().getStatusCode());

        String createdPid = EntityUtils.toString(resp.getEntity());

        List<String> uris = client.retrieveUris(serverAddress + "rest/objects");
        assertTrue("created pid not found in fixity client request", uris.contains(serverAddress + "rest" + createdPid));
    }
}
