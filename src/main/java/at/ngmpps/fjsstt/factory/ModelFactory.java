package at.ngmpps.fjsstt.factory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ngmpps.fjsstt.model.MyBean;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.SolutionSet;

public class ModelFactory {
	private static final Map<Integer, Float> myMap;

	static {
		Map<Integer, Float> aMap = new HashMap<>();
		aMap.put(1, 1.1f);
		aMap.put(2, 1.2f);
		myMap = Collections.unmodifiableMap(aMap);
	}

	private static final List<String> myList;

	static {
		List<String> aList = new ArrayList<>();
		aList.add("entry1");
		aList.add("entry2");
		myList = Collections.unmodifiableList(aList);
	}

	public static MyBean createBean(String uri) {
		final MyBean myBean = new MyBean(URI.create(uri), "mybean", "mydata", myList, myMap);
		return myBean;
	}

	public static ProblemSet createDummyProblemSet() {
	    return new ProblemSet("dummyFJS", "dummyTransport", "dummyProp=xyz");
    }
	public static ProblemSet createSrfgProblemSet() {
		final ProblemSet ps = new ProblemSet("15 28\n" +
				"6 2 2 3 5 5 2 14 4 15 5 3 19 5 20 6 21 8 3 22 4 23 3 24 6 3 25 5 26 4 28 6 3 19 5 20 6 21 8 0 45 1\n" +
				"6 3 19 5 20 6 21 8 3 25 5 26 4 28 6 3 19 5 20 6 21 8 2 14 4 15 5 3 22 4 23 3 24 6 2 2 3 5 5 0 35 2\n" +
				"6 2 14 4 15 5 3 25 5 26 4 28 6 3 19 5 20 6 21 8 3 19 5 20 6 21 8 2 2 3 5 5 3 22 4 23 3 24 6 0 40 3\n" +
				"6 2 14 4 15 5 3 19 5 20 6 21 8 2 2 3 5 5 3 19 5 20 6 21 8 3 25 5 26 4 28 6 3 22 4 23 3 24 6 0 50 2\n" +
				"6 3 19 5 20 6 21 8 2 2 3 5 5 3 25 5 26 4 28 6 2 14 4 15 5 3 19 5 20 6 21 8 3 22 4 23 3 24 6 0 45 3\n" +
				"7 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 2 27 3 5 4 2 1 2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 50 1\n" +
				"7 2 1 2 6 3 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 2 27 3 5 4 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 55 2\n" +
				"7 2 14 5 15 4 2 3 6 4 7 2 1 2 6 3 2 27 3 5 4 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 3 7 7 8 8 9 6 0 55 1\n" +
				"7 2 14 5 15 4 2 3 6 4 7 2 1 2 6 3 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 27 3 5 4 3 7 7 8 8 9 6 0 50 3\n" +
				"7 2 27 3 5 4 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 1 2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 50 1\n" +
				"5 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 7 5 8 4 9 8 3 19 5 20 6 21 8 0 30 1\n" +
				"5 3 19 5 20 6 21 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8 4 9 8 0 25 2\n" +
				"5 3 19 5 20 6 21 8 3 7 5 8 4 9 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 0 25 3\n" +
				"5 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8 4 9 8 3 19 5 20 6 21 8 0 30 4\n" +
				"5 3 7 5 8 4 9 8 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 19 5 20 6 21 8 0 35 4",

				"0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n" +
				"0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n" +
				"0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n" +
				"22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n" +
				"5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n" +
				"22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n" +
				"0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n" +
				"0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\n" +
				"22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n" +
				"8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;12;8;7;7;21;7;21;8;18\n" +
				"8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;12;8;7;7;21;7;21;8;18\n" +
				"4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n" +
				"4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n" +
				"13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\n" +
				"11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\n" +
				"5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n" +
				"5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n" +
				"7;7;7;15;6;15;7;7;15;8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15\n" +
				"13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\n" +
				"13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\n" +
				"7;7;7;15;6;15;7;7;15;8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15\n" +
				"4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n" +
				"4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n" +
				"11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\n" +
				"4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\n" +
				"11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\n" +
				"5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\n" +
				"22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\n",

				"");
		return ps;
	}

    private static final Map<String, String> mySolution;

    static {
	    Map<String, String> aSolution = new HashMap<>();
        aSolution.put("Job 0", "(4,0,5) (13,9,4) (20,15,8) (22,29,3) (24,32,5) (19,48,6)");
        aSolution.put("Job 1", "(19,0,6) (27,13,6) (18,26,5) (13,31,4) (22,46,3) (1,53,3)");
        aSolution.put("Job 2", "(14,0,5) (25,5,4) (20,23,8) (18,33,5) (4,41,5) (21,51,4)");
        aSolution.put("Job 3", "(13,5,4) (18,9,5) (4,17,5) (19,25,6) (24,42,5) (21,47,4)");
        aSolution.put("Job 4", "(18,0,5) (4,8,5) (25,19,4) (14,23,5) (20,35,8) (22,49,3)");
        aSolution.put("Job 5", "(11,0,3) (24,3,6) (26,13,3) (0,21,2) (2,33,6) (6,40,7) (14,58,4)");
        aSolution.put("Job 6", "(0,0,2) (11,6,3) (24,9,6) (26,19,3) (2,27,6) (6,33,7) (14,51,4)");
        aSolution.put("Job 7", "(14,5,4) (2,20,6) (0,26,2) (26,33,3) (11,40,3) (27,49,4) (8,53,6)");
        aSolution.put("Job 8", "(13,0,5) (3,12,7) (5,19,3) (27,22,4) (11,32,3) (26,39,3) (6,47,7)");
        aSolution.put("Job 9", "(26,0,3) (25,9,5) (11,18,3) (5,27,3) (3,30,7) (8,37,6) (13,50,5)");
        aSolution.put("Job 10", "(9,0,8) (9,53,9) (10,62,9) (7,79,4) (20,90,8)");
        aSolution.put("Job 11", "(18,14,5) (10,36,9) (9,45,8) (10,53,8) (7,69,4)");
        aSolution.put("Job 12", "(20,0,8) (7,15,4) (9,27,10) (9,37,8) (10,45,8)");
        aSolution.put("Job 13", "(10,0,9) (9,9,8) (9,17,9) (7,34,4) (20,45,8)");
        aSolution.put("Job 14", "(7,0,4) (10,12,7) (10,19,8) (10,27,9) (18,48,5)");
        mySolution = Collections.unmodifiableMap(aSolution);
    }

	public static SolutionSet createSrfgSolutionSet() {
		// rem values for config make no sense
		final SolutionSet ss = new SolutionSet("Algorithmus 1", "SubgradientSearch=Surrogate Subgradientenverfahren\n SubgradientSolution=Variable Nachbarschaftssuche\n FeasibilityRepari=List Scheduling\n",
                mySolution, 537.5, 541.0);
		return ss;
	}

    public static SolutionSet noSolutionSet() {
        return null;
    }
    
    public static SolutionSet emptySolutionSet() {
   	 // rem values for config make no sense
        final SolutionSet ss = new SolutionSet("noname", "SubgradientSearch=no problem\n SubgradientSolution=no subproblems\n FeasibilityRepari=no feasibilityRepair\n",
                null, 0.0, 1.0);
        return ss;
    }
}
