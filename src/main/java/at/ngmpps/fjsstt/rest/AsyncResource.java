package at.ngmpps.fjsstt.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.factory.ProblemParser;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.SolutionSet;
import at.ngmpps.fjsstt.model.problem.FJSSTTproblem;
import at.ngmpps.fjsstt.model.problem.subproblem.SubproblemSolverConfig;
import at.profactor.NgMPPS.ActorHelper;
import at.profactor.NgMPPS.Actors.Messages.MainSolveProtocol.SolutionReady;
import at.profactor.NgMPPS.Actors.Messages.MainSolveProtocol.SolutionReadyUI;
import at.profactor.NgMPPS.DualProblem.SubgradientSearch;
import at.profactor.NgMPPS.UI.ConsoleProblemVisualiser;

@Path("/asyncresource")
public class AsyncResource {
	private static int numberOfSuccessResponses = 0;
	private static int numberOfFailures = 0;
	private static Throwable lastException = null;

	final static Logger log = LoggerFactory.getLogger(AsyncResource.class);
	
	final static Map<String,String> imageTable = new Hashtable<String, String>();

	static ActorHelper ah = null;

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
		// switch console output off
		ConsoleProblemVisualiser.printoutStatus = false;
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
	@Produces(MediaType.APPLICATION_XHTML_XML)
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
				checkConfiguration(problemSet);
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
						
						//System.out.println("problem jobs: "+problem.getJobs()+" problem config: " + problem.getConfigurations());
						sr = ah.solve(problem, problem.getConfigurations(), 500, false);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				try {
					SolutionReadyUI srui = ah.getCurrentSolutionFiles(problemSet.hashCode());
					if(srui!=null) {
						StringBuilder html = new StringBuilder();
						// just the html to be inserted in page
						//html.append("<html><head><title>FJSSTT Problem Solved</title></head><body>");
						if(sr!=null) {
							html.append("<br /><span id=\"maxlowerbound\">MaxLowerBound: ").append(sr.getMaxLowerBoundSolution().getObjectiveValue());
							html.append("</span><span id=\"minupperbound\"><br />MinUpperBound: ").append(sr.getMinUpperBoundSolution().getObjectiveValue());
							html.append("</span><br /><span id=\"algofinished\">Algorithm is Finished? ").append(sr.isFinished());
							html.append("</span><br /><br /><br />");
						}
						for(String f : srui.getUiFiles()) {
							html.append("<p class='text-info'>");
							String filename = f.substring(f.lastIndexOf(File.separatorChar)+1, f.length());
							imageTable.put(filename, f);
							html.append(filename);
							html.append("<br/><img src='rest/asyncresource/images/").append(filename).append("' style='width:100%;' />");
							html.append("</p>");
						}
						html.append("nr images:").append(imageTable.size());
						//html.append("</body></html>");
						asyncResponse.resume(html.toString());
					}
				}catch (Exception e) {
					asyncResponse.resume(e);
				}
				
			}
		}).start();
	}
	
	@POST
	@Path("/currentsolution")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentSolution(final ProblemSet problemSet) throws InterruptedException {
		SolutionReady sr = null;
		checkConfiguration(problemSet);
		try{
			sr = ah.getCurrentSolution(problemSet.hashCode());
			FJSSTTproblem fjsstt = ProblemParser.parseStrings(problemSet.getFjs(), problemSet.getProperties(), problemSet.getTransport());
			if(sr != null && fjsstt != null)
				return Response.ok(new SolutionSet(problemSet, fjsstt, sr.getMinUpperBoundSolution(),sr.getMaxLowerBoundSolution())).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
	
	@Path("images/{name}")
	@Produces("image/png")
	@GET
	public Response getImage(@PathParam("name") String name) {
		//System.out.println("=========================================get image: " + name);
		if(imageTable.containsKey(name)) {
			//System.out.println("-------------------------------------------Filename: " + imageTable.get(name));
			File f = new File(imageTable.get(name));
			if(f.exists() && f.canRead()){
				//System.out.println("can read" + f.getAbsolutePath());
				try {
				   return Response.ok(new FileInputStream(f)).build();
				} catch (IOException e) {
					//quietly ignore if we can not access the png (e.g. because its currently written
				}
			} else {
				return Response.status(Status.FORBIDDEN).build();
			}
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

	
	class TriggerSolution implements Runnable {

		final ActorHelper ah;
		final AsyncResponse asyncResponse;
		final ProblemSet problem;

		public TriggerSolution(final ActorHelper ah, final ProblemSet problem , final AsyncResponse asyncResponse) {
			this.ah = ah;
			this.asyncResponse = asyncResponse;
			this.problem = checkConfiguration(problem);
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
				FJSSTTproblem fjsstt = null;
				// nothing found?
				if (sr == null) {
					fjsstt = ProblemParser.parseStrings(problem.getFjs(), problem.getProperties(), problem.getTransport());
					// make sure we have the right ID!!
					fjsstt.setProblemId(problem.hashCode());
					log.info("problem set parsed (hash: {})", fjsstt.getProblemId());

					// this might cause problems on a slow or occupied server,
					// starting the algo all the time  (timeout is ~10secs)
					// solve might return the first solution found or wait for the
					// final results -> last boolean true = wait
					sr = ah.solve(fjsstt, fjsstt.getConfigurations(), 500, false);
				}
				if (sr != null && fjsstt !=null) {
					// return a SolutionSet
					asyncResponse.resume(new SolutionSet(problem, fjsstt, sr.getMinUpperBoundSolution(),sr.getMaxLowerBoundSolution()));
				} else {
					asyncResponse.resume(new Throwable("nothing found sofar :-( maybe a bit later"));
				}
			}catch(Exception e) {
				asyncResponse.resume(e);
			}
		}
	}
	/**
	 * make sure, that we execute one run and only one valid run
	 * since the id is important to identify the problem, we have to correct the config beforehand
	 * @param configuration
	 */
	protected ProblemSet checkConfiguration(ProblemSet problemSet){
		String config = problemSet.getProperties();
		Pattern dualproblemBoth = Pattern.compile(SubgradientSearch.SEARCH_TYPE_KEY+"[ ]*=[ ]*"+SubgradientSearch.SEARCH_TYPE_BOTH, Pattern.CASE_INSENSITIVE);
		Pattern dualproblemSurrogate = Pattern.compile(SubgradientSearch.SEARCH_TYPE_KEY+"[ ]*=[ ]*"+SubgradientSearch.SEARCH_TYPE_SURROGATE_SUBGRADIENT_SEARCH, Pattern.CASE_INSENSITIVE);

		Pattern subProblemSearchDP = Pattern.compile(SubproblemSolverConfig.TYPE_KEY+"[ ]*=[ ]*"+SubproblemSolverConfig.TYPE_DP, Pattern.CASE_INSENSITIVE);
		Pattern subProblemSearchVNS = Pattern.compile(SubproblemSolverConfig.TYPE_KEY+"[ ]*=[ ]*"+SubproblemSolverConfig.TYPE_VNS, Pattern.CASE_INSENSITIVE);
		
		boolean useVNS = false;
		Matcher dualproblemBothM = dualproblemBoth.matcher(config); 
		if(dualproblemBothM.find()){
			System.out.println("Both");
			config = config.substring(0, dualproblemBothM.start()) + 
					"\n" + 
					SubgradientSearch.SEARCH_TYPE_KEY+" = "+SubgradientSearch.SEARCH_TYPE_SURROGATE_SUBGRADIENT_SEARCH+"\n"+
					config.substring(dualproblemBothM.end());
			useVNS = true;
		} else  if(dualproblemSurrogate.matcher(config).find()) {
			useVNS = true;
			System.out.println("Surrogate");
		}
		Matcher subProblemSearchDPM = subProblemSearchDP.matcher(config);
		Matcher subProblemSearchVNSM = subProblemSearchVNS.matcher(config);
		if (useVNS &&subProblemSearchDPM.find()) {
			config = config.substring(0, subProblemSearchDPM.start()) + 
					"\n" + 
					SubproblemSolverConfig.TYPE_KEY+" = "+SubproblemSolverConfig.TYPE_VNS+"\n"+
					config.substring(subProblemSearchDPM.end());
			System.out.println("VNS");
		} else if (!useVNS && subProblemSearchVNSM.find()) {
			config = config.substring(0, subProblemSearchDPM.start()) + 
					"\n" + 
					SubproblemSolverConfig.TYPE_KEY+" = "+SubproblemSolverConfig.TYPE_DP+"\n"+
					config.substring(subProblemSearchDPM.end());
			System.out.println("DP");
		}
		problemSet.setProperties(config);
		System.out.println(config);
		return problemSet;
	}
	
    private class LocalTimeoutHandler implements TimeoutHandler {
   	 public LocalTimeoutHandler() {
   		 
   	 }
        @Override
        public void handleTimeout(AsyncResponse resp) {
            resp.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Operation time out.").build());
        }
    }
    
}