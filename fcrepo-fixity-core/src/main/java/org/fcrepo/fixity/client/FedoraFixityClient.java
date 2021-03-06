/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */

package org.fcrepo.fixity.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.fcrepo.RdfLexicon;
import org.fcrepo.fixity.model.DatastreamFixityError;
import org.fcrepo.fixity.model.DatastreamFixityRepaired;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.DatastreamFixitySuccess;
import org.fcrepo.utils.FixityResult.FixityState;
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
 */
@Service("fixityClient")
public class FedoraFixityClient {

    private static final Logger LOG = LoggerFactory
            .getLogger(FedoraFixityClient.class);

    /**
     * Fetch all the identifiers of objects which have a given parent
     * 
     * @param parentUri
     *        The URI of the parent (e.g. http://localhost:8080/rest/objects)
     * @return A {@link List} containing the URIs of the child objects
     */
    public List<String> retrieveUris(String parentUri) throws IOException {
        return retrieveUris(parentUri, parentUri);
    }

    /**
     * TODO
     * 
     * @param sourceUri TODO
     * @param parentUri TODO
     * @return TODO
     * @throws IOException
     */
    public List<String> retrieveUris(String sourceUri, String parentUri)
        throws IOException {
        /* fetch a RDF Description of the parent from the repository */
        StmtIterator stmts = null;
        try {
            /* parse the RDF N3 response using Apache Jena */
            final Model model = ModelFactory.createDefaultModel();
            try {
                LOG.info("reading model for {} from parent URI {}", parentUri,
                        sourceUri);
                RDFDataMgr.read(model, sourceUri);
            } catch (HttpException e) {
                throw new IOException("Unable to fetch uris from " + parentUri,
                        e);
            }

            /*
             * and iterate over all the elements which contain the predicate
             * #hasParent in order to discover objects
             */
            stmts =
                    model.listStatements(model.createResource(parentUri),
                            RdfLexicon.HAS_CHILD, (RDFNode) null);
            final List<String> uris = new ArrayList<>();
            while (stmts.hasNext()) {
                Statement st = stmts.next(); // NOSONAR
                String uri = st.getObject().asResource().getURI();
                uris.add(uri);
                LOG.debug("adding '" + uri + "' to retrieveUris results");
            }
            return uris;
        } finally {
            if (stmts != null) {
                stmts.close();
            }
        }
    }

    /**
     * Request a datastream fixity check execution from Fedora an
     * 
     * @param datastreamUris a List of the Fedora datastream URIs
     */
    public List<DatastreamFixityResult> requestFixityChecks(
            final List<String> datastreamUris) throws IOException {

        final List<DatastreamFixityResult> results = new ArrayList<>();
        for (final String uri : datastreamUris) {
            /* parse the fixity part of the RDF response */
            final Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, uri + "/fcr:fixity", Lang.N3);
            StmtIterator sts = null;
            try {
                sts =
                        model.listStatements(model.createResource(uri),
                                RdfLexicon.HAS_FIXITY_RESULT, (RDFNode) null);
                if (!sts.hasNext()) {
                    sts.close();
                    throw new IOException(
                            "No fixity information available for " + uri);
                }
                Statement st = sts.next(); // NOSONAR
                final Resource res = st.getObject().asResource();

                /* parse the checksum from the model */
                st =
                        model.listStatements(res,
                                RdfLexicon.HAS_COMPUTED_CHECKSUM,
                                (RDFNode) null).next();
                final String checksum = st.getObject().asResource().getURI();

                /* parse the status */
                st =
                        model.listStatements(res, RdfLexicon.HAS_FIXITY_STATE,
                                (RDFNode) null).next();
                final String stateName = st.getObject().asLiteral().getString();

                /* parse the location */
                st =
                        model.listStatements(res, RdfLexicon.HAS_LOCATION,
                                (RDFNode) null).next();
                final String location = st.getObject().asResource().getURI();

                LOG.debug("Found fixity information: [{}, {}, {}]", stateName,
                        checksum, location);

                FixityState state = FixityState.valueOf(stateName);
                switch (state) {
                    case BAD_CHECKSUM:
                        results.add(new DatastreamFixityError(uri,
                                "Datastream has the wrong Checksum"));
                        break;
                    case BAD_SIZE:
                        results.add(new DatastreamFixityError(uri,
                                "Datastream has a bad size"));
                        break;
                    case SUCCESS:
                        results.add(new DatastreamFixitySuccess(uri));
                        break;
                    case REPAIRED:
                        results.add(new DatastreamFixityRepaired(uri));
                        break;
                    default:
                        throw new IOException(
                                "Unable to handle results of unknown type");
                }
            } finally {
                if (sts != null) {
                    sts.close();
                }
            }
        }
        return results;
    }

    /**
     * @param objectUri
     * @return
     */
    public List<String> retrieveDatatstreamUris(String objectUri) {
        final Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, objectUri);
        final StmtIterator sts =
                model.listStatements(null, RdfLexicon.HAS_MIXIN_TYPE,
                        "fedora:datastream");
        try {
            List<String> result = new ArrayList<>();
            while (sts.hasNext()) {
                final Statement st = sts.next(); // NOSONAR
                final String dsUri = st.getSubject().asResource().getURI();
                result.add(dsUri);
            }
            return result;
        } finally {
            if (sts != null) {
                sts.close();
            }
        }
    }
}
