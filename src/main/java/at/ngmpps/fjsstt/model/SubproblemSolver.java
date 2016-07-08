package at.ngmpps.fjsstt.model;


/**
 * The interface to be implemented by any algorithm solving the subproblems.
 * 
 * @author ahaemm
 * 
 */
public interface SubproblemSolver {

	Bid solve();

	/**
	 * Get the solver type, implemented types are dynamic programming and variable neighbourhood search. 
	 * 
	 * @return The solver type.
	 */
	SubproblemSolverType getType();

}
