package at.ngmpps.fjsstt;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.profactor.NgMPPS.NgMPPSmain;
import at.profactor.NgMPPS.TestRuns.TestAsyncRequests;

public class AsyncActorTriggerTest {

	public static void main(String args[]) throws Exception {
		// remove to not start JFrames for every Job & Machine Actor
		NgMPPSmain.startGUI = true;
		
		TestAsyncRequests test = new TestAsyncRequests();
		test.parseStrings(ModelFactory.createSrfgProblemSet().getFjs(), 
				ModelFactory.createSrfgProblemSet().getProperties(),
				ModelFactory.createSrfgProblemSet().getTransport());
		
		// starts a new thread that starts the Akka Actor System
		test.startAsync();
		// starts a new thread that polls (~every 10secs) intermediate results and writes to the console
		test.getCurrSolutionAsync();
	}
}
