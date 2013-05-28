/**
 *
 */
package org.fcrepo.web.fixity.integration;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;


public class FixityResultIT {
    private static final String fedoraUri = "http://localhost:8080";

    private final HttpClient client = new DefaultHttpClient();

    @Test
    public void testGetResult() throws Exception {
        HttpGet get = new HttpGet(fedoraUri + "/fixity");
        HttpResponse resp = client.execute(get);
        assertEquals(200, resp.getStatusLine().getStatusCode());
        get.releaseConnection();
    }
}
