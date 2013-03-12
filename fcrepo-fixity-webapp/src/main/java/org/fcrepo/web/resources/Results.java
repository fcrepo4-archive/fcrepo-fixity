package org.fcrepo.web.resources;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.fcrepo.services.fixity.FixityService;
import org.fcrepo.services.fixity.model.DailyStatistics;
import org.fcrepo.services.fixity.model.FixityResult;
import org.fcrepo.services.fixity.model.GeneralStatistics;

@Named
@Path("/results")
public class Results {

	@Inject
	private FixityService service;

	@GET
	public List<FixityResult> getAllResults() {
		return service.getResults(0, 50);
	}

	@Path("/{offset}/{length}")
	@GET
	public List<FixityResult> getAllResultsWithOffset(@PathParam("offset") int offset, @PathParam("length") int length) {
		return service.getResults(offset, length);
	}

	@Path("/{pid}")
	@GET
	public List<FixityResult> getResults(@PathParam("pid") String pid) {
		return service.getResults(pid);
	}

	@Path("/queue")
	@POST
	public Response queueFixityCheck(@QueryParam("pid") String pid) throws IOException {
		service.checkObject(pid);
		return Response.ok().build();
	}

	@Path("/statistics/general")
	@GET
	public GeneralStatistics getGeneralStatsitics() {
		return service.getStatistics();
	}
	
	@Path("/statistics/daily")
	@GET
	public List<DailyStatistics> getDailyStatistics(){
		return service.getDailyStatistics();
	}
}
