package at.ngmpps.fjsstt.rest;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.SolutionSet;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("/asyncresource")
public class AsyncResource {
    private static int numberOfSuccessResponses = 0;
    private static int numberOfFailures = 0;
    private static Throwable lastException = null;

    @GET
    public void asyncGetWithTimeout(@Suspended final AsyncResponse asyncResponse) {
        asyncResponse.register(new CompletionCallback() {
            @Override
            public void onComplete(Throwable throwable) {
                if (throwable == null) {
                    // no throwable - the processing ended successfully
                    // (response already written to the client)
                    numberOfSuccessResponses++;
                } else {
                    numberOfFailures++;
                    lastException = throwable;
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = veryExpensiveOperation();
                asyncResponse.resume(result);
            }

            private String veryExpensiveOperation() {
                try {
                    for (int i = 0; i < 10; i++) {
                        System.out.println("sleeping for another second: " + i);
                        Thread.sleep(1000);
                    }
                    return "Done";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return "Interrupted";
                }
            }
        }).start();
    }


    /**
     * Method handling HTTP GET requests. The returned object will be sent to the
     * client as "application/json" media type.
     *
     * @param problemSet: a problem set parsed from JSON input
     * @return the SolutionSet, which can be parsed into a JSON Object
     */
    @POST
    @Path("/problem")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void calcSolution(ProblemSet problemSet, @Suspended final AsyncResponse ar) throws InterruptedException {
        ar.register(new CompletionCallback() {
            @Override
            public void onComplete(Throwable throwable) {
                if (throwable == null) {
                    // no throwable - the processing ended successfully
                    // (response already written to the client)
                    numberOfSuccessResponses++;
                } else {
                    numberOfFailures++;
                    lastException = throwable;
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                SolutionSet result = veryExpensiveOperation();
                ar.resume(result);
            }

            private SolutionSet veryExpensiveOperation() {
                try {
                    int n = 2000;
                    System.out.println("need " + n + " seconds for calculating solutions");
                    Thread.sleep(n);
                    return ModelFactory.createSrfgSolutionSet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return ModelFactory.noSolutionSet();
                }
            }
        }).start();
    }


}