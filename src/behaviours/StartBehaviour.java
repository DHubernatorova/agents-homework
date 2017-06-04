package behaviours;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import agents.RunnerAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * StartBehaviour is used by the representative of each team before the start of running. It
 * receives the start message from ExperimentMasterAgent with the start time and number of laps
 * and triggers the RunnerBehaviour at passed start time.
 */
public class StartBehaviour extends SimpleBehaviour {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = -8974966583277442879L;

	private boolean isInitialized;
	private MessageTemplate experimentMasterMT;
	private Pattern pattern;
	private Date startTime;

	public StartBehaviour() {
		super();
		this.isInitialized = false;
		this.experimentMasterMT = MessageTemplate.and(MessageTemplate.MatchSender(new AID("EMA", AID.ISLOCALNAME)),
				MessageTemplate.MatchConversationId("start"));
		this.pattern = Pattern.compile("(\\d+);(\\d+)"); // "startTime;numLaps"
	}

	@Override
	public void action() {
		// Listen to START message from ExperimentMasterAgent
		ACLMessage msg = myAgent.receive(experimentMasterMT);
		if (msg != null) {
			Matcher m = pattern.matcher(msg.getContent());
			if (m.find()) {
				// Confirm receiving of start message
				ACLMessage confMsg = new ACLMessage(ACLMessage.INFORM);
				confMsg.setConversationId("start");
				confMsg.addReceiver(msg.getSender());
				myAgent.send(confMsg);
				// Get start time
				startTime = new Date(Long.parseLong(m.group(1)));
				// Get number of laps
				int numLaps = Integer.parseInt(m.group(2));
				((RunnerAgent) myAgent).setNumOfLaps(numLaps);
				logger.info(myAgent.getLocalName() + ": Start message received: (" + startTime.toString() + ";" + numLaps
						+ ")");
				// Start running at agreed time
				new Timer().schedule(new TimerTask() {
					public void run() {
						int step = 1;
						logger.info(myAgent.getLocalName() + ":START RUNNING!");
						Behaviour runnerBehaviour = new RunnerBehaviour(step);
						myAgent.addBehaviour(runnerBehaviour);// representative start running
					}
				}, startTime);
				isInitialized = true;
			}
		} else {
			block();
		}
	}

	@Override
	public boolean done() {
		return isInitialized;
	}
}