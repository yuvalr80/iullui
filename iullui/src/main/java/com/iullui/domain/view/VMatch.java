package com.iullui.domain.view;

import java.util.Date;

import com.iullui.domain.data.Item;


/**
 * Includes the child and votes per given parent
 * @author Yuval
 */
public class VMatch implements Comparable<VMatch> {

	Item parent;
	Item child;
	String userId;
	String name;
	String comment;
	Date timestamp;
	
	public VMatch() { }

	
	public Item getParent() {
		return parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}

	public Item getChild() {
		return child;
	}

	public void setChild(Item child) {
		this.child = child;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(VMatch vm) {
		return this.getTimestamp().compareTo(vm.getTimestamp());
	}
	
	
}
