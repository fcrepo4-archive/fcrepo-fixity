/**
 *
 */

package org.fcrepo.fixity.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.riot.RDFDataMgr;
import org.fcrepo.RdfLexicon;
import org.fcrepo.fixity.model.DatastreamFixityError;
import org.fcrepo.fixity.model.DatastreamFixityRepaired;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.DatastreamFixityResult.ResultType;
import org.fcrepo.fixity.model.DatastreamFixitySuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * This class is responsible for interaction with the Fedora Repository
 *
 * @author frank asseg
 *
 */
@Service("fixityClient")
public class FedoraFixityClient {

    private static final Logger LOG = LoggerFactory
            .getLogger(FedoraFixityClient.class);

    /**
     * Fetch all the identifiers of objects which have a given parent
     * @param parentUri The URI of the parent (e.g. http://localhost:8080/rest/objects)
     * @return A {@link List} containing the URIs of the child objects
     */
    public List<String> retrieveUris(String parentUri) throws IOException {
        /* fetch a RDF Description of the parent form the repository */
        final HttpGet search = new HttpGet(parentUri);
        StmtIterator stmts = null;
        try {
            /* parse the RDF N3 response using Apache Jena */
            final Model model = ModelFactory.createDefaultModel();
            try {
                RDFDataMgr.read(model, parentUri);
            } catch (HttpException e) {
                stmts.close();
                throw new IOException("Unable to fetch uris from " + parentUri,
                        e);
            }

            /*
             * and iterate over all the elements which contain the predicate
             * #hasParent in order to discover objects
             */
            stmts = model.listStatements(null, RdfLexicon.HAS_PARENT, model
                    .createResource(parentUri));
            final List<String> uris = new ArrayList<>();
            while (stmts.hasNext()) {
                Statement st = stmts.next(); //NOSONAR
                uris.add(st.getSubject().getURI());
            }
            return uris;
        } finally {
            stmts.close();
            search.releaseConnection();
        }
    }

    /**
     * Request a datastream fixity check execution from Fedora an
     * @param uri the URI of the Fedora datastream
     */
    public List<DatastreamFixityResult> requestFixityChecks(
            final List<String> datastreamUris) throws IOException {

        final List<DatastreamFixityResult> results = new ArrayList<>();
        for (final String uri : datastreamUris) {
            /* parse the fixity part of the RDF response */
            final Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, uri + "/fcr:fixity");
            StmtIterator sts=null;
            try{
                sts = model.listStatements(model.createResource(uri),
                        RdfLexicon.HAS_FIXITY_RESULT, (RDFNode) null);
                if (!sts.hasNext()) {
                    sts.close();
                    throw new IOException("No fixity information available for " +
                            uri);
                }
                Statement st = sts.next();  //NOSONAR
                final Resource res = st.getObject().asResource();

                /* parse the checksum from the model */
                st =
                        model.listStatements(res, RdfLexicon.HAS_COMPUTED_CHECKSUM,
                                (RDFNode) null).next();
                final String checksum = st.getObject().asResource().getURI();

                /* parse the status */
                st =
                        model.listStatements(res, RdfLexicon.HAS_FIXITY_STATE,
                                (RDFNode) null).next();
                final String state = st.getObject().asLiteral().getString();

                /* parse the location */
                st =
                        model.listStatements(res, RdfLexicon.HAS_LOCATION,
                                (RDFNode) null).next();
                final String location = st.getObject().asResource().getURI();

                LOG.debug("Found fixity information: [{}, {}, {}]", state,
                        checksum, location);

                ResultType type = ResultType.valueOf(state);
                switch (type) {
                    case ERROR:
                        results.add(new DatastreamFixityError(uri));
                        break;
                    case SUCCESS:
                        results.add(new DatastreamFixitySuccess(uri));
                        break;
                    case REPAIRED:
                        results.add(new DatastreamFixityRepaired(uri));
                        break;
                    default:
                        throw new IOException(
                                "Unabel to handle results of unkwonn type");
                }
            }finally{
                sts.close();
            }
        }
        return results;
    }

}
