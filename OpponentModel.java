package heugo;

import java.util.HashMap;

import negotiator.Bid;
import negotiator.parties.NegotiationInfo;
/**
 * 
 * Modeling the opponent based on previously gleaned information.
 * 
 * @author Kaitlyn
 *
 */
public class OpponentModel{
	protected HashMap<Integer, Bid> opponentBids;
	protected HashMap<Integer, Double> opponentUtilities;
	protected int numBids;
	protected double mean;
	protected double standardDeviation;
	protected Bid lastBid;
	protected NegotiationInfo info;
	
	/**
	 * Constructor that initializes variables unique to the specific opponent.
	 * 
	 * @param info
	 */
	public OpponentModel (NegotiationInfo info){
		this.info = info;
		opponentBids = new HashMap<>();
		opponentUtilities = new HashMap<>();
		mean = 0;
		standardDeviation = 0;
	}
	
	/**
	 * Update the HashMap of opponent bids.
	 * 
	 * @param bid
	 */
	protected void addToOpponentBids(Bid bid){
		numBids ++;
		String bidString = bid.toString();
		int bidInt = Integer.parseInt(bidString);
		lastBid = bid;
		opponentBids.put(bidInt, bid);
	}
	
	/**
	 * Update the HashMap of opponent utilities.
	 * 
	 * @param bid
	 * @param bidInt
	 */
	protected void addToOpponentUtilities(Bid bid, int bidInt){
		double util = info.getUtilitySpace().getUtility(bid);
		opponentUtilities.put(bidInt, util);
	}
	
	/**
	 * Update the mean to reflect the new data set.
	 */
	protected void updateMean(){
		double sum = 0.0;
		
		for (double utility : opponentUtilities.values())
			sum += utility;
		
		mean = sum / numBids;
	}
	
	/**
	 * Update the standard deviation to reflect the new data set.
	 */
	protected void updateStandardDeviation(){
		double sd = 0.0;
		double squaredDifference = 0.0;
		
		for (double utility : opponentUtilities.values())
			squaredDifference += Math.pow((utility - mean), 2);
		
		sd = squaredDifference / (numBids - 1);
		sd = Math.sqrt(sd);
	}
	
	/**
	 * Return the current mean.
	 * 
	 * @return mean
	 */
	public double getMean(){
		return mean;
	}
	
	/**
	 * Return the current standard deviation.
	 * 
	 * @return standard deviation
	 */
	public double getStandardDeviation(){
		return standardDeviation;
	}
	
	/**
	 * Return the last bid made by the opponent.
	 * 
	 * @return last bid made.
	 */
	public Bid getLastBid(){
		return lastBid;
	}
}
