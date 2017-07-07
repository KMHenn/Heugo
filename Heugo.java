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

public class Heugo extends AbstractNegotiationParty {
	private static double alpha = .30769;
	private Bid lastPartnerBid;
	private static HashMap <Integer, Bid> heugoPastActions = new HashMap<>();
	
	@Override
	public void init(NegotiationInfo info){
		//TODO
		super.init(info);
		lastPartnerBid = null;
	}
	
	@Override
	public void receiveMessage(AgentID sender, Action action){
		super.receiveMessage(sender, action);
		
		if (action instanceof Offer)
			lastPartnerBid = ((Offer) action).getBid();
	}
	
	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		// TODO Auto-generated method stub
		if ((validActions.size() == 2) && (!validActions.contains(Accept.class)))
			//return offer
		else{
			//if second round
			//else (all other rounds)
		}
		return null;
	}

	
	public double getThreshold(double time){
		return (1.0 - Math.pow(time,  (1 / alpha)));
	}
	
	
	@Override
	public String getDescription() {
		return "Heugo";
	}
	
	

}
