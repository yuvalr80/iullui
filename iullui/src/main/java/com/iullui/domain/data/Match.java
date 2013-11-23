package com.iullui.domain.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="votes")
@CompoundIndexes(value={
	@CompoundIndex(name="idxUserParentChild", unique=true,
		def="{'parentId' : 1, 'childId' : 1, 'userId' : 1}")})	
public abstract class Match {

	@Id	String id;
	String parentId;
	String childId;

	public String getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getChildId() {
		return childId;
	}
	
	public void setChildId(String childId) {
		this.childId = childId;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Vote)) return false;
		Vote vote = (Vote) obj;
		if (this.getId() != null && vote.getId() != null) return (this.getId().equals(vote.getId())); 
		return false;
	}
}
