package agents;

import behaviours.RunnerBehaviour;
import behaviours.StartBehaviour;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import jade.wrapper.ControllerException;
import util.AgentsUtils;

/**
 * RunnerAgent has two statuses:
 * 1 LocalAgent: await for the previous runnerAgent to reach its location.
 * 2 RunnerAgent: run from its location to following localAgent location.
 * 
 * Parameters:
 * 1. isRepresentative: true|false - whether the agent represents the team on the first machine.
 * 2. targetAgent: agentName - the agent that it has to reach.
 */
public class RunnerAgent extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 7941245165294941476L;

	private Behaviour runnerBehaviour;
	private boolean isRepresentative;
	private String targetAgent;
	private String originLocation;
	private int numLaps;
	private int completedLaps;

	@Override
	protected void setup() {
		// Get arguments (isRepresentative, targetAgent)
		Object[] args = getArguments();
		if (args != null && args.length == 2) {
			isRepresentative = ((String) args[0]).equalsIgnoreCase("true");			
			targetAgent = (String) args[1];
			if (targetAgent != null) {
				logger.info("Runner " + getLocalName() + " (Rep: " + isRepresentative + "). Target: " + targetAgent);
			} else {
				logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Incorrect target agent");
				doDelete();
			}
		} else {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Incorrect number of arguments");
			doDelete();
		}
		// Register agent in yellow pages
		String type = isRepresentative ? "RArep" : "RA";
		AgentsUtils.registerAgent(this, type);
		// Save origin location
		try {
			originLocation = getContainerController().getContainerName();
		} catch (ControllerException e) {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot get container name");
		}
		// Initiate laps counter
		completedLaps = 0;
		int step = 0;
		// Register content language and mobility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		// Add behavior
		if (isRepresentative) {
			addBehaviour(new StartBehaviour());
		} else {
			addBehaviour(new RunnerBehaviour(step));
		}
	}

	@Override
	protected void afterMove() {
		// Get new location
		String newLocation = null;
		try {
			newLocation = getContainerController().getContainerName();
		} catch (ControllerException e) {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot get container name");
		}
		logger.info(getLocalName() + ": Reach new location: " + newLocation);
		if (isRepresentative) {
			if (newLocation.equals(originLocation)) { // on the start place
				// If is the origin -> one lap completed
				completedLaps++;
				logger.info(getLocalName() + ": New lap completed!. " + completedLaps + "/" + numLaps);
				if (completedLaps >= numLaps) {
					// All laps completed
					logger.info(getLocalName() + ": All laps are completed.");
					// Send completion message to Experiment master and finish
					ACLMessage compMsg = new ACLMessage(ACLMessage.INFORM);
					compMsg.setConversationId("completion");
					compMsg.addReceiver(new AID("ExperimentMasterAgent", AID.ISLOCALNAME));
					send(compMsg);
					// Restart behaviors (Remove RunnerBehaviour and add StartBehaviour)
					// start once again
					originLocation = newLocation;
					removeBehaviour(runnerBehaviour);
					addBehaviour(new StartBehaviour());
					return;
				}
			}
		}
		// Register content language and mobility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		// Send message to local agent to start running
		ACLMessage runMsg = new ACLMessage(ACLMessage.REQUEST);
		runMsg.setConversationId("running");
		runMsg.addReceiver(getTargetAgent());
		send(runMsg);
		logger.info(getLocalName() + ": Relay given");
		// Wait for answer
		MessageTemplate mtRunner = MessageTemplate.MatchConversationId("running");
		ACLMessage msg;
		do {
			msg = receive(mtRunner);
		} while (msg == null);
	}

	public AID getTargetAgent() {
		//Get Agent AID
		SearchConstraints sC = new SearchConstraints();
		sC.setMaxResults(new Long(-1));
		AMSAgentDescription [] agents;
		try { 
			agents = AMSService.search(this, new AMSAgentDescription(), sC);	
			for (int i = 0; i < agents.length; i++) {
				if (agents[i].getName().getLocalName().equals(targetAgent))
					return agents[i].getName();
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void setNumOfLaps(int num) {
		this.numLaps = num;
	}

	public void setRunnerBehaviour(Behaviour b) {
		this.runnerBehaviour = b;
	}
}