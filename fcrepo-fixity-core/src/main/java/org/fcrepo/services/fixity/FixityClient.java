/**
 *
 */

package org.fcrepo.services.fixity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.fcrepo.services.fixity.model.DatastreamChecksum;
import org.fcrepo.services.fixity.model.DatastreamFixity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author frank asseg
 *
 */
public class FixityClient {

    private final HttpClient client = new DefaultHttpClient();

    private static final Logger logger = LoggerFactory
            .getLogger(FixityClient.class);

    private String serverAddress;

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public DatastreamChecksum getDatastreamChecksum(String pid, String dsId)
            throws IOException {
        final HttpGet get =
                new HttpGet(serverAddress + "objects/" + pid + "/" + dsId);
        final HttpResponse resp = client.execute(get);
        return null;
    }

    public DatastreamFixity getDatastreamFixity(String pid, String dsId)
            throws IOException {
        return null;
    }

    public List<String> getDatastreamIds(final String pid) throws IOException {
        final List<String> pids = new ArrayList<>();
        final String uri = serverAddress + "objects/" + pid;
        final HttpGet get = new HttpGet(uri);
        get.setHeader("Accept", "application/rdf+json");
        final HttpResponse resp = client.execute(get);

        if (resp.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Unable to fetch Datastream IDs from fedora");
        }

        final Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, resp.getEntity().getContent(), Lang.N3);
        final String childUri =
                model.getResource(uri)
                        .getProperty(
                                ResourceFactory
                                        .createProperty("info:fedora/fedora-system:def/internal#hasChild"))
                        .getLiteral().getString();

        pids.add(childUri);
        return pids;
    }

    public InputStream getDatastreamContent(String pid, String dsId) {
        return null;
    }

    public List<String> getPids() throws IOException {
        final String uri = serverAddress + "objects";
        final HttpGet get = new HttpGet(uri);
        final HttpResponse resp = client.execute(get);

        if (resp.getStatusLine().getStatusCode() != 200) {
            resp.getEntity().getContent().close();
            throw new HttpResponseException(resp.getStatusLine()
                    .getStatusCode(),
                    "Unable to fetch object list from fedora: " +
                            resp.getStatusLine().getReasonPhrase());
        }

        /* parse the RDF describe response */
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, resp.getEntity().getContent(), Lang.N3);

        StmtIterator sts = model.listStatements();

        List<String> pids = new ArrayList<>();

        while (sts.hasNext()) {
            Statement st = sts.next();
            if (st.getSubject().getURI().equals(uri) && st.getPredicate().getURI().equals("info:fedora/fedora-system:def/internal#hasChild")){
                Resource child = (Resource) st.getObject();
                pids.add(child.getURI().substring(uri.length() + 1));
                logger.debug("adding child " + pids.get(pids.size() - 1));
            }
        }

        return pids;
    }

}
