package at.ngmpps.fjsstt;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.SolutionSet;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WSPostClient {

    public static void main(String args[]) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/fjsstt-service/rest/").path("asyncresource/problem");
        System.out.println("requesting target: " + target);
        final AsyncInvoker asyncInvoker = target.request().async();
        ProblemSet ps = ModelFactory.createSrfgProblemSet();
        final Future<Response> responseFuture = asyncInvoker.post(Entity.json(ps));
        System.out.println("Request is being processed asynchronously.");
        final Response response = responseFuture.get(5, TimeUnit.SECONDS);
        // get() waits for the response to be ready
        System.out.println("Response received. " + response);
        System.out.println("Content: " + response.readEntity(SolutionSet.class));
        client.close();

    }
}
