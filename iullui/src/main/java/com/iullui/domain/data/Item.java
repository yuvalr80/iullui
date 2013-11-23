package com.iullui.domain.data;

import static com.iullui.common.Util.isEmpty;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.social.facebook.api.Page;

@Document(collection="items")
public class Item implements Comparable<Item> {

	public static final String NS_FACEBOOK = "fb-";
	public static final String NS_YOUTUBE = "yt-";

	public static final String ITEM_IMAGE_PLACEHOLDER = "/images/placeholder_icon.png";
	public static final String FACEBOOK_GRAPH_URL = "https://graph.facebook.com/";
	
	@Id String id;
	@Indexed(name="idxTitle", unique=true, sparse=true) String title;
	String url;
	String img;
	String media;
	String description;
	String ownerId;
	
	private Boolean hideOwnerId = true;
	
	public Item() { };
	
	public static String normalizeWikiText(String text) {
		return text.replaceAll("\\(.*\\)", "").replaceAll("\\[.*\\]", "")
				.replace(" , ", " ").replace(" ,", ",").replace(" .", ".")
				.replace(" ;", ";").replace("  ", " ");
	}
	
	// parse the item json and remove special wiki characters
	public Item(JSONObject json) { 
		JSONObject title = json.getJSONObject("Text");
		JSONObject url = json.getJSONObject("Url");
		JSONObject img = json.getJSONObject("Image");
		JSONObject desc = json.getJSONObject("Description");
		
		this.setTitle(title.getString("text"));
		this.setUrl(url.getString("text"));
		
		try { this.setImg(img.getString("source")); }
		catch (JSONException jsonEx) {	} // image is n/a
		
		try {
			String normalizedDesc = normalizeWikiText(desc.getString("text"));
			this.setDescription(normalizedDesc);
		}
		catch (JSONException jsonEx) {	} // description is n/a
	}
	
	// convert a facebook page to an item
	public Item(Page page) {
		this.setId(NS_FACEBOOK + page.getId());
		this.update(page);
	}
	
	public void update(Page page) { 
		this.setTitle(page.getName());
		this.setUrl(page.getLink());
		this.setImg(FACEBOOK_GRAPH_URL.concat(page.getId()).concat("/picture?type=large"));
		String desc = page.getAbout();
		if (isEmpty(desc)) desc = page.getCompanyOverview();
		if (isEmpty(desc)) desc = page.getDescription();
		if (isEmpty(desc)) desc = page.getCategory();
		this.setDescription(desc);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) { 
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getImg() {
		return this.img;
	}
	
	public void setImg(String img) {
		this.img = img;
	}
	
	public String getMedia() {
		return media;
	}
	
	public void setMedia(String media) {
		this.media = media;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwnerId() {
		return (this.hideOwnerId ? null : ownerId);
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setHideOwnerId(Boolean hideOwnerId) {
		this.hideOwnerId = hideOwnerId;
	}
	
	public Boolean isFacebook() { 
		return (!isEmpty(this.getId()) && this.getId().startsWith(NS_FACEBOOK));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Item)) return false;
		return (this == obj || this.getId().equals(((Item) obj).getId()));
	}
	
	@Override
	public int hashCode() { 
		return this.getId().hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Item item) {
		if (this.isFacebook() && !item.isFacebook()) return -1;
		else if ((item.isFacebook() && !this.isFacebook())) return 1;
		else if (this.getTitle().length() != item.getTitle().length()) 
			return this.getTitle().length() - item.getTitle().length();
		else return this.getTitle().compareTo(item.getTitle());
	}
	
}
