package at.ngmpps.fjsstt.model;

import java.util.Map;

/**
 *
 * Proposed structure of a solution set. The examples in the javadoc refer to the two solution sets given in
 * https://github.com/ngmpps/scenario/blob/master/Loesung.md
 *
 */
public class SolutionSet {

    /**
     * Arbitrary name of the algorithm used (e.g. "Algorithm 1", "Algorithm 2")
     */
    private String name;

    /**
     * The name of the dual problem (e.g. "Surrogate Subgradientenverfahren")
     */
    private String dualProblem;

    /**
     * Name of the sub problems (e.g. "Variable Nachbarschaftssuche", "Dynamische Programmierung")
     */
    private String subProblems;

    /**
     * Feasibility Repair (e.g. "List Scheduling")
     */
    private String feasibilityRepair;

    /**
     * A Map of Strings representing the solution, one entry per job.
     * The "Key" is the name of the job, the "Value" is the solution itself, e.g.
     *   "Job 0" -> (4,0,5) (13,9,4) (20,15,8) (22,29,3) (24,32,5) (19,48,6)
     *   "Job 1" -> (19,0,6) (27,13,6) (18,26,5) (13,31,4) (22,46,3) (1,53,3)
     *   ...
     *   (TODO: We can structure it even more as "Map<String, Solution>",
     *   where the type Solution would be a List<String> representing the steps of the solution.)
     */
    private Map<String, String> solution;

    /**
     * The lower bound of the solution (e.g. 537.5, 537.7)
     */
    private Double lowerBound;

    /**
     * The upper bound of the solution (e.g. 541)
     */
    private Double upperBound;

    public SolutionSet() {
    }

    public SolutionSet(String name,
                       String dualProblem,
                       String subProblems,
                       String feasibilityRepair,
                       Map<String, String> solution,
                       Double lowerBound,
                       Double upperBound) {

        this.name = name;
        this.dualProblem = dualProblem;
        this.subProblems = subProblems;
        this.feasibilityRepair = feasibilityRepair;
        this.solution = solution;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getName() {
        return name;
    }

    public String getDualProblem() {
        return dualProblem;
    }

    public String getSubProblems() {
        return subProblems;
    }

    public String getFeasibilityRepair() {
        return feasibilityRepair;
    }

    public Map<String, String> getSolution() {
        return solution;
    }

    public Double getLowerBound() {
        return lowerBound;
    }

    public Double getUpperBound() {
        return upperBound;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDualProblem(String dualProblem) {
        this.dualProblem = dualProblem;
    }

    public void setSubProblems(String subProblems) {
        this.subProblems = subProblems;
    }

    public void setFeasibilityRepair(String feasibilityRepair) {
        this.feasibilityRepair = feasibilityRepair;
    }

    public void setSolution(Map<String, String> solution) {
        this.solution = solution;
    }

    public void setLowerBound(Double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(Double upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return "SolutionSet{" +
                "name='" + name + '\'' +
                ", dualProblem='" + dualProblem + '\'' +
                ", subProblems='" + subProblems + '\'' +
                ", feasibilityRepair='" + feasibilityRepair + '\'' +
                ", solution=" + solution +
                ", lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                '}';
    }
}