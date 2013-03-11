package org.fcrepo.web.resources;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.fcrepo.services.fixity.FixityService;
import org.fcrepo.services.fixity.model.FixityResult;

@Named
@Path("/fixity")
public class Results {

	@Inject
	private FixityService service;

	@Path("/{pid}")
	@GET
	public List<FixityResult> getResults(@PathParam("pid") String pid) {
		return service.getResults(pid);
	}

	@Path("/queue/{pid}")
	@GET
	public Response queueFixityCheck(@PathParam("pid") String pid) throws IOException {
		service.checkObject(pid);
		return Response.ok().build();
	}
}
