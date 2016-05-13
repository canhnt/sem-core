package nl.uva.sne.semantic.infraviz;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

public interface oldInfraVizService {
	
	@GET
	@Path("{baseLayer}")
	@Produces("application/json")
	public Response requestNodes(@PathParam("baseLayer") String baseLayer);

}
