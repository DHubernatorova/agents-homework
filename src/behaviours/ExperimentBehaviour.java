package behaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import util.AgentsUtils;

/**
 * ExperimentBehaviour is used by ExperimentMasterAgent to run the experiment.
 * 
 * Parameters:
 * 1. numAttempts: total number of attempts to run.
 * 2. initLaps: number of laps in first attempt.
 * 3. step: number of laps to increase in each attempt.
 */
public class ExperimentBehaviour extends SimpleBehaviour {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 6100703780980004688L;

	private static final int DELAY = 10;

	private int numTeams;
	private int numMachines;
	private DFAgentDescription[] representatives;
	private long startTime;
	private long endTime;
	private int numLaps;
	private int attempt;
	private int numMaxAttempts;
	private int step;
	private MessageTemplate startMT;
	private MessageTemplate compMT;
	private int teamsConfirmed;

	public ExperimentBehaviour(int numAttempts, int initLaps, int step) {
		this.attempt = 1;
		this.numLaps = initLaps;
		this.numMaxAttempts = numAttempts;
		this.step = step;
		this.startMT = MessageTemplate.MatchConversationId("start");
		this.compMT = MessageTemplate.MatchConversationId("completion");
	}

	@Override
	public void action() {
		try {
			// Get representatives of all teams (RArep)
			representatives = DFService.search(myAgent, AgentsUtils.getDFD(AgentsUtils.getSD("RArep")));
			// Count number of teams
			numTeams = representatives.length;
			// Get rest of members of the teams (RA)
			DFAgentDescription[] runners = DFService.search(myAgent, AgentsUtils.getDFD(AgentsUtils.getSD("RA")));
			// Count number of machines (one member per machine)
			numMachines = runners.length / numTeams;
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Cannot get runners", e);
		}
		// Send start time and number of laps to team captains
		ACLMessage startMsg = new ACLMessage(ACLMessage.REQUEST);
		startMsg.setConversationId("start");
		for (int i = 0; i < representatives.length; ++i) {
			startMsg.addReceiver(representatives[i].getName());
		}
		startTime = System.currentTimeMillis() + DELAY * 1000;
		startMsg.setContent(startTime + ";" + numLaps);
		myAgent.send(startMsg);
		logger.info("Start message was sent!");
		// Receive confirmations
		teamsConfirmed = 0;
		while (teamsConfirmed != numTeams) {
			ACLMessage confMsg = this.getAgent().receive(startMT);
			if (confMsg != null) {
				teamsConfirmed++;
				logger.info("Teams confirmed: " + teamsConfirmed + "/" + numTeams);
			}
		}
		logger.info("Running experiment...");
		// Receive completion confirmations
		teamsConfirmed = 0;
		while (teamsConfirmed != numTeams) {
			ACLMessage compMsg = this.getAgent().receive(compMT);
			if (compMsg != null) {
				teamsConfirmed++;
				logger.info("Teams completed: " + teamsConfirmed + "/" + numTeams);
			}
		}
		// Get end time
		endTime = System.currentTimeMillis();
		// Print results
		logger.info("Number of machines: " + numMachines);
		logger.info("Number of teams: " + numTeams);
		logger.info("Number of laps: " + numLaps);
		logger.info("Runtime: " + (endTime - startTime));
		// Update variables
		numLaps += step;
		attempt++;
	}

	@Override
	public boolean done() {
		return attempt > numMaxAttempts;
	}
}