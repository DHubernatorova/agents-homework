package agents;

import behaviours.ExperimentBehaviour;
import jade.core.Agent;
import jade.util.Logger;

/**
 * ExperimentMasterAgent starts the execution of the experiment, gives the start signal,
 * logs the execution time.
 * 
 * Parameters:
 * 1. numAttempts: total number of attempts to run.
 * 2. initLaps: number of laps in first attempt.
 * 3. step: number of laps to increase in each attempt.
 */
public class ExperimentMasterAgent extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 934303410329286008L;

	private int numAttempts;
	private int initLaps;
	private int step;

	@Override
	protected void setup() {
		// Get arguments (numAttempts, initLaps, step)
		Object[] args = getArguments();
		if (args != null && args.length == 3) {
			numAttempts = Integer.parseInt((String) args[0]);
			initLaps = Integer.parseInt((String) args[1]);
			step = Integer.parseInt((String) args[2]);
			logger.info("Start the experiment (number of attempts:" + numAttempts + ", first attempt laps:" 
			+ initLaps + ", step to increase:" + step);
		} else {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Incorrect number of arguments");
			doDelete();
		}
		// Start experiment
		addBehaviour(new ExperimentBehaviour(numAttempts, initLaps, step));
	}
}