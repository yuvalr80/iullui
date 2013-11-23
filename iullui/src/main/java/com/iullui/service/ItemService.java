package com.iullui.service;

import static com.iullui.common.Util.isEmpty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.io.IOUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Reference;
import org.springframework.stereotype.Service;

import com.iullui.domain.data.Item;
import com.iullui.domain.data.Match;
import com.iullui.domain.data.VotedMatch;
import com.iullui.domain.view.VMatchChild;
import com.iullui.domain.view.VMatchUser;
import com.iullui.domain.view.VMatches;
import com.iullui.domain.view.VSearchedItem;
import com.iullui.service.MetaMatcher.MetaMatch;

/**
 * Wiki items searcher
 * @author Yuval
 */
@Service
public class ItemService {

	public static final String WIKI_SEARCH_URL = "http://en.wikipedia.org/w/api.php?action=opensearch&search=${search}&limit=${limit}&namespace=0&format=xml";

	public static final String WIKI_COMMONS_URL = "http://upload.wikimedia.org/wikipedia/commons/";
	public static final String WIKI_EN_URL = "http://upload.wikimedia.org/wikipedia/en/";
	public static final String WIKI_PAGE_IDS_URL = "http://en.wikipedia.org/w/api.php?action=query&pageids=${pageids}&format=json";
	public static final String WIKI_PAGE_TITLES_URL = "http://en.wikipedia.org/w/api.php?action=query&titles=${titles}&format=json";

	public static final String WIKI_DISAMBIGUATION = "(disambiguation)";
	public static final String WIKI_MAY_REFER_TO = "may refer to";
	public static final String WIKI_MAY_MEAN = "may mean";
	public static final String WIKI_MAY_STAND_FOR = "may stand for";

	public static final Integer WIKI_DEFAULT_THUMB_SIZE = 180;
	public static final Integer MAX_SAMPLE_VOTERS = 5;
	public static final Integer NTHREDS = 10;
	

	protected final Facebook facebook;
	protected final MongoOperations mongo;
	protected final MetaMatcher metaMatcher;
	
	private XMLSerializer xmlSerializer = new XMLSerializer();


	/**
	 * Callable class for multiple items searching / loading  
	 * @author Yuval
	 */
	protected class ItemsLoader implements Callable<List<Item>> {
		
		protected String search;
		protected Integer limit;
		protected Boolean ids;
		
		public ItemsLoader(String search, Integer limit, Boolean ids) {
			this.search = search;
			this.limit = limit;
			this.ids = ids;
		}
		
		@Override
		public List<Item> call() throws Exception {
			return this.loadItems();
		}
		
		public List<Item> loadItems() throws IOException { 
			URL url = new URL(WIKI_SEARCH_URL
					.replace("${search}", URLEncoder.encode(this.search, "utf-8"))
					.replace("${limit}", this.limit.toString()));

			// not using IOUtils.toString(..) since it does not support utf-8 characters
			InputStream is = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String xml = "", line = "";
		    while ((line = br.readLine()) != null) { xml += line; } 
		    is.close();
		    
		    JSON jsonFromXml = xmlSerializer.read(xml);
		    JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(
		    		jsonFromXml.toString().replaceAll("#", "").replaceAll("@",""));
		    
		    JSONArray itemsJson = new JSONArray();
		    if (!jsonObject.getJSONObject("Section").isNullObject()) {
			    try { 
			    	itemsJson = jsonObject.getJSONObject("Section").getJSONArray("Item"); 
			    }
			    catch (JSONException jsonEx) { // item is not an array
			    	itemsJson.add(jsonObject.getJSONObject("Section").getJSONObject("Item"));
			    } 
		    }
		    
		    List<Item> wikiItems = new ArrayList<Item>();
		    for (int i=0; i<itemsJson.size(); i++) {
		    	if (itemsJson.getJSONObject(i).isNullObject()) continue;
		    	Item item = new Item(itemsJson.getJSONObject(i));
		    	item.setImg(canonizeImageUrl(item.getImg()));
		    	if (!isEmpty(item.getDescription()) && 
		    			(item.getDescription().contains(WIKI_MAY_REFER_TO) ||
		    			item.getDescription().contains(WIKI_MAY_MEAN) ||
		    			item.getDescription().contains(WIKI_MAY_STAND_FOR))
		    		|| item.getTitle().contains(WIKI_DISAMBIGUATION)) continue;
		    	
	        	wikiItems.add(item);
	        }
		   
		    if (this.ids) this.addItemIds(wikiItems);
		    
		    return wikiItems;
		}
	    
		/**
		 * Adds ids to items in the list
		 * @param items
		 * @throws MalformedURLException
		 * @throws IOException
		 */
		@SuppressWarnings("unchecked")
		protected void addItemIds(List<Item> items) throws IOException {
			if (items == null || items.size() == 0) return;
			List<Item> idItems = new ArrayList<Item>();
			
			String strTitles = "";		
			Map<String, Item> titleItems = new HashMap<String, Item>();		
			for (Item item : items) {
				titleItems.put(item.getTitle(), item);
				strTitles += URLEncoder.encode(item.getTitle(), "utf-8") + "|"; 
			}		
			strTitles = strTitles.substring(0, strTitles.length() - 1);

			URL pageidsUrl = new URL(WIKI_PAGE_TITLES_URL.replace("${titles}", strTitles));
			InputStream is = pageidsUrl.openStream();
			String pageidsJsonStr = IOUtils.toString(is);		
			is.close();
			
			JSONObject json = (JSONObject) JSONSerializer.toJSON(pageidsJsonStr);
			JSONObject pages = json.getJSONObject("query").getJSONObject("pages");
			for (String id : (Set<String>) pages.keySet()) {
				JSONObject idJson = pages.getJSONObject(id);
				if (idJson.has("pageid")) {
					Item item = titleItems.get(idJson.getString("title"));
					item.setId(idJson.getString("pageid"));
					idItems.add(item);
				}
			}
		}

	}
	
	/**
	 * @param url a wiki image url
	 * @return the canonic image url, without thumb size dimensions
	 */
	public static String canonizeImageUrl(String url) {
		if (isEmpty(url)) return null;
		if (url.startsWith("//")) url = url.replaceFirst("//", "http://");
		if (!url.contains(WIKI_COMMONS_URL) && !url.contains(WIKI_EN_URL)) return url;
		if (!url.contains("thumb/")) return url;
		return url.substring(0, url.lastIndexOf("/")).replace("thumb/", "");
	}
	
	/**
	 * @param url a wiki image url
	 * @return a wiki thumb url with preset size
	 */
	public static String getWikiThumbUrl(String url) { 
		if (url == null || url.isEmpty()) return null;
		if (!url.contains(WIKI_COMMONS_URL) && !url.contains(WIKI_EN_URL)) return url;
		String thumb = url + "/" + WIKI_DEFAULT_THUMB_SIZE + "px-" + url.substring(url.lastIndexOf("/") + 1, url.length()); 
		if (thumb.substring(0, WIKI_COMMONS_URL.length()).equals(WIKI_COMMONS_URL)) {
			thumb = thumb.replace(WIKI_COMMONS_URL, WIKI_COMMONS_URL + "thumb/");
		}
		else if (thumb.substring(0, WIKI_EN_URL.length()).equals(WIKI_EN_URL)) { 
			thumb = thumb.replace(WIKI_EN_URL, WIKI_EN_URL + "thumb/");
		};
		if (thumb.endsWith(".svg")) thumb += ".png";
		
		return thumb;
	}
	
	
	@Inject
	public ItemService(Facebook facebook, MongoOperations mongo, MetaMatcher metaMatcher) {
		this.facebook = facebook;
		this.mongo = mongo;
		this.metaMatcher = metaMatcher;
	}
	
	/**
	 * @param title (must be exact match)
	 * @return a single item
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Item loadItem(String title) throws MalformedURLException, IOException {
		return this.loadItems(title, 1).get(0);
	}

	/**
	 * @param metaMatches
	 * @param children
	 * @return complete meta match items list
	 */
	public List<VMatchChild> loadItems(List<MetaMatch> metaMatches, Set<VMatchChild> children) throws IOException {
		
		Set<VMatchChild> metaMatchChildren = new HashSet<VMatchChild>();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		List<Future<List<Item>>> futures = new ArrayList<Future<List<Item>>>();
		
		// placing the top meta match items in a map for faster join and preparing callables
		Map<String, Item> metaMatchesMap = new HashMap<String, Item>(); // title, item
		for (int i=0; i < metaMatches.size(); i++) {
			String title = metaMatches.get(i).getItem().getTitle();
			metaMatchesMap.put(title, metaMatches.get(i).getItem());
			Callable<List<Item>> worker = new ItemsLoader(title, 1, false);
		    Future<List<Item>> submit = executor.submit(worker);
			futures.add(submit);
		}

		// completing the meta children ids and adding them to the list asynchronously
		for (Future<List<Item>> future : futures) {
			try {
				Item item = future.get().get(0);
				if (!metaMatchesMap.keySet().contains(item.getTitle())) continue;
				item.setId(metaMatchesMap.get(item.getTitle()).getId());
				VMatchChild metaMatchChild = new VMatchChild(item);
				// only items with image which are not already recommended by users are taken
				if (!children.contains(metaMatchChild) && metaMatchChild.getImg() != null) { 
					metaMatchChildren.add(metaMatchChild);
					if (metaMatchChildren.size() > MetaMatcher.MAX_META_MATCHES) break;
				}
			}
			catch (Exception ex) {	}
		}
		
		executor.shutdown();
		
		return new ArrayList<VMatchChild>(metaMatchChildren);
	}
	
	/**
	 * Returning a client-side joined list of items to a matches collection
	 * @param collection
	 * @return a map of item ids to items
	 */
	public Map<String, Item> loadItems(Iterable<? extends Match> matches) { 
		Set<String> itemIds = new HashSet<String>();		
		for (Match match : matches) {
			itemIds.add(match.getParentId());
			itemIds.add(match.getChildId());
		}
		
		Query q = new Query(Criteria.where("id").in(itemIds));
		List<Item> items = this.mongo.find(q, Item.class);		
		Map<String, Item> mapItems = new HashMap<String, Item>();
		for (Item item : items) { mapItems.put(item.getId(), item); }
		
		return mapItems;
	}

	/**
	 * Search for a term, convert xml to json, remove wiki special characters and prepare clean items list object.
	 * In case of a single result limit, searches for exact match.
	 * @param search
	 * @param limit
	 * @return a set of items by search results
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public List<Item> loadItems(String search, Integer limit) 
			throws MalformedURLException, IOException {
		Set<Item> itemSet = new HashSet<Item>(); 
		
		if (limit > 1) {
			itemSet.addAll(this.mongo.find(new Query(
				Criteria.where("title").regex("^" + search, "i")).limit(limit), Item.class));
		}
		
		if (itemSet.size() < limit) {
			ItemsLoader loader = new ItemsLoader(search, limit - itemSet.size(), true);
			List<Item> wikiItems = loader.loadItems();
			itemSet.addAll(wikiItems);
		}
		
		List<VSearchedItem> searchedItems = new ArrayList<VSearchedItem>();
		for (Item item : itemSet) { searchedItems.add(new VSearchedItem(item, search));	}
		Collections.sort(searchedItems);
		List<Item> sortedItems = new ArrayList<Item>();
		for (VSearchedItem searchedItem : searchedItems) { sortedItems.add(searchedItem.getItem()); }
		
		return sortedItems;
	}

	
	/**
	 * Loads an item from wiki api by pageid and fills its data
	 * @param itemId
	 * @return the loaded item
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Item getItem(String itemId) throws MalformedURLException, IOException {
		return this.getItem(itemId, null, null, false);
	}
	
	/**
	 * Loads an item from DB or wiki api by pageid and fills its data
	 * @param itemId
	 * @param parentId the context match parent (optional)
	 * @user the current user (optional)
	 * @param forceNewItem forces an api call to wiki (used for item updates)
	 * @return the loaded item
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Item getItem(String itemId, String parentId, FacebookProfile user, Boolean forceNewItem) throws MalformedURLException, IOException {
		Item item = null;

		if (!forceNewItem) {
			// if given a parent context, match it for child loading
			if (!isEmpty(parentId)) { 
				VMatches matches = this.getMatches(parentId, itemId, user);
				if (matches != null && matches.getChildren().size() > 0) item = matches.getChildren().get(0);
				if (item != null) return item;
			}
			
			// search db first for item
			item = this.mongo.findById(itemId, Item.class);
			if (item != null) return item;
		}
		
		// otherwise search wiki for item
		URL pageidsUrl = new URL(WIKI_PAGE_IDS_URL.replace("${pageids}", itemId));
		InputStream is = pageidsUrl.openStream();
		String pageidsJsonStr = IOUtils.toString(is);
		is.close();
		JSONObject json = (JSONObject) JSONSerializer.toJSON(pageidsJsonStr);
		String title = json.getJSONObject("query").getJSONObject("pages").getJSONObject(itemId).getString("title");		
		
		item = this.loadItem(title);
		item.setId(itemId);

		return item;
	}
	
	/**
	 * @param parentId the parent item 
	 * @param childId the child item (optional)
	 * @param user when specified, retrieving only the current user votes (optional)
	 * @return the matches container with matches children (by descending votes order)  
	 * @throws IOException
	 */
	public VMatches getMatches(String parentId, String childId, FacebookProfile user) throws IOException {
		VMatches matches = new VMatches();
		Item parent = mongo.findOne(new Query(Criteria.where("id").is(parentId)), Item.class);
		if (parent == null) return new VMatches();
		matches.setParent(parent);

		GroupBy groupBy = GroupBy.key("parentId", "childId")
			.initialDocument("{ votesCount: 0, userVoted: false, voterIds: [] }")
			.reduceFunction("function(obj, prev) { prev.votesCount++ ; " + 
			(user != null ? "prev.userVoted = prev.userVoted || " +
			"obj.userId == " +	user.getId() + "; " : "") + " prev.voterIds.push(obj.userId); }");
		Criteria c = Criteria.where("parentId").is(parent.getId());
		if (!isEmpty(childId)) 
			c = c.andOperator(Criteria.where("childId").is(childId));
		
		GroupByResults<VotedMatch> resVotes = 
				mongo.group(c, "votes", groupBy, VotedMatch.class);

		Map<String, Reference> friendsMap = new HashMap<String, Reference>();
		if (user != null) { 
			List<Reference> friends = this.facebook.friendOperations().getFriends();
			for (Reference f : friends) {
				friendsMap.put(f.getId(), f);
			}
		}
		
		Set<VMatchUser> voters = new HashSet<VMatchUser>();
		Map<String, ? extends Item> mapChildren = this.loadItems(resVotes);
		List<VMatchChild> children = new ArrayList<VMatchChild>();
		for (VotedMatch cv : resVotes) {
			VMatchChild child = new VMatchChild(mapChildren.get(cv.getChildId()));
			child.setVotesCount(cv.getVotesCount());
			child.setUserVoted(cv.getUserVoted());
			
			for (String voterId : cv.getVoterIds()) {
				voters.add(new VMatchUser(voterId));
				if (user != null && voterId.equals(user.getId())) continue;
				if (friendsMap.containsKey(voterId)) {
					VMatchUser friend = new VMatchUser(friendsMap.get(voterId));
					child.getFriends().add(friend);
					matches.getFriends().add(friend);
				}
				else {					
					VMatchUser voter = new VMatchUser(voterId);
					if (child.getSampleVoters().size() < MAX_SAMPLE_VOTERS 
						&& !child.getSampleVoters().contains(voter)) 
							child.getSampleVoters().add(voter);
					if (matches.getSampleVoters().size() < MAX_SAMPLE_VOTERS 
						&& !matches.getSampleVoters().contains(voter)) 
							matches.getSampleVoters().add(voter);
				}
			}
			
			children.add(child);
			matches.setUserVoted(matches.getUserVoted() || child.getUserVoted());
		}
		matches.setVotesCount(voters.size());
		matches.setChildren(children);
		
		return matches; 
	}

}
