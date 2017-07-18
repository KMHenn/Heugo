package heugo;

import java.util.HashMap;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Domain;
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
 * @version 7.18.17
 */
public class Heugo extends AbstractNegotiationParty {
	private final static double RESERVATION_UTILITY = 0.65; // Arbitrary reservation value.
	private Bid lastPartnerBid; // Last bid made by the opponent.
	private static HashMap <String, Bid> heugoPastActions = new HashMap<>(); // Track the past actions made by Heugo to ensure no repeats.
	private OpponentModel opponent;
	public Domain domain;
	
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
		domain = utilitySpace.getDomain();
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
		System.out.println("\n\n---------\n\n");
		double lastPartnerBidUtility;
		double time = timeline.getTime();
		
		
		try{
			if ((validActions.size() == 2) && (!validActions.contains(Accept.class))){ // First proposal
				Bid firstBid = utilitySpace.getMaxUtilityBid();
				updateHashMap(firstBid);
				return new Offer(getPartyId(), firstBid);
			}
			
			else{ //All other proposals
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
			e.printStackTrace();
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
		Bid newBid;
		double newBidUtility;
		double opponentMean = opponent.getMean();
		double opponentSD = opponent.getStandardDeviation();
		double minUtil = 1 - (opponentMean + opponentSD);
		double maxUtil = 1 - (opponentMean - opponentSD);
		
		newBid = utilitySpace.getMaxUtilityBid();
		newBidUtility = getUtility(newBid);
		
		System.out.println("Max Bid Util: " + newBidUtility);
		
		System.out.println("minUtil: " + minUtil);
		System.out.println("maxUtil: " + maxUtil);
		
		while ((newBidUtility < minUtil) || (newBidUtility >= maxUtil)){
			newBid = generateRandomBid();
			newBidUtility = getUtility(newBid);
		}
		
		System.out.println("Bidding utility: " + newBidUtility);
		
		updateHashMap(newBid);
		return newBid;
		
		

	}
	
	/**
	 * Update the hash map, generating a String key based on the bid.
	 * 
	 * @param bid
	 */
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
	 * @throws Exception 
	 */
	public double getThreshold(double time) throws Exception{
		double threshold = 1.0;
		
		if ((time >=0) && (time <= 0.25)){
			threshold = 1.0 - (time / 10);
		}
		
		else if ((time > 0.25) && (time <= 0.5)){
			threshold = 1.0 - (time / 5);
		}
		
		else if ((time > 0.5) && (time <= 0.75)){
			threshold = 0.9 - Math.pow(time, (1 / 0.1));
		}
		
		else if ((time > 0.75) && (time <= 0.90)){
			threshold = 1.66 - Math.pow(time, (1 / 1.5));
		}
		
		else{
			threshold = RESERVATION_UTILITY;
		}
	
		return threshold;
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
	 * @throws Exception 
	 */
	public boolean isAcceptable(double utility, double time) throws Exception{
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