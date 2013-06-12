/**
 *
 */

package org.fcrepo.fixity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.fcrepo.fixity.client.FedoraFixityClient;
import org.junit.Test;

/**
 * @author frank asseg
 *
 */
public class FedoraFixitClientTest {

    private final FedoraFixityClient client = new FedoraFixityClient();

    @Test
    public void testRetrieveObjects() throws Exception {
        URI responseUri = this.getClass().getClassLoader()
                .getResource("mock-responses/response-turtle-objects.n3")
                .toURI();
        assertNotNull(responseUri);
        assertEquals("file", responseUri.getScheme());
        List<String> objectUris =
                this.client.retrieveUris(responseUri.toASCIIString(),"http://localhost:8080/fcrepo/rest/objects");
        assertNotNull(objectUris);
        assertTrue(objectUris.size() == 2);
    }
    @Test
    public void testRetrieveDatastreams() throws Exception {
        URI responseUri = this.getClass().getClassLoader()
                .getResource("mock-responses/response-turtle-datastreams.n3")
                .toURI();
        assertNotNull(responseUri);
        assertEquals("file", responseUri.getScheme());
        List<String> objectUris =this.client.retrieveDatatstreamUris(responseUri.toASCIIString());
        assertNotNull(objectUris);
        assertTrue(objectUris.size() == 3);
    }
}
