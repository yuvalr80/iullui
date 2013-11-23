package com.iullui.domain.view;

import java.util.ArrayList;
import java.util.List;

import com.iullui.domain.data.Item;
import com.iullui.domain.data.ItemAd;

/**
 * View for Item Ads
 * @author Yuval
 */
public class VItemAd extends ItemAd {

	public class VItemAdParent extends ItemAdParent {
		
		Item item;
		
		public Item getItem() {
			return item;
		}
		
		public void setItem(Item item) { 
			this.item = item;
		}
	}
	
	Item item;
	List<VItemAdParent> vparents = new ArrayList<VItemAdParent>();
	
	public Item getItem() {
		return this.item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public List<VItemAdParent> getVParents() {
		return this.vparents;
	}
	
}
