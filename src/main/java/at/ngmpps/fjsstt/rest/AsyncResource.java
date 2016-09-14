package at.ngmpps.fjsstt.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

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

	ActorHelper ah = null;
	Integer lastRequestId = -1;

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

		// no problem Set paramenter =>> use default problem // do not set lastRequestId, keep old one (unless actor is started)
		checkStartActors(ModelFactory.createSrfgProblemSet());
		// using last requestId
		getSolution(lastRequestId, asyncResponse);
	}

	public Integer checkStartActors(ProblemSet problemSet) {
		if (ah == null) {
			// reuse lastRequestId
			lastRequestId = problemSet.hashCode();
			synchronized (lastRequestId) {
				FJSSTTproblem problem = ProblemParser.parseStrings(problemSet.getFjs(), problemSet.getProperties(), problemSet.getTransport());
				// make sure we have the right ID!!
				problem.setProblemId(lastRequestId);
				log.info("problem set parsed (hash: {})", lastRequestId);
				// do not wait for end result but take return the first one found =>
				// false
				ah = new ActorHelper(problem, problem.getConfigurations(), false);
			}
		}
		return lastRequestId;
	}
	public void getSolution(Integer requestId, AsyncResponse asyncResponse) {
		new Thread(new TriggerSolution(ah, requestId, asyncResponse)).start();
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

		lastRequestId = checkStartActors(problemSet);
		// using last requestId
		getSolution(lastRequestId,asyncResponse);
	}

	class TriggerSolution implements Runnable {

		final Integer problemId;
		final ActorHelper ah;
		final AsyncResponse asyncResponse;

		public TriggerSolution(final ActorHelper ah, final Integer problemId, AsyncResponse asyncResponse) {
			this.problemId = problemId;
			this.ah = ah;
			this.asyncResponse = asyncResponse;
		}

		@Override
		public void run() {
			// blocks for ~10s
			SolutionReady sr = ah.getCurrentSolution(problemId);
			// nothing found?
			if (sr == null)
				// this might cause problems on a slow or occupied server,
				// starting the algo all the time  (timeout is ~10secs)
				// solve might return the first solution found or wait for the
				// final result -> see last param in ActorHelper()
				sr = ah.solve(500);

			if (sr != null) {
				// return a Solution object (not a SolutionSet)
				asyncResponse.resume(sr.getMinUpperBoundSolution());
			} else {
				asyncResponse.resume(new Throwable("nothing found sofar :-( maybe a bit later"));
			}
		}
	}
}