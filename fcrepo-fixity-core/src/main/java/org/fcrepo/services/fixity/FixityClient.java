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
import org.fcrepo.services.fixity.model.DatastreamFixity.ResultType;
import org.fcrepo.services.fixity.model.FixityProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
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
        final HttpGet get = new HttpGet(serverAddress + "objects/" + pid + "/" + dsId);
        try{
            final HttpResponse resp = client.execute(get);
            return null;
        }finally{
            get.releaseConnection();
        }
    }

    public DatastreamFixity getDatastreamFixity(String datastreamUri)
            throws IOException {
        final HttpGet get = new HttpGet(datastreamUri + "/fcr:fixity");
        try {
            final HttpResponse resp = client.execute(get);

            if (resp.getStatusLine().getStatusCode() != 200) {
                throw new IOException(
                        "Unable to fetch Datastream fixity from fedora");
            }

            /* get the fixity result from the datasream */
            final Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, resp.getEntity().getContent(), Lang.N3);

            final StmtIterator sts =
                    model.listStatements(
                            model.createResource(datastreamUri),
                            model.createProperty("info:fedora/fedora-system:def/internal#hasFixityResult"),
                            (RDFNode) null);

            final DatastreamFixity fixity = new DatastreamFixity();
            fixity.setDatastreamId(datastreamUri);
            fixity.setProblems(new ArrayList<FixityProblem>());
            ResultType overallResult = ResultType.SUCCESS;

            while (sts.hasNext()) {
                /* get the status of the fixity check first */
                final Statement st = sts.next();
                final StmtIterator prop =
                        model.listStatements(
                                st.getObject().asResource(),
                                model.createProperty("info:fedora/fedora-system:def/internal#status"),
                                (RDFNode) null);
                final ResultType status =
                        ResultType.valueOf(prop.next().getString());
                if (status != ResultType.SUCCESS) {
                    final FixityProblem pb = new FixityProblem();
                    pb.type = status;
                    fixity.getProblems().add(pb);
                }

                /* set the error state for the overall result */
                if (status == ResultType.REPAIRED &&
                        overallResult != ResultType.ERROR) {
                    overallResult = ResultType.REPAIRED;
                }
                if (status == ResultType.ERROR) {
                    overallResult = ResultType.ERROR;
                }
            }

            fixity.setType(overallResult);
            return fixity;
        } finally {
            get.releaseConnection();
        }
    }

    public List<String> getDatastreamUris(final String pid) throws IOException {
        final String uri = serverAddress + "objects/" + pid;
        final HttpGet get = new HttpGet(uri);
        try {
            final HttpResponse resp = client.execute(get);

            if (resp.getStatusLine().getStatusCode() != 200) {
                throw new IOException(
                        "Unable to fetch Datastream IDs from fedora");
            }

            final Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, resp.getEntity().getContent(), Lang.N3);
            get.releaseConnection();
            /*
             * list all the objects in the model with a corresponding resource
             * association and mixin type
             */
            SimpleSelector selector =
                    new SimpleSelector(
                            null,
                            model.createProperty("info:fedora/fedora-system:def/internal#mixinTypes"),
                            "fedora:datastream");
            StmtIterator sts = model.listStatements(selector);

            List<String> datastreamIds = new ArrayList<>();
            while (sts.hasNext()) {
                datastreamIds.add(sts.next().getSubject().getURI());
            }
            logger.debug("discovered " + datastreamIds.size() +
                    " objects for object " + pid);

            return datastreamIds;
        } finally {
            get.releaseConnection();
        }
    }

    public InputStream getDatastreamContent(String pid, String dsId) {
        return null;
    }

    public List<String> getPids() throws IOException {
        final String uri = serverAddress + "objects";
        final HttpGet get = new HttpGet(uri);
        try {
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
            get.releaseConnection();

            StmtIterator sts = model.listStatements();

            List<String> pids = new ArrayList<>();

            while (sts.hasNext()) {
                Statement st = sts.next();
                if (st.getSubject().getURI().equals(uri) &&
                        st.getPredicate()
                                .getURI()
                                .equals("info:fedora/fedora-system:def/internal#hasChild")) {
                    Resource child = (Resource) st.getObject();
                    pids.add(child.getURI().substring(uri.length() + 1));
                    logger.debug("adding child " + pids.get(pids.size() - 1));
                }
            }

            return pids;
        } finally {
            get.releaseConnection();
        }
    }

}
