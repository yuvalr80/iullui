package com.iullui.domain.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.iullui.domain.view.VItemAdUpsert;
import com.iullui.domain.view.VItemAdUpsert.VItemAdAddUpdateParent;

/**
 * An Ad for promoting items 
 * @author Yuval
 */
@Document(collection="ads")
@CompoundIndexes(value={
		@CompoundIndex(name="idxUserItem", unique=true,
			def="{'itemId' : 1, 'userId' : 1}")})	
public class ItemAd {

	public class ItemAdParent {
		String id;
		Integer views;
		Integer clicks;
		Boolean active;
		Date additionTimestamp;
		Date modificationTimestamp;
		
		public ItemAdParent() { }
		
		public ItemAdParent(VItemAdAddUpdateParent itemAdAddUpdateParent) {
			this.setId(itemAdAddUpdateParent.getId());
			this.setViews(0);
			this.setClicks(0);
			this.setActive(true);
			this.setAdditionTimestamp(new Date());
			this.setModificationTimestamp(new Date());
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public Integer getViews() {
			return views;
		}
		
		public void setViews(Integer views) {
			this.views = views;
		}
		
		public Integer getClicks() {
			return clicks;
		}
		
		public void setClicks(Integer clicks) {
			this.clicks = clicks;
		}
		
		public Boolean getActive() {
			return this.active;
		}
		
		public void setActive(Boolean active) { 
			this.active = active;
		}

		public Date getAdditionTimestamp() {
			return additionTimestamp;
		}

		public void setAdditionTimestamp(Date additionTimestamp) {
			this.additionTimestamp = additionTimestamp;
		}

		public Date getModificationTimestamp() {
			return modificationTimestamp;
		}

		public void setModificationTimestamp(Date modificationTimestamp) {
			this.modificationTimestamp = modificationTimestamp;
		}
		
		@Override
		public boolean equals(Object itemAdParent) { 
			if (itemAdParent == null || !(itemAdParent instanceof ItemAdParent)) return false;
			if (itemAdParent == this) return true;
			return this.getId().equals(((ItemAdParent) itemAdParent).getId());
		}
	}
	
	
	@Id String id;
	String userId;
	String itemId;
	List<ItemAdParent> parents = new ArrayList<ItemAdParent>(); 
	Integer views;
	Integer clicks;
	Money cpv;
	Money cpr;
	Money cpc;
	Boolean active;
	Date additionTimestamp;
	Date modificationTimestamp;
	
	public ItemAd() { }
	
	public ItemAd(String userId, VItemAdUpsert itemAdAddUpdate, Money cpv, Money cpr, Money cpc) {
		this.setUserId(userId);
		this.setClicks(0);
		this.setViews(0);
		this.setCpv(cpv);
		this.setCpr(cpr);
		this.setCpc(cpc);
		this.setAdditionTimestamp(new Date());
		this.setModificationTimestamp(new Date());
		this.setActive(true);
		for (VItemAdAddUpdateParent itemAdAddUpdateParent : itemAdAddUpdate.getParents()) {
			this.getParents().add(new ItemAdParent(itemAdAddUpdateParent));
		}
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public List<ItemAdParent> getParents() {
		return parents;
	}
	
	public void setParents(List<ItemAdParent> parents) {
		this.parents = parents;
	}
	
	public Integer getViews() {
		return views;
	}
	
	public void setViews(Integer views) {
		this.views = views;
	}
	
	public Integer getClicks() {
		return clicks;
	}
	
	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}

	public Money getCpv() {
		return this.cpv;
	}
	
	public void setCpv(Money cpv) {
		this.cpv = cpv;
	}
	
	public Money getCpr() {
		return this.cpr;
	}
	
	public void setCpr(Money cpr) {
		this.cpr = cpr;
	}
	
	public Money getCpc() {
		return this.cpc;
	}
	
	public void setCpc(Money cpc) {
		this.cpc = cpc;
	}

	public Boolean getActive() {
		return this.active;
	}
	
	public void setActive(Boolean active) { 
		this.active = active;
	}

	public Date getAdditionTimestamp() {
		return additionTimestamp;
	}

	public void setAdditionTimestamp(Date additionTimestamp) {
		this.additionTimestamp = additionTimestamp;
	}

	public Date getModificationTimestamp() {
		return modificationTimestamp;
	}

	public void setModificationTimestamp(Date modificationTimestamp) {
		this.modificationTimestamp = modificationTimestamp;
	}

}
