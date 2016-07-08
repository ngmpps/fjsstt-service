package at.ngmpps.fjsstt.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ngmpps.fjsstt.model.FJSSTTproblem.Objective;

/**
 * Implements a solution for a one job scheduling problem. Basically it consists
 * of 2 arrays, (1) for the assignment of machines to operations, (2) the
 * beginning times offsets of operations. Furthermore it declares an error code,
 * indicating if the neighbour operation calculating the solution returned an
 * error.
 * 
 * @author ahaemm
 * 
 */
public class SubproblemSolution {

	/**
	 * Logger configuration in NgMPPS/src/logback.xml, e.g. to switch between
	 * info / debug level.
	 */
	final static Logger mLogger = LoggerFactory.getLogger(SubproblemSolution.class);

	SubproblemInstance mSubproblem;

	/**
	 * Integer array storing the assignment of machines to operations. Array
	 * indices are operations, entries are machines.
	 */
	int[] machineAssignments;

	/**
	 * Integer array storing the beginning times offsets of operations. Array
	 * indices are operations, entries are time offsets from 0.
	 */
	int[] beginningTimesOffsets;

	/**
	 * Might be useful for neighbor solutions to indicate if the neighbor
	 * operation was successful. The default value is 0.
	 */
	int errorCode;

	/**
	 * Creates a solution object with empty arrays for machine assignments and
	 * beginning times, and with default error code 0.
	 * 
	 * @param subproblem
	 */
	public SubproblemSolution(final SubproblemInstance subproblem) {
		mSubproblem = subproblem;
		machineAssignments = new int[subproblem.getOperations()];
		beginningTimesOffsets = new int[subproblem.getOperations()];
		errorCode = 0;
	}

	/**
	 * Create a solution object with default error code 0. Deep copy of parameter
	 * arrays.
	 * 
	 * @param assignments
	 * @param times
	 */
	public SubproblemSolution(final SubproblemInstance problem, final int[] assignments, final int[] times) {
		mSubproblem = problem;
		machineAssignments = new int[assignments.length];
		beginningTimesOffsets = new int[times.length];
		System.arraycopy(assignments, 0, machineAssignments, 0, assignments.length);
		System.arraycopy(times, 0, beginningTimesOffsets, 0, times.length);
		errorCode = 0;
	}

	/**
	 * Deep copy of parameter arrays.
	 * 
	 * @param assignments
	 * @param times
	 * @param errorCode
	 */
	public SubproblemSolution(final SubproblemInstance problem, final int[] assignments, final int[] times, final int errorCode) {
		mSubproblem = problem;
		machineAssignments = new int[assignments.length];
		beginningTimesOffsets = new int[times.length];
		System.arraycopy(assignments, 0, machineAssignments, 0, assignments.length);
		System.arraycopy(times, 0, beginningTimesOffsets, 0, times.length);
		this.errorCode = errorCode;
	}

	/**
	 * Calculates the cost of a solution for the one job scheduling problem. This
	 * is the sum of the objective function (completion time or tardiness) plus
	 * machine usage cost.
	 * 
	 * @param solution
	 *           The solution to be evaluated.
	 * 
	 * @return The solution cost.
	 */
	public double calcCost(final Objective objective) {
		double cost = 0;

		// calculate the cost related to the objective function, assuming an
		// offset 0 for the beginning times
		cost += mSubproblem.calcObjectiveValue(objective, beginningTimesOffsets[mSubproblem.getOperations() - 1],
				machineAssignments[mSubproblem.getOperations() - 1]);

		// calculate the cost related to machine usage
		for (int op = 0; op < mSubproblem.getOperations(); op++) {
			final int opMachine = this.getMachineAssignments()[op];
			final int opCompletionTime = beginningTimesOffsets[op] + mSubproblem.getProcessTimes()[op][opMachine] - 1;
			for (int t = beginningTimesOffsets[op]; t <= opCompletionTime; t++) {
				cost += mSubproblem.getMultipliers()[opMachine][t];
			}
		}
		return cost;
	}

	/**
	 * Creates a bid object from the subproblem solution.
	 * 
	 * @return The bid.
	 */
	public Bid convertToBid() {

		final Bid bid = new Bid(mSubproblem.getJobID(), this.calcCost(mSubproblem.getObjective()), machineAssignments, beginningTimesOffsets);

		// loop over operations
		for (int j = 0; j < mSubproblem.getOperations(); j++) {

			// the optimal machine for operation j
			final int optMachine = this.getMachineAssignments()[j];

			// loop over occupied time slots
			for (int t = beginningTimesOffsets[j]; t <= this.beginningTimesOffsets[j] + mSubproblem.getProcessTimes()[j][optMachine]
					- 1; t++) {
				final int[] tuple = { optMachine, t };
				bid.getOccupiedTimeSlots().add(tuple);
			}
		}
		return bid;
	}

	public int[] getBeginningTimesOffsets() {
		return beginningTimesOffsets;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public int[] getMachineAssignments() {
		return machineAssignments;
	}

	/**
	 * Simply print the solution to the console.
	 * 
	 * @param name
	 *           An optional name of the solution (e.g. "initial", "random").
	 */
	public void print(String name) {
		mLogger.debug("Printing the solution \"" + name + "\" for subproblem / job with index " + mSubproblem.getJobID());
		mLogger.debug("operation, machine");
		for (int i = 0; i < mSubproblem.operations; i++) {
			mLogger.debug(i + ", " + machineAssignments[i]);
		}
		mLogger.debug("operation, beginning time, completion time");
		for (int i = 0; i < mSubproblem.getOperations(); i++) {
			final int completionTime = beginningTimesOffsets[i] + mSubproblem.getProcessTimes()[i][machineAssignments[i]] - 1;
			mLogger.debug(i + ", " + beginningTimesOffsets[i] + ", " + completionTime);
		}

	}

	public void setBeginningTimesOffsets(final int[] mBeginningTimesOffsets) {
		this.beginningTimesOffsets = mBeginningTimesOffsets;
	}

	public void setErrorCode(final int mErrorCode) {
		this.errorCode = mErrorCode;
	}

	public void setMachineAssignments(final int[] mMachineAssignments) {
		this.machineAssignments = mMachineAssignments;
	}

}
