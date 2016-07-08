package at.ngmpps.fjsstt.model;


import java.util.List;
import java.util.Random;

/**
 * Datastructure representing a solution for a FJSSTT problem.
 * 
 * @author ahaemm
 * 
 */
public class Solution {

	/**
	 * The problem to be solved.
	 */
	final FJSSTTproblem problem;

	double objectiveValue;

	/**
	 * The job bids, each one representing the solution of a job-level subproblem. Indices are jobs.
	 */
	final Bid[] bids;

	/**
	 * The completion times of job operations.
	 */
	final int[][] operationsBeginTimes;

	/**
	 * The machine assignments of job operations.
	 */
	final int[][] operationsMachineAssignments;

	/**
	 * The iteration in which the solution was found.
	 */
	int iteration;

	/**
	 * The subgradient values resulting from mOperationsBeginTimes and mOperationsMachineAssignments.
	 */
	final int[][] subgradients;

	/**
	 * The vector of strictly positive Lagrange multipliers that led to the solution.
	 */
	final double[][] multipliers;

	public Solution(final FJSSTTproblem problem) {
		this(problem, Double.NEGATIVE_INFINITY);
	}

	public Solution(final FJSSTTproblem problem, final double objectiveValue) {
		this(problem, objectiveValue, null, 0, new int[problem.getMachines()][problem.getTimeSlots()]);
	}

	public Solution(final FJSSTTproblem problem, final double objectiveValue, final Bid[] bids, final int iteration,
			final int[][] subgradients) {
		this(problem, objectiveValue, bids, iteration, subgradients, null);
	}

	public Solution(final FJSSTTproblem problem, final double objectiveValue, final Bid[] bids, final int iteration,
			final int[][] subgradients, final double[][] multipliers) {
		this.problem = problem;
		this.objectiveValue = objectiveValue;
		this.bids = bids;
		this.iteration = iteration;
		this.subgradients = subgradients;

		this.operationsBeginTimes = new int[problem.getJobs()][problem.getMaxOperations()];
		this.operationsMachineAssignments = new int[problem.getJobs()][problem.getMaxOperations()];
		this.multipliers = new double[problem.getMachines()][problem.getTimeSlots()];

		// for all jobs and operations: compile the arrays for optimal machine
		// assignments and completion times for operations
		if (this.bids != null) {
			for (final Bid bid : this.bids) {
				final int job = bid.getJobID();
				final int[] machineAssignments = bid.getmOptimumMachines();
				final int[] beginTimes = bid.getmOptimumBeginTimes();

				System.arraycopy(machineAssignments, 0, this.operationsMachineAssignments[job], 0,
						machineAssignments.length);
				System.arraycopy(beginTimes, 0, this.operationsBeginTimes[job], 0, beginTimes.length);
			}
		} else {
			createInitialInfeasibleSolution();
		}

		if (multipliers != null) {
			for (int m = 0; m < problem.getMachines(); m++) {
				System.arraycopy(multipliers[m], 0, multipliers[m], 0, multipliers[m].length);
			}
		}
	}

	/**
	 * Constructor used by ListScheduling, so that it can return a real Solution and not just a String[][]. Problems:
	 * ListScheduling does not know in which iteration it is called, so it cannot set mIteration. Both bids and
	 * subgradients are not important for ListScheduling and - to my knowledge- need not be set.
	 */
	public Solution(final FJSSTTproblem problem, final double objectiveValue, final int[][] begTimes,
			final int[][] machAss) {
		this(problem, objectiveValue, begTimes, machAss, 0);
	}

	public Solution(final FJSSTTproblem problem, final double objectiveValue, final int[][] begTimes,
			final int[][] machAss, final int iteration) {
		this.problem = problem;
		this.objectiveValue = objectiveValue;
		this.bids = null;
		this.iteration = iteration;

		this.operationsBeginTimes = begTimes;
		this.operationsMachineAssignments = machAss;
		this.subgradients = null;
		this.multipliers = null;
		createInitialInfeasibleSolution();
	}

	/**
	 * TODO: javadoc!
	 * 
	 * @return
	 */
	protected Solution createInitialInfeasibleSolution() {
		if (problem != null
				&& (objectiveValue == -1 || objectiveValue == Double.NEGATIVE_INFINITY
						|| objectiveValue == Double.POSITIVE_INFINITY || objectiveValue == Double.MAX_VALUE || objectiveValue == Double.MIN_VALUE)) {
			
			for (int i = 0; i < problem.getJobs(); i++) {
				for (int j = 0; j < problem.getOperations()[i]; j++) {
					if (j == 0) {
						setOperationsBeginTimes(i, j, j);
						final List<Integer> H_ij = problem.getAltMachines(i, j);
						Random r = new Random();
						int machine = r.nextInt(H_ij.size());
						setOperationsMachineAssignments(i, j, H_ij.get(machine));
						
					} else {
						setOperationsBeginTimes(i, j, getOperationsBeginTimes()[i][j-1] + 
								problem.getProcessTimes()[i][j - 1][getOperationsMachineAssignments()[i][j - 1]]);
						final List<Integer> H_ij = problem.getAltMachines(i, j);
						Random r = new Random();
						int machine = r.nextInt(H_ij.size());
						setOperationsMachineAssignments(i, j, H_ij.get(machine));
					}
				}
				// H_ij.iterator().next() simply takes the first one. code below also; no need to create iterator
				// mInfeasibleSolution.setmOperationsMachineAssignments(i, j, H_ij.iterator().next());

			}

		}
		return this;
	}

	public Solution clone() {
		Solution result = new Solution(problem, objectiveValue, bids, iteration, subgradients);
		if (operationsBeginTimes != null)
			for (int i = 0; i < operationsBeginTimes.length; ++i)
				for (int ii = 0; ii < operationsBeginTimes[i].length; ++ii)
					result.setOperationsBeginTimes(i, ii, operationsBeginTimes[i][ii]);

		if (operationsMachineAssignments != null)
			for (int i = 0; i < operationsMachineAssignments.length; ++i)
				for (int ii = 0; ii < operationsMachineAssignments[i].length; ++ii)
					result.setOperationsMachineAssignments(i, ii, operationsMachineAssignments[i][ii]);
		return result;
	}

	/**
	 * Checks if two solutions are equal. The check only considers machine assignments and begin times.
	 * 
	 * @param sol
	 *            The solution to be compared to.
	 * @return True if the solutions are equal.
	 */
	public boolean equals(Solution sol) {
		for (int i = 0; i < problem.getJobs(); i++) {
			for (int j = 0; j < problem.getOperations()[i]; j++) {
				if (this.operationsMachineAssignments[i][j] != sol.getOperationsMachineAssignments()[i][j])
					return false;
				if (this.operationsBeginTimes[i][j] != sol.getOperationsBeginTimes()[i][j])
					return false;
			}
		}
		return true;
	}

	/**
	 * @return the mObjectiveValue
	 */
	public double getObjectiveValue() {
		return objectiveValue;
	}

	public void setObjectiveValue(double newvalue) {
		objectiveValue = newvalue;
	}

	/**
	 * @return the mBids
	 */
	public Bid[] getBids() {
		return bids;
	}

	/**
	 * @return the mIteration
	 */
	public int getIteration() {
		return iteration;
	}

	public void setIteration(int bestvaluefoundiniteration) {
		iteration = bestvaluefoundiniteration;
	}

	/**
	 * @return the mOperationsCompTimes
	 */
	public int[][] getOperationsBeginTimes() {
		return operationsBeginTimes;
	}

	public void setOperationsBeginTimes(final int job, final int op, final int time) {
		operationsBeginTimes[job][op] = time;
	}

	/**
	 * @return the mOperationsMachineAssignments
	 */
	public int[][] getOperationsMachineAssignments() {
		return operationsMachineAssignments;
	}

	public void setOperationsMachineAssignments(final int job, final int op, final int time) {
		operationsMachineAssignments[job][op] = time;
	}

	/**
	 * @return the mSubgradients
	 */
	public int[][] getSubgradients() {
		return subgradients;
	}

	public double[][] getMultipliers() {
		return multipliers;
	}
}
