package heugo;

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
 * @version 7.10.17
 */
public class OpponentModel{
	protected HashMap<String, Bid> opponentBids; // HashMap of the bids made by the opponent.
	protected HashMap<String, Double> opponentUtilities; // HashMap of the utilities of the bids made by the opponent.
	protected HashMap<Issue, HashMap<Integer, HashMap<Value, Integer>>> frequencyTracker = null;
	protected int numBids; // Number of total bids made.
	protected double mean; // Mean of the utilities.
	protected double standardDeviation; // Standard deviation of the utilities.
	protected Bid lastBid; // Last bid made by the opponent.
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
		mean = 0.0;
		standardDeviation = 0.0;
	}
	
	/**
	 * Update the HashMap of opponent bids.
	 * 
	 * @param bid
	 */
	protected void addToOpponentBids(Bid bid){
		numBids ++;
		lastBid = bid;
		System.out.println("Updated last bid: " + lastBid); //Debug
		String bidString = bid.toString(); // Convert the bid to a String to use as a key.
		opponentBids.put(bidString, bid);
		
		System.out.println("Adding to Opponent Bids: " + bid + "\nIssue(s): " + bid.getIssues()); // Debug
		
		addToOpponentUtilities(bid, bidString);
		bidTrack(bid);
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
		
		System.out.println("\tNew SD: " + standardDeviation); // Debug
	}
	
	protected void bidTrack(Bid bid){
		HashMap <Integer, Value> currentBid = bid.getValues();
		List<Issue> issueList = bid.getIssues();
		int counter = 0;
		if (frequencyTracker == null){
			numberOfIssues = issueList.size();
			System.out.println("Number of Issues: " + numberOfIssues);
			while (counter < numberOfIssues){
				for (HashMap.Entry<Integer, Value> entry : currentBid.entrySet()){
					frequencyTracker = new HashMap<>();
					HashMap <Value, Integer> innermostMap = new HashMap<>();
					innermostMap.put(entry.getValue(), 1);
					System.out.println("issueList.get(counter): " + issueList.get(counter));
					HashMap<Integer, HashMap<Value, Integer>> innerMap = new HashMap<>();
					innerMap.put(entry.getKey(), innermostMap);
					frequencyTracker.put(issueList.get(counter), innerMap);
					System.out.println("Added to tracker");
					System.out.println("Issue: " + issueList.get(counter)); // For debug
					counter ++;
				}
			}
		}
		else{
			for (HashMap.Entry<Integer, Value> entry : currentBid.entrySet()){
				HashMap <Integer, HashMap<Value, Integer>> innerFrequencyTracker = frequencyTracker.get(issueList.get(counter));
				for(Integer key : innerFrequencyTracker.keySet()){
					HashMap <Value, Integer> innermostFrequencyTracker = innerFrequencyTracker.get(key);
					//TODO Check
					if (innermostFrequencyTracker.containsValue(entry.getValue())){
						innermostFrequencyTracker.put(entry.getValue(), (innermostFrequencyTracker.get(entry.getValue()) + 1));
						innerFrequencyTracker.put(key, innermostFrequencyTracker);
						frequencyTracker.put(issueList.get(counter), innerFrequencyTracker);
					}
					else{
						HashMap<Integer, HashMap<Value, Integer>> innerMap = new HashMap<>();
						HashMap<Value, Integer> innermostMap = new HashMap<>();
						innermostMap.put(entry.getValue(), 1);
						innerMap.put(key,  innermostMap);
						frequencyTracker.put(issueList.get(counter), innerMap);
					}
				}
				counter ++;
			}
		
		}
	}
	
	public Bid getProjectedOptimalBid(Domain domain){ // TODO currently returns a null bid Maybe track things in an arraylist of arrays? with different list for each issue?
		HashMap<Integer, Value> bestBidMap = new HashMap<>();
		Bid newBid = lastBid;
		System.out.println("Best bid (initial): " + lastBid);
		
		for (HashMap.Entry<Issue, HashMap<Integer, HashMap<Value, Integer>>> activeIssue : frequencyTracker.entrySet()){
			HashMap <Integer, HashMap<Value, Integer>> allValues = activeIssue.getValue();
			int bestHashInt = 0;
			HashMap<Value, Integer> bestValueMap = null;
			Value bestValue = null;
			
			for(HashMap.Entry<Integer, HashMap<Value, Integer>> activeValues : allValues.entrySet()){
				HashMap<Value, Integer> values = activeValues.getValue();
				if ((bestHashInt == 0) && (bestValueMap == null)){
					bestHashInt = activeValues.getKey();
					bestValueMap = new HashMap<>();
					bestValueMap = activeValues.getValue();
				}
				else{
					for (Value value : values.keySet()){
						if (bestValue == null)
							bestValue = value;
						else{
							if (values.get(value) > values.get(bestValue))
								bestValue = value;
						}
					}
				}
				bestBidMap.put(bestHashInt, bestValue);
			}
			
		}
		newBid = new Bid(domain, bestBidMap);
		return newBid;
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
