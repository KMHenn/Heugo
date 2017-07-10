package heugo;

import java.util.HashMap;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

/**
 * 
 * Main class for the agent.
 * 
 * @author Kaitlyn
 *
 */
public class Heugo extends AbstractNegotiationParty {
	private static double alpha = .30769;
	private Bid lastPartnerBid;
	private static HashMap <String, Bid> heugoPastActions = new HashMap<>();
	private OpponentModel opponent;
	
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
		double lastPartnerBidUtility;
		double time = timeline.getTime();
		
		
		try{
			if ((validActions.size() == 2) && (!validActions.contains(Accept.class))){ // First proposal
				Bid firstBid = utilitySpace.getMaxUtilityBid();
				return new Offer(getPartyId(), firstBid);
			}
			
			else{ // TODO All other proposals
				lastPartnerBidUtility = getUtility(lastPartnerBid);
				
				if(isAcceptable(lastPartnerBidUtility, time))
					return new Accept(getPartyId(), lastPartnerBid);
				
				do{
					
					opponent.addToOpponentBids(lastPartnerBid);
					Bid newBid = generateBid(time);
					return new Offer(getPartyId(), newBid);
				}
				while (!isAcceptable(lastPartnerBidUtility, time));
			}
		}
		catch (Exception e){
			System.out.println("Error: " + e);
			return new EndNegotiation(getPartyId());
		}
	}

	/**
	 * Generate a bid, based on the threshold and known information on
	 * the opponent.
	 * 
	 * @param lastPartnerBidUtility
	 * @return
	 * @throws Exception 
	 */
	private Bid generateBid(double time) throws Exception{
		double opponentMean = opponent.getMean();
		double opponentSD = opponent.getStandardDeviation();
		double threshold = getThreshold(time);
		
		Bid newBid = utilitySpace.getMaxUtilityBid();
		double newBidUtility = getUtility(newBid);
		
		if((threshold >= (opponentMean - opponentSD)) && (threshold <= (opponentMean + opponentSD))){
			while(((newBidUtility <= (opponentMean - opponentSD)) || (newBidUtility >= (opponentMean + opponentSD)))
					&& (!heugoPastActions.containsValue(newBid))){
				newBid = generateRandomBid();
				newBidUtility = getUtility(newBid);
			}
		}
		
		updateHashMap(newBid);
		return newBid;
	}
	
	
	private void updateHashMap(Bid bid){
		String bidString = getBidString(bid);
		heugoPastActions.put(bidString, bid);
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
	public String getBidString(Bid bid){
		String bidString = bid.toString();
		return bidString;
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
