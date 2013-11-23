package com.iullui.domain.data;

import java.util.Date;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Includes the vote data (user, timestamp...) per match 
 * @author Yuval
 */
public class Vote extends Match {

	@Indexed(name="idxUser") String userId;
	@Indexed(name="idxTimestamp", direction = IndexDirection.DESCENDING) Date timestamp;
	String comment;
	
	public Vote(String parentId, String childId, String userId) {
		this.setParentId(parentId);
		this.setChildId(childId);
		this.setUserId(userId);
		this.setTimestamp(new Date());
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) { 
		this.timestamp = timestamp;
	}
	
	public String getComment() { 
		return comment;
	}
	
	public void setComment(String comment) { 
		this.comment = comment;
	}

	
}
