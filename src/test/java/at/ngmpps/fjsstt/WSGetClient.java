package at.ngmpps.fjsstt;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;

public class WSGetClient {

    public static void main(String args[]) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/fjsstt-service/rest/").path("asyncresource");
        System.out.println("requesting target: " + target);
        final AsyncInvoker asyncInvoker = target.request().async();
        final Future<Response> responseFuture = asyncInvoker.get();
        System.out.println("Request is being processed asynchronously.");
        final Response response = responseFuture.get();
        // get() waits for the response to be ready
        System.out.println("Response received.");
        System.out.println("Content: " + response.readEntity(String.class));
        client.close();

    }
}
