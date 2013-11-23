package com.iullui.domain.view;

import com.iullui.domain.data.Item;

public class VSearchedItem implements Comparable<VSearchedItem> {

	protected Item item;
	protected String search;
	
	public VSearchedItem(Item item, String search) {
		this.setItem(item);
		this.setSearch(search.toLowerCase());
	}
	
	public Item getItem() { 
		return this.item;
	}
	
	public void setItem(Item item) { 
		this.item = item;
	}
	
	public String getSearch() { 
		return this.search;
		
	}
	public void setSearch(String search) { 
		this.search = search;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(VSearchedItem searchedItem) {
		String title = this.getItem().getTitle().toLowerCase();
		String otherTitle = searchedItem.getItem().getTitle().toLowerCase();
		if 		(title.equals(search) && !otherTitle.equals(search)) return -1;
		else if (otherTitle.equals(search) && !title.equals(search)) return 1;
		else if (title.startsWith(search) && !otherTitle.startsWith(search)) return -1;
		else if (otherTitle.startsWith(search) && !title.startsWith(search)) return 1;
		else if (title.contains(search) && !otherTitle.contains(search)) return -1;
		else if (otherTitle.contains(search) && !title.contains(search)) return 1;
		else return this.getItem().compareTo(searchedItem.getItem());
	}
	
}
