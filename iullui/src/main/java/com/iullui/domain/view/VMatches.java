package com.iullui.domain.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.iullui.domain.data.Item;


/**
 * Matches container - contains a parent matches, whether the user voted in one or more, which friends voted and total votes count 
 * @author Yuval
 */
public class VMatches {

	Item parent;
	Boolean userVoted = false; 
	Integer votesCount;
	Set<VMatchUser> friends = new HashSet<VMatchUser>();
	Set<VMatchUser> sampleVoters = new HashSet<VMatchUser>();
	List<VMatchChild> children = new ArrayList<VMatchChild>();
	
	public Item getParent() {
		return parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}

	public List<VMatchChild> getChildren() {
		return children;
	}

	public void setChildren(List<VMatchChild> children) {
		this.children = children;
	}

	public Boolean getUserVoted() {
		return userVoted;
	}

	public void setUserVoted(Boolean userVoted) {
		this.userVoted = userVoted;
	}

	public Set<VMatchUser> getFriends() {
		return friends;
	}

	public void setFriends(Set<VMatchUser> friends) {
		this.friends = friends;
	}
	
	public Set<VMatchUser> getSampleVoters() { 
		return sampleVoters;
	}
	
	public void setSampleVoters(Set<VMatchUser> sampleVoters) { 
		this.sampleVoters = sampleVoters;
	}

	public Integer getVotesCount() {
		return votesCount;
	}

	public void setVotesCount(Integer votesCount) {
		this.votesCount = votesCount;
	} 
	
	
}
