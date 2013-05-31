/**
 *
 */

package org.fcrepo.fixity.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * This class is responsible for interaction with the Fedora Repository
 *
 * @author frank asseg
 *
 */
public class FedoraFixityClient {

    private final static String PREDICATE_PREFIX =
            "info:fedora/fedora-system:def/internal";

    private final static String PREDICATE_STATUS = PREDICATE_PREFIX + "#status";

    private final static String PREDICATE_HASPARENT = PREDICATE_PREFIX +
            "#hasParent";

    private static final Logger logger = LoggerFactory
            .getLogger(FedoraFixityClient.class);

    /**
     * Fetch all the identifiers of objects which have a given parent
     * @param parentUri The URI of the parent (e.g. http://localhost:8080/rest/objects)
     * @return A {@link List} containing the URIs of the child objects
     */
    public List<String> retrieveUris(final String parentUri) throws IOException {
        /* fetch a RDF Description of the parent form the repository */
        final HttpGet search = new HttpGet(parentUri);
        try {
            /* parse the RDF N3 response using Apache Jena */
            final Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, parentUri);

            /*
             * and iterate over all the elements which contain the predicate
             * #hasParent in order to discover objects
             */
            final StmtIterator stmts =
                    model.listStatements(null, model
                            .createProperty(PREDICATE_HASPARENT), model
                            .createResource(parentUri));
            final List<String> uris = new ArrayList<>();
            while (stmts.hasNext()) {
                final Statement st = stmts.next();
                uris.add(st.getSubject().getURI());
            }

            return uris;
        } finally {
            search.releaseConnection();
        }
    }
}
