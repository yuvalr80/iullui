package com.iullui.domain.data;

import java.util.ArrayList;
import java.util.List;


/**
 * Includes the votes data per match
 * @author Yuval
 */
public class VotedMatch extends Match {

	Integer votesCount; // indicates how many votes the child has with relation to its parent
	Boolean userVoted; // indicates whether the current user has voted for this recommendation
	List<String> voterIds = new ArrayList<String>(); // all the users who voted for this child
	
	public VotedMatch(String parentId, Integer votesCount) {
		this.setParentId(parentId);
		this.setVotesCount(votesCount);
	}
	
	public Integer getVotesCount() {
		return votesCount;
	}
	
	public void setVotesCount(Integer votesCount) {
		this.votesCount = votesCount;
	}
	
	public Boolean getUserVoted() { 
		return userVoted;
	}
	
	public void setUserVoted(Boolean userVoted) { 
		this.userVoted = userVoted;
	}
	
	public List<String> getVoterIds() { 
		return this.voterIds;
	}
	
	public void setVoterIds(List<String> voterIds) { 
		this.voterIds = voterIds;
	}
	
}
