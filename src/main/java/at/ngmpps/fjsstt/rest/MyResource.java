package at.ngmpps.fjsstt.rest;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.model.MyBean;
import at.ngmpps.fjsstt.model.ProblemSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Got it!";
	}


	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "application/json" media type.
	 *
	 * @return MyBean, which can be parsed into a JSON Object
	 * @param uri
	 */
	@GET
	@Path("/mybean/{uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public MyBean getMyBean(@PathParam("uri") String uri) {
		return ModelFactory.createBean(uri);
	}


	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "application/json" media type.
	 *
	 * @return the ProblemSet, which can be parsed into a JSON Object
	 * @param id: "srfg" to get the SRFG instance of the problem set, any other to get a dummy problem set
	 */
	@GET
	@Path("/problem/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProblemSet getProblem(@PathParam("id") String id) {
		if ("srfg".equals(id))
			return ModelFactory.createSrfgProblemSet();
		else {
			return ModelFactory.createDummyProblemSet();
		}
	}

}
