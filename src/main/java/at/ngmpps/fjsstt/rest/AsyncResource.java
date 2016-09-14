package at.ngmpps.fjsstt.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.factory.ProblemParser;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.problem.FJSSTTproblem;
import at.ngmpps.fjsstt.model.problem.Solution;
import at.profactor.NgMPPS.ActorHelper;
import at.profactor.NgMPPS.Actors.Messages.MainSolveProtocol.SolutionReady;

@Path("/asyncresource")
public class AsyncResource {
	private static int numberOfSuccessResponses = 0;
	private static int numberOfFailures = 0;
	private static Throwable lastException = null;

	final static Logger log = LoggerFactory.getLogger(AsyncResource.class);

	final static Map<Integer, Solution> solutions = Collections.synchronizedMap(new HashMap<>());

	@GET
	public void asyncGetWithTimeout(@Suspended final AsyncResponse asyncResponse) {
		asyncResponse.setTimeoutHandler(new LocalTimeoutHandler());
		asyncResponse.setTimeout(20, TimeUnit.SECONDS);
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

		// no problem Set paramenter =>> use default problem
		ProblemSet ps = ModelFactory.createSrfgProblemSet();
		new Thread(new TriggerSolution(ps, asyncResponse)).start();
	}

    /**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "application/json" media type.
	 *
	 * @param problemSet:
	 *           a problem set parsed from JSON input
	 * @return the SolutionSet, which can be parsed into a JSON Object
	 */
	@POST
	@Path("/problem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void calcSolution(final ProblemSet problemSet, @Suspended final AsyncResponse asyncResponse) throws InterruptedException {
        asyncResponse.setTimeoutHandler(new LocalTimeoutHandler());
		asyncResponse.setTimeout(20, TimeUnit.SECONDS);
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

		new Thread(new TriggerSolution(problemSet, asyncResponse)).start();
	}

	class TriggerSolution implements Runnable {

		AsyncResponse asyncResponse;
		ProblemSet problemSet;

		public TriggerSolution(ProblemSet problemSet, AsyncResponse ar) {
			asyncResponse = ar;
			this.problemSet = problemSet;
		}

		@Override
		public void run() {
			try {
				Solution result = doSolve(problemSet);
				asyncResponse.resume(result);
			} catch (IOException e) {
				//e.printStackTrace();
				log.error(e.getLocalizedMessage());
			}
		}

		private Solution doSolve(ProblemSet problemSet) throws IOException {

			synchronized (solutions) {
				final Solution oldS = solutions.get(problemSet);
				if (oldS != null) {
					log.info("returning existing solution (hash: {})", oldS.hashCode());
					return oldS;
				}

				// build problem parser
				FJSSTTproblem problem = ProblemParser.parseStrings(problemSet.getFjs(), problemSet.getProperties(), problemSet.getTransport());
				// make sure we have the right ID!!
				problem.setProblemId(problemSet.hashCode());
				// DONE (gw): instrument the parser on the problemSet
				log.info("problem set parsed (hash: {})", problem.getProblemId());

				ActorHelper ah = new ActorHelper(problem, problem.getConfigurations(), true);
				SolutionReady sr = ah.solve(500);
				// you could use the method below to get 
				//ah.getCurrentSolution(problemSet.hashCode());
				solutions.put(problemSet.hashCode(), sr.getMinUpperBoundSolution());
				// TODO: generate additional solutions and put them to the HashMap
				// when new results are available

				log.info("returning new solution ({})", sr.getMinUpperBoundSolution());
				return sr.getMinUpperBoundSolution();
			}
		}
	}

    private class LocalTimeoutHandler implements TimeoutHandler {
        @Override
        public void handleTimeout(AsyncResponse resp) {
            resp.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Operation time out.").build());
        }
    }
}