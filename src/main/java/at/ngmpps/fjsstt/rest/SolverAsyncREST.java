package at.ngmpps.fjsstt.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.factory.ProblemParser;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.SolutionSet;
import at.ngmpps.fjsstt.model.problem.FJSSTTproblem;
import at.ngmpps.fjsstt.model.problem.subproblem.SubproblemSolverConfig;
import at.profactor.NgMPPS.ActorHelper;
import at.profactor.NgMPPS.NgMPPSmain;
import at.profactor.NgMPPS.Actors.Messages.MainSolveProtocol.SolutionReady;
import at.profactor.NgMPPS.DualProblem.SubgradientSearch;
import at.profactor.NgMPPS.UI.ConsoleProblemVisualiser;

@Path("/solver")
@Singleton
public class SolverAsyncREST {
    private static int numberOfSuccessResponses = 0;
    private static int numberOfFailures = 0;
    private static Throwable lastException = null;

    private final static Logger log = LoggerFactory.getLogger(SolverAsyncREST.class);

    private final static Map<String, String> imageTable = new ConcurrentHashMap<String, String>();

    private void checkStartActors() {
 		NgMPPSmain.startGUI = false;
 		NgMPPSmain.isRemote = false;
        // switch console output off
        ConsoleProblemVisualiser.printoutStatus = false;
    }

    @POST
    @Path("/solution")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void postSolution(final ProblemSet problemSet, @Suspended final AsyncResponse asyncResponse) throws InterruptedException {
        // Start with input validation of "problemSet", before SolverThread is spawned.
        checkConfiguration(problemSet); // THIS CALL MAY MODIFY THE HASHCODE!!
        FJSSTTproblem problem = ProblemParser.parseStrings(problemSet.getFjs(), problemSet.getProperties(), problemSet.getTransport());
        // make sure we have the right ID!!
        problem.setProblemId(problemSet.hashCode());
        log.debug("problem config: {}", problem.getConfigurations());

        // after 20 Seconds, an empty SolutionSet is returned, if none is available.
        asyncResponse.setTimeoutHandler(new TimeoutHandler() {
            @Override
            public void handleTimeout(AsyncResponse asyncResponse) {
                asyncResponse.resume(Response.status(Response.Status.OK).entity(ModelFactory.emptySolutionSet()).build());
            }
        });
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
                SolutionSet solset = ModelFactory.emptySolutionSet();
                try {
                    sr = ActorHelper.getCurrentSolution(problemSet.hashCode());
                    solset = new SolutionSet(problemSet, problem, sr.getMinUpperBoundSolution(), sr.getMaxLowerBoundSolution());
                    asyncResponse.resume(solset);
                } catch (Exception e) {
                    // we need to start a new algorithm
                    log.info("no existing solution for {} - start new algorithm!", problemSet.hashCode());
                    try {
                        // nothing found?
                        if (sr == null) {
                            // this might cause problems on a slow or occupied server,
                            // starting the algo all the time  (timeout is ~10secs)
                            // solve might return the first solution found or wait for the
                            // final results -> last boolean true = waitForEndResults
                            sr = ActorHelper.solve(problem, problem.getConfigurations(), 10, false);
                        }
                        // we got a solution before the timeout, we can return it!
                        if (sr != null) {
                            solset = new SolutionSet(problemSet, problem, sr.getMinUpperBoundSolution(), sr.getMaxLowerBoundSolution());
                        }
                        asyncResponse.resume(solset);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * make sure, that we execute one run and only one valid run
     * since the id is important to identify the problem, we have to correct the config beforehand
     *
     * @param problemSet the ProblemSet where properties should be fixed.
     */
    private ProblemSet checkConfiguration(ProblemSet problemSet) {
        String config = problemSet.getProperties();
        Pattern dualproblemBoth = Pattern.compile(SubgradientSearch.SEARCH_TYPE_KEY + "[ ]*=[ ]*" + SubgradientSearch.SEARCH_TYPE_BOTH, Pattern.CASE_INSENSITIVE);
        Pattern dualproblemSurrogate = Pattern.compile(SubgradientSearch.SEARCH_TYPE_KEY + "[ ]*=[ ]*" + SubgradientSearch.SEARCH_TYPE_SURROGATE_SUBGRADIENT_SEARCH, Pattern.CASE_INSENSITIVE);

        Pattern subProblemSearchDP = Pattern.compile(SubproblemSolverConfig.TYPE_KEY + "[ ]*=[ ]*" + SubproblemSolverConfig.TYPE_DP, Pattern.CASE_INSENSITIVE);
        Pattern subProblemSearchVNS = Pattern.compile(SubproblemSolverConfig.TYPE_KEY + "[ ]*=[ ]*" + SubproblemSolverConfig.TYPE_VNS, Pattern.CASE_INSENSITIVE);

        boolean useVNS = false;
        Matcher dualproblemBothM = dualproblemBoth.matcher(config);
        if (dualproblemBothM.find()) {
            config = config.substring(0, dualproblemBothM.start()) +
                    "\n" +
                    SubgradientSearch.SEARCH_TYPE_KEY + " = " + SubgradientSearch.SEARCH_TYPE_SURROGATE_SUBGRADIENT_SEARCH + "\n" +
                    config.substring(dualproblemBothM.end());
            useVNS = true;
        } else if (dualproblemSurrogate.matcher(config).find()) {
            useVNS = true;
        }
        Matcher subProblemSearchDPM = subProblemSearchDP.matcher(config);
        Matcher subProblemSearchVNSM = subProblemSearchVNS.matcher(config);
        if (useVNS && subProblemSearchDPM.find()) {
            config = config.substring(0, subProblemSearchDPM.start()) +
                    "\n" +
                    SubproblemSolverConfig.TYPE_KEY + " = " + SubproblemSolverConfig.TYPE_VNS + "\n" +
                    config.substring(subProblemSearchDPM.end());
        } else if (!useVNS && subProblemSearchVNSM.find()) {
            config = config.substring(0, subProblemSearchDPM.start()) +
                    "\n" +
                    SubproblemSolverConfig.TYPE_KEY + " = " + SubproblemSolverConfig.TYPE_DP + "\n" +
                    config.substring(subProblemSearchDPM.end());
        }
        problemSet.setProperties(config);
        return problemSet;
    }
    
    @GET
    @Path("/statusfinished")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFinishedSolutions() throws InterruptedException {
   	 checkStartActors();
   	 List<SolutionReady> sol = ActorHelper.getFinishedSolutions(); 
   	 List<SolutionSet> result = new ArrayList<SolutionSet>(sol.size());
   	 for(int i=0;i<sol.size();++i) {
   		 result.add(i, new SolutionSet());
   		 result.get(i).setName("Solution for Problem #"+sol.get(i).getProblemId());
   		 result.get(i).setProblemId(sol.get(i).getProblemId());
   		 result.get(i).setMaxLowerBoundSolution(sol.get(i).getMaxLowerBoundSolution().getObjectiveValue());
   		 result.get(i).setMinUpperBoundSolution(sol.get(i).getMinUpperBoundSolution().getObjectiveValue());
   	 } 
   	 GenericEntity<List<SolutionSet>> entity = new GenericEntity<List<SolutionSet>>(result) {};
   	 return Response.status(Response.Status.OK).entity(entity).build();
    }
    @GET
    @Path("/statusrunning")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunningSolutions() throws InterruptedException {
   	 checkStartActors();
   	 Collection<SolutionReady> sol = ActorHelper.getRunningSolutions(); 
   	 List<SolutionSet> result = new ArrayList<SolutionSet>(sol.size());
   	 int i = -1;
   	 for(SolutionReady sr : sol) {
   		 i++;
   		 result.add(i, new SolutionSet());
   		 result.get(i).setName("Solution for Problem #"+sr.getProblemId());
   		 result.get(i).setProblemId(sr.getProblemId());
   		 result.get(i).setMaxLowerBoundSolution(sr.getMaxLowerBoundSolution().getObjectiveValue());
   		 result.get(i).setMinUpperBoundSolution(sr.getMinUpperBoundSolution().getObjectiveValue());
   	 } 
   	 GenericEntity<List<SolutionSet>> entity = new GenericEntity<List<SolutionSet>>(result) {};
   	 return Response.status(Response.Status.OK).entity(entity).build();
    }
}
