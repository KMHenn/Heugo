package heugo;

import java.util.HashMap;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

/**
 * Main class for the agent.
 * 
 * @author Kaitlyn
 *
 */
public class Heugo extends AbstractNegotiationParty {
	private static double alpha = .30769;
	private Bid lastPartnerBid;
	private static HashMap <Integer, Bid> heugoPastActions = new HashMap<>();
	private OpponentModel opponent;
	private int round = 0;
	
	/**
	 * Initialization at the beginning of the negotiation round.
	 * 
	 * @param info
	 */
	@Override
	public void init(NegotiationInfo info){
		super.init(info);
		lastPartnerBid = null;
		opponent = new OpponentModel(info);
	}
	
	/**
	 * Receive the opponent's action as a message.
	 * 
	 * @param sender
	 * @param action
	 */
	@Override
	public void receiveMessage(AgentID sender, Action action){
		super.receiveMessage(sender, action);
		
		if (action instanceof Offer){
			lastPartnerBid = ((Offer) action).getBid();
			opponent.addToOpponentBids(lastPartnerBid);
		}
	}
	
	
	/**
	 * Decide on the appropriate action to take based on the state of 
	 * the session.
	 * 
	 * @param validActions
	 * 
	 * @return chosen action
	 */
	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		// TODO
		if ((validActions.size() == 2) && (!validActions.contains(Accept.class)))
			//return offer
		else{
			//if second round
			//else (all other rounds)
		}
		return null;
	}

	/**
	 * Get the current threshold, based on the normalized time.
	 * 
	 * @param time
	 * 
	 * @return threshold
	 */
	public double getThreshold(double time){
		return (1.0 - Math.pow(time,  (1 / alpha)));
	}
	
	/**
	 * Get the integer associated with the bid.
	 * Used for HashMap key.
	 * 
	 * @param bid
	 * 
	 * @return integer representation of the bid.
	 */
	public int getIndexInt(Bid bid){
		String bidString = bid.toString();
		int bidInt = Integer.parseInt(bidString);
		return bidInt;
	}
	
	/**
	 * Check whether or not the bid is acceptable based on its relation
	 * to the threshold.
	 * 
	 * @param utility
	 * @param time
	 * 
	 * @return
	 */
	public boolean isAcceptable(double utility, double time){
		double threshold = getThreshold(time);
		
		if(utility >= threshold)
			return true;
		
		return false;
	}
	
	/**
	 * Get the String description of the agent.
	 * 
	 * @return agent name.
	 */
	@Override
	public String getDescription() {
		return "Heugo";
	}
	
	

}
