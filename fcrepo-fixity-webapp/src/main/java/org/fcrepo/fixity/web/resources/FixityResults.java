/**
 *
 */

package org.fcrepo.fixity.web.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
 *
 */
@Path("/fixity-results")
@Component
public class FixityResults {

    private static final int MAX_RESULTS = 50;

    @Autowired
    private FixityService fixityService;

    @Autowired
    private FixityDatabaseService databaseService;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<ObjectFixityResult> getAllResults() {
        return databaseService.getResults(0, MAX_RESULTS);
    }

    @Path("/{offset}/{length}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @GET
    public List<ObjectFixityResult> getAllResultsWithOffset(
            @PathParam("offset")
            int offset, @PathParam("length")
            int length) {
        return databaseService.getResults(offset, length);
    }

    @Path("/datastream/details/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @GET
    public DatastreamFixityResult getDatastreamFixityResult(@PathParam("id") final long id) {
        return databaseService.getDatastreamFixityResult(id);
    }

    @Path("/{pid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<ObjectFixityResult> getResults(@PathParam("uri")
    String uri) {
        return databaseService.getResults(uri);
    }

    @Path("/details/{recordId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public ObjectFixityResult getResult(@PathParam("recordId")
    long recordId) {
        return databaseService.getResult(recordId);
    }

    @Path("/queue")
    @POST
    public Response queueFixityChecks() throws IOException {
        fixityService.queueFixityChecks(null);
        return Response.ok().build();
    }

    @Path("/queue/{uri}")
    @POST
    public Response queueFixityChecks(@PathParam("uri")
    final String uri) throws IOException {
        fixityService.queueFixityCheck(uri);
        return Response.ok().build();
    }

    @Path("/statistics-daily")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<DailyStatistics> getDailyStatistics() {
        return databaseService.getDailyStatistics();
    }

    @Path("/statistics")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Statistics getStatistics() {
        return databaseService.getStatistics();
    }
}
