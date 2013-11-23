package com.iullui.domain.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Item Ad insert or update message 
 * @author Yuval
 */
public class VItemAdUpsert {
	
	public class VItemAdAddUpdateParent { 
		String id;
		Boolean active;
		
		public String getId() { 
			return this.id;
		}
		
		public void setId(String id) { 
			this.id = id;
		}
		
		public Boolean getActive() { 
			return this.active;
		}
		
		public void setActive(Boolean active) { 
			this.active = active;
		}
		
	}
	
	String itemId;
	Boolean active;
	List<VItemAdAddUpdateParent> parents = new ArrayList<VItemAdAddUpdateParent>();
	
	public String getItemId() {
		return itemId;
	}
	
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public List<VItemAdAddUpdateParent> getParents() {
		return parents;
	}
	
	public void setParents(List<VItemAdAddUpdateParent> parents) {
		this.parents = parents;
	}
	
}
