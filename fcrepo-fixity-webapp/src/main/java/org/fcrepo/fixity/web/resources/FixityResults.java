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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.fcrepo.fixity.db.FixityDatabaseService;
import org.fcrepo.fixity.model.FixityStatistics;
import org.fcrepo.fixity.model.ObjectFixityResult;
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

    @Autowired
    private FixityService fixityService;

    @Autowired
    private FixityDatabaseService databaseService;

    @GET
    public List<ObjectFixityResult> getAllResults() {
        return databaseService.getResults(0, 50);
    }

    @Path("/{offset}/{length}")
    @GET
    public List<ObjectFixityResult> getAllResultsWithOffset(@PathParam("offset") int offset, @PathParam("length") int length) {
        return databaseService.getResults(offset, length);
    }

    @Path("/{pid}")
    @GET
    public List<ObjectFixityResult> getResults(@PathParam("uri") String uri) {
        return databaseService.getResults(uri);
    }

    @Path("/details/{recordId}")
    @GET
    public ObjectFixityResult getResult(@PathParam("recordId") long recordId){
        return databaseService.getResult(recordId);
    }

    @Path("/queue")
    @POST
    public Response queueFixityCheck(@QueryParam("uri") String uri) throws IOException {
        fixityService.queueFixityCheck(uri);
        return Response.ok().build();
    }

    @Path("/statistics/daily")
    @GET
    public List<FixityStatistics> getDailyStatistics(){
        return databaseService.getFixityStatistics();
    }
}
