package com.iullui.domain.view;

import java.util.HashSet;
import java.util.Set;

import com.iullui.domain.data.Item;


/**
 * Includes the child and votes per given parent
 * @author Yuval
 */
public class VMatchChild extends Item {

	Integer votesCount; // indicates how many votes the child has with relation to its parent
	Boolean userVoted; // indicates whether the current user has voted for this recommendation
	Set<VMatchUser> friends = new HashSet<VMatchUser>();
	Set<VMatchUser> sampleVoters = new HashSet<VMatchUser>();
	String media;
	String comment;
	
	public VMatchChild() { }

	public VMatchChild(Item item) {
		this.setId(item.getId());
		this.setTitle(item.getTitle());
		this.setUrl(item.getUrl());
		this.setImg(item.getImg());
		this.setDescription(item.getDescription());
		this.setVotesCount(0);
		this.setUserVoted(false);
		this.setMedia(item.getMedia());
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
	
	public Set<VMatchUser> getFriends() {
		return this.friends;
	}
	
	public void setFriends(Set<VMatchUser> friends) { 
		this.friends = friends;
	}
	
	public Set<VMatchUser> getSampleVoters() {
		return this.sampleVoters;
	}
	
	public void setSampleVoters(Set<VMatchUser> sampleVoters) { 
		this.sampleVoters = sampleVoters;
	}
	
	public String getMedia() { 
		return this.media;
	}
	
	public void setMedia(String media) { 
		this.media = media;
	}
	
	public String getComment() { 
		return this.comment;
	}
	
	public void setComment(String comment) { 
		this.comment = comment;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Item item) {
		VMatchChild matchChild = (VMatchChild) item;
		if (!this.getVotesCount().equals(matchChild.getVotesCount())) {
			return this.getVotesCount() - matchChild.getVotesCount();
		}
		else {
			return (this.getImg() != null ? -1 : 0);
		}
	}
	
}
