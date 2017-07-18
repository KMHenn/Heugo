package heugo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.NegotiationInfo;
/**
 * 
 * Modeling the opponent based on previously gleaned information.
 * 
 * @author Kaitlyn
 *
 * @version 7.18.17
 */
public class OpponentModel{
	protected HashMap<String, Bid> opponentBids; // HashMap of the bids made by the opponent.
	protected HashMap<String, Double> opponentUtilities; // HashMap of the utilities of the bids made by the opponent.
	protected ArrayList<Double> utilityChanges;
	protected int numBids; // Number of total bids made.
	protected double mean; // Mean of the utilities.
	protected double standardDeviation; // Standard deviation of the utilities.
	protected Bid lastBid; // Last bid made by the opponent.
	protected Bid twoBidsAgo; // Bid made two rounds ago.
	protected int numberOfIssues; // Number of issues in the bid pool.
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
		utilityChanges = new ArrayList<>();
		mean = 0.0;
		standardDeviation = 0.0;
		lastBid = null;
		twoBidsAgo = null;
	}
	
	/**
	 * Update the HashMap of opponent bids.
	 * 
	 * @param bid
	 */
	protected void addToOpponentBids(Bid bid){
		numBids ++;
	
		if (lastBid != null)
			twoBidsAgo = lastBid;
	
		lastBid = bid;
		String bidString = bid.toString(); // Convert the bid to a String to use as a key.
		opponentBids.put(bidString, bid);
		
		addToOpponentUtilities(bid, bidString);
		updateMean();
		updateStandardDeviation();
	}
	
	/**
	 * Update the HashMap of opponent utilities.
	 * 
	 * @param bid
	 * @param bidInt
	 */
	protected void addToOpponentUtilities(Bid bid, String bidString){
		double util = info.getUtilitySpace().getUtility(bid); // Get the utility of the bid.
		opponentUtilities.put(bidString, util);
		
		System.out.println("Adding to Opponent Utilities: " + util); // Debug
	}
	
	/**
	 * Update the mean to reflect the new data set.
	 */
	protected void updateMean(){
		double sum = 0.0; // Hold the running total of the utilities.
		
		for (double utility : opponentUtilities.values())
			sum += utility;
		
		mean = sum / numBids;
		
		System.out.println("\tNew Mean: " + mean); // Debug
	}
	
	/**
	 * Update the standard deviation to reflect the new data set.
	 */
	protected void updateStandardDeviation(){
		double sd = 0.0; // Temporary value for the standard deviation.
		double squaredDifference = 0.0; // Hold the values of (utility - mean)^2
		
		for (double utility : opponentUtilities.values())
			squaredDifference += Math.pow((utility - mean), 2);
		
		sd = squaredDifference / (numBids - 1);
		standardDeviation = Math.sqrt(sd);
		
		System.out.println("\tNew SD: " + standardDeviation);
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
