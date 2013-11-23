package com.iullui.domain.view;

import org.springframework.social.facebook.api.Reference;

public class VMatchUser {

	String id;
	String name;
	String firstName;
	
	public VMatchUser() { }
	
	public VMatchUser(String id) {
		this.setId(id);
	}
	
	public VMatchUser(Reference ref) { 
		this.setId(ref.getId());
		this.setName(ref.getName());
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return this.firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public boolean equals(Object obj) { 
		if (obj == null || !(obj instanceof VMatchUser)) return false;
		if (obj == this) return true;
		return (((VMatchUser) obj).getId().equals(this.getId()));
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	
}
