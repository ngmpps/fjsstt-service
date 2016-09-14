package at.ngmpps.fjsstt.rest;

import java.io.File;
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
import at.profactor.NgMPPS.Actors.Messages.MainSolveProtocol.SolutionReadyUI;

@Path("/asyncresource")
public class AsyncResource {
	private static int numberOfSuccessResponses = 0;
	private static int numberOfFailures = 0;
	private static Throwable lastException = null;

	final static Logger log = LoggerFactory.getLogger(AsyncResource.class);

	ActorHelper ah = null;

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

		checkStartActors();
		getSolution(ModelFactory.createSrfgProblemSet(), asyncResponse);
	}

	public void checkStartActors() {
		if (ah == null) {
				ah = new ActorHelper();
		}
	}
	public void getSolution(ProblemSet problemSet, AsyncResponse asyncResponse) {
		new Thread(new TriggerSolution(ah, problemSet, asyncResponse)).start();
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

		checkStartActors();
		getSolution(problemSet,asyncResponse);
	}


	@POST
	@Path("/solution")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void getUIFiles(final ProblemSet problemSet, @Suspended final AsyncResponse asyncResponse) throws InterruptedException {
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

		checkStartActors();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// first get Solution with all values, then the files
				SolutionReady sr = null;
				try{
					sr = ah.getCurrentSolution(problemSet.hashCode());
				} catch (Exception e) {
					// here we do nothing, but start a new algorithm

				}
				try{
					// nothing found?
					if (sr == null) {
						// this might cause problems on a slow or occupied server,
						// starting the algo all the time  (timeout is ~10secs)
						// solve might return the first solution found or wait for the
						// final results -> last boolean true = wait
						FJSSTTproblem problem = ProblemParser.parseStrings(problemSet.getFjs(), problemSet.getProperties(), problemSet.getTransport());
						// make sure we have the right ID!!
						problem.setProblemId(problemSet.hashCode());
						sr = ah.solve(problem, problem.getConfigurations(), 500, false);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				try {
					SolutionReadyUI srui = ah.getCurrentSolutionFiles(problemSet.hashCode());
					if(srui!=null) {
						StringBuilder html = new StringBuilder();
						html.append("<html><head><title>FJSSTT Problem Solved</title></head><body>");
						if(sr!=null) {
							html.append("<br />MaxLowerBound: ").append(sr.getMaxLowerBoundSolution());
							html.append("<br />MinUpperBound: ").append(sr.getMinUpperBoundSolution());
							html.append("<br />Algorithm is Finished?: ").append(sr.isFinished());
							html.append("<br /><br /><br />");
						}
						for(String f : srui.getUiFiles()) {
							html.append(f.substring(f.lastIndexOf(File.separatorChar)+1, f.length()));
							html.append("<img src='").append(f).append("' style='width:300px;'>");
						}
						asyncResponse.resume(html.toString());
					}
				}catch (Exception e) {
					asyncResponse.resume(e);
				}
				
			}
		}).start();
}

	
	class TriggerSolution implements Runnable {

		final ActorHelper ah;
		final AsyncResponse asyncResponse;
		final ProblemSet problem;

		public TriggerSolution(final ActorHelper ah, final ProblemSet problem , final AsyncResponse asyncResponse) {
			this.ah = ah;
			this.asyncResponse = asyncResponse;
			this.problem = problem;
		}

		@Override
		public void run() {
			// blocks for ~10s - or returns null if no alorithm started to solve this problem
			SolutionReady sr = null;
			try{
				sr = ah.getCurrentSolution(problem.hashCode());
			} catch (Exception e) {
				// here we do nothing, but start a new algorithm
			}
			try{
				// nothing found?
				if (sr == null) {
					FJSSTTproblem fjsstt = ProblemParser.parseStrings(problem.getFjs(), problem.getProperties(), problem.getTransport());
					// make sure we have the right ID!!
					fjsstt.setProblemId(problem.hashCode());
					log.info("problem set parsed (hash: {})", fjsstt.getProblemId());

					// this might cause problems on a slow or occupied server,
					// starting the algo all the time  (timeout is ~10secs)
					// solve might return the first solution found or wait for the
					// final results -> last boolean true = wait
					sr = ah.solve(fjsstt, fjsstt.getConfigurations(), 500, false);
				}
				if (sr != null) {
					// return a Solution object (not a SolutionSet)
					asyncResponse.resume(sr.getMinUpperBoundSolution());
				} else {
					asyncResponse.resume(new Throwable("nothing found sofar :-( maybe a bit later"));
				}
			}catch(Exception e) {
				asyncResponse.resume(e);
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