package at.ngmpps.fjsstt.rest;

import at.ngmpps.fjsstt.factory.ProblemParser;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.problem.FJSSTTproblem;
import at.ngmpps.fjsstt.model.problem.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@javax.ws.rs.Path("/asyncresource")
public class AsyncResource {
    private static int numberOfSuccessResponses = 0;
    private static int numberOfFailures = 0;
    private static Throwable lastException = null;

    final static Logger log = LoggerFactory.getLogger(AsyncResource.class);

    final static Map<ProblemSet, Solution> solutions = Collections.synchronizedMap(new HashMap<>());

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
                        log.info("sleeping for another second: {}", i);
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
    @javax.ws.rs.Path("/problem")
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
                try {
                    Solution result = veryExpensiveOperation();
                    ar.resume(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private Solution veryExpensiveOperation() throws IOException {

                synchronized (solutions) {
                    final Solution oldS = solutions.get(problemSet);
                    if (oldS != null) {
                        log.info("returning existing solution (hash: {})", oldS.hashCode());
                        return oldS;
                    }

                    // build problem parser
                    ProblemParser parser = new ProblemParser();
                    // DONE (gw): instrument the parser on the problemSet
                    parser.parseProblem(problemSet.getFjs());
                    parser.parseTransportTimes(problemSet.getTransport());
                    parser.parseConfiguration(problemSet.getProperties());
                    log.info("problem set parsed (hash: {})", problemSet.hashCode());
                    // DONE (gw): Calculate and return problem solution
                    FJSSTTproblem problem = parser.getProblem();
                    // TODO (gw) this does not generate any solution, it just holds the data...
                    final Solution newS = new Solution();
                    solutions.put(problemSet, newS);
                    // TODO: generate additional solutions and put them to the HashMap when new results are available

                    log.info("returning new solution (hash: {})", newS.hashCode());
                    return newS;
                }

            }
        }).start();
    }

}