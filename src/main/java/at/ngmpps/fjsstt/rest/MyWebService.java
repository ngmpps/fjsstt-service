package at.ngmpps.fjsstt.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.model.MyBean;

@Path("myservice")
public class MyWebService {

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("method1")
	public Response method1Get(@QueryParam("uri") String uri) {
		try {
			MyBean result = ModelFactory.createBean(uri);
			return Response.status(Response.Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getLocalizedMessage()).build();
		}

	}

	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("method1")
	public Response method1Post(MyBean input) {
		try {
			System.out.printf("Received and returning: " + input);
			return Response.status(Response.Status.OK).entity(input).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getLocalizedMessage()).build();
		}
	}
}
