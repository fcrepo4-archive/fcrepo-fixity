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

package org.fcrepo.fixity.web.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fcrepo.fixity.db.FixityDatabaseService;
import org.fcrepo.fixity.model.DailyStatistics;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.fcrepo.fixity.model.Statistics;
import org.fcrepo.fixity.service.FixityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author frank asseg
 */
@Path("/fixity-results")
@Component
public class FixityResults {

    private static final int MAX_RESULTS = 50;

    @Autowired
    private FixityService fixityService;

    @Autowired
    private FixityDatabaseService databaseService;

    /**
     * TODO
     * 
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<ObjectFixityResult> getAllResults() {
        return databaseService.getResults(0, MAX_RESULTS);
    }

    /**
     * TODO
     * 
     * @param offset
     * @param length
     * @return
     */
    @Path("/{offset}/{length}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @GET
    public List<ObjectFixityResult> getAllResultsWithOffset(
            @PathParam("offset")
            int offset, @PathParam("length")
            int length) {
        return databaseService.getResults(offset, length);
    }

    /**
     * TODO
     * 
     * @param id
     * @return
     */
    @Path("/datastream/details/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @GET
    public DatastreamFixityResult getDatastreamFixityResult(
            @PathParam("id")
            final long id) {
        return databaseService.getDatastreamFixityResult(id);
    }

    /**
     * TODO
     * 
     * @param uri
     * @return
     */
    @Path("/{pid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<ObjectFixityResult> getResults(
            @PathParam("uri")
            String uri) {
        return databaseService.getResults(uri);
    }

    /**
     * TODO
     * 
     * @param recordId
     * @return
     */
    @Path("/details/{recordId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public ObjectFixityResult getResult(
            @PathParam("recordId")
            long recordId) {
        return databaseService.getResult(recordId);
    }

    /**
     * TODO
     * 
     * @param url
     * @return
     * @throws IOException
     */
    @Path("/queue")
    @POST
    public Response queueFixityChecks(
            @QueryParam("url")
            final String url) throws IOException {
        if (url == null || url.length() == 0) {
            fixityService.queueFixityChecks(null);
        } else {
            fixityService.queueFixityChecks(Arrays.asList(url));
        }
        return Response.ok().build();
    }

    /**
     * TODO
     * 
     * @return
     */
    @Path("/statistics-daily")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<DailyStatistics> getDailyStatistics() {
        return databaseService.getDailyStatistics();
    }

    /**
     * TODO
     * 
     * @return
     */
    @Path("/statistics")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Statistics getStatistics() {
        return databaseService.getStatistics();
    }
}
