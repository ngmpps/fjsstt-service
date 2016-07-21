package at.ngmpps.fjsstt.rest;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.factory.ProblemParser;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.SolutionSet;
import at.ngmpps.fjsstt.model.problem.subproblem.Bid;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@javax.ws.rs.Path("/asyncresource")
public class AsyncResource {
    private static int numberOfSuccessResponses = 0;
    private static int numberOfFailures = 0;
    private static Throwable lastException = null;

    final static Logger log = LoggerFactory.getLogger(AsyncResource.class);

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
                SolutionSet result = veryExpensiveOperation();
                ar.resume(result);
            }

            private SolutionSet veryExpensiveOperation() {
                try {
                    // write ProblemSet to local files
                    List<Path> filenames = writeToFiles(problemSet);
                    Path fjsFile = filenames.get(0);
                    log.info("fjsFile available at: {}", fjsFile);
                    Path transportFile = filenames.get(1);
                    log.info("transportFile available at: {}", transportFile);
                    Path propertiesFile = filenames.get(2);
                    log.info("propertiesFile available at: {}", propertiesFile);
                    // build problem parser
                    ProblemParser parser = new ProblemParser();
                    // TODO (gw): instrument the parser on the three Filenames in "List<String> filenames"

                    // TODO (gw): Calculate and return problem solution

                    // delete temporary files
                    for (Path file : filenames) {
                        Files.deleteIfExists(file);
                        log.info("file {} deleted.", file);
                    }

                    // TODO (fs): Transform Solution to SolutionSet (or directly return Solution, if possible)

                    return ModelFactory.emptySolutionSet();

                } catch (IOException e) {
                    e.printStackTrace();
                    return ModelFactory.emptySolutionSet();
                }
            }
        }).start();
    }

    private List<Path> writeToFiles(ProblemSet problemSet) throws IOException {
        final Path fjsFile = Files.createTempFile("PROBLEM", ".fjs");
        List<String> lines = Arrays.asList(problemSet.getFjs().split("\n"));
        Files.write(fjsFile, lines, Charset.forName("UTF-8"));
        final Path transportFile = Files.createTempFile("PROBLEM", ".transport");
        List<String> tLines = Arrays.asList(problemSet.getTransport().split("\n"));
        Files.write(transportFile, tLines, Charset.forName("UTF-8"));
        final Path propertiesFile = Files.createTempFile("PROBLEM", ".properties");
        List<String> pLines = Arrays.asList(problemSet.getProperties().split("\n"));
        Files.write(propertiesFile, pLines, Charset.forName("UTF-8"));
        return Arrays.asList(fjsFile, transportFile, propertiesFile);
    }


}