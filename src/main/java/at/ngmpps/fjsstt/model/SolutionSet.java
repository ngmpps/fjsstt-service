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
     * Configuration Information, such as "dual problem", "sub problems" or "feasibility repair"
     */
    private String configuration;

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
                       String configuration,
                       Map<String, String> solution,
                       Double lowerBound,
                       Double upperBound) {

        this.name = name;
        this.configuration = configuration;
        this.solution = solution;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getName() {
        return name;
    }

    public String getConfiguration() {
        return configuration;
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

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
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
                ", configuration='" + configuration + '\'' +
                ", solution=" + solution +
                ", lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                '}';
    }
}
