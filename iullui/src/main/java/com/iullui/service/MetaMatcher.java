package com.iullui.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import com.iullui.domain.data.Item;

/**
 * A meta matches generator 
 * @author Yuval
 */
@Service
public class MetaMatcher {
	
	public static final String WIKI_ITEM_CATEGORIES_URL = "http://en.wikipedia.org/w/api.php?action=query&pageids=${ids}&prop=categories&cllimit=max&clshow=!hidden&format=json";
	public static final String WIKI_CATEGORY_ITEMS_URL = "http://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle=${category}&cmtype=page&cmlimit=max&format=json&cmcontinue=";
	
	public static final Integer MAX_CATEGORY_PAGES = 3; 
	public static final Integer MAX_META_MATCHES = 5;
	public static final Integer MIN_RATE = 3; // minimum different categories / contexts
	public static final Integer NTHREDS = 30;
	
	
	public class MetaMatch implements Comparable<MetaMatch> {
		
		protected Item item;
		protected Integer rate;
		
		public MetaMatch(Item item, Integer rate) {
			this.item = item;
			this.rate = rate;
		}
		
		public Item getItem() { 
			return this.item;
		}

		public Integer getRate() {
			return this.rate;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof MetaMatch)) return false;
			return (this == obj || this.item.equals(((MetaMatch) obj).getItem()));
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(MetaMatch matchRate) {
			return matchRate.getRate() - this.getRate();
		}
		
	}

	/**
	 * Callable worker to extract a category's pages
	 * @author Yuval
	 */
	protected class ItemPagesLoader implements Callable<List<JSONObject>> {
		
		protected String category;
		
		public ItemPagesLoader() { } 
		
		public ItemPagesLoader(String category) {
			this.category = category;
		}
		
		@Override
		public List<JSONObject> call() throws Exception {
			Boolean stop = false;
			String strItemsUrl = WIKI_CATEGORY_ITEMS_URL.replace("${category}", URLEncoder.encode(this.category, "utf-8"));
			URL itemsUrl = new URL(strItemsUrl);
			List<JSONObject> categoryPages = new ArrayList<JSONObject>();
			// for each category, paging through the results
			int pagesCounter = 0;
			while (!stop && pagesCounter++ < MAX_CATEGORY_PAGES) {
				InputStream isItems = itemsUrl.openStream();
				String itemsJsonStr = IOUtils.toString(isItems);		
				isItems.close();
				
				JSONObject json = (JSONObject) JSONSerializer.toJSON(itemsJsonStr);
				categoryPages.add(json); // adding the page to the category pages list
				
				if (!json.getJSONObject("query-continue").isNullObject()) {
					String cmcontinue = json.getJSONObject("query-continue").getJSONObject("categorymembers").getString("cmcontinue");
					itemsUrl = new URL(strItemsUrl.replace("cmcontinue=", "cmcontinue=" + cmcontinue));
				}
				else stop = true;
			}
			
			return categoryPages;
		}
		
	}
		  
	
	/**
	 * Crawls and parses wiki articles with the search term and adds derived meta-matches (if don't exist already) 
	 * @param parent
	 */
	public List<MetaMatch> getMetaMatches(Item parent) throws IOException {
		if (parent == null || parent.isFacebook()) return new ArrayList<MetaMatch>();

		Set<String> categories = new HashSet<String>();
		Map<Item, Integer> matchesRank = new HashMap<Item, Integer>(); // Item/Rank map
		
		URL categoriesUrl = new URL(WIKI_ITEM_CATEGORIES_URL.replace("${ids}", parent.getId()));
		InputStream isCategories = categoriesUrl.openStream();
		String categoriesJsonStr = IOUtils.toString(isCategories);		
		isCategories.close();
		
		JSONObject categoriesJson = (JSONObject) JSONSerializer.toJSON(categoriesJsonStr);
		JSONArray categoriesJsonArr = categoriesJson.getJSONObject("query")
				.getJSONObject("pages").getJSONObject(parent.getId()).getJSONArray("categories");
		
		for (int i = 0; i < categoriesJsonArr.size(); i++) {
			String title = categoriesJsonArr.getJSONObject(i).getString("title");
			categories.add(title);
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		List<Future<List<JSONObject>>> futures = new ArrayList<Future<List<JSONObject>>>();

		// executing threads to fetch each category pages from wiki api concurrently  
		for (String category : categories) {
			Callable<List<JSONObject>> worker = new ItemPagesLoader(category);
		    Future<List<JSONObject>> submit = executor.submit(worker);
			futures.add(submit);
		}
		
		// adding all parsed pages
		List<JSONObject> allPages = new ArrayList<JSONObject>();
		for (Future<List<JSONObject>> future : futures) {
			try {
				List<JSONObject> categoryPages = future.get();
				allPages.addAll(categoryPages);
			}
			catch (ExecutionException ex) {	}
			catch (InterruptedException ex) { }
		}
		
		executor.shutdown();
		
		// going through all the categories and extracting their items
		for (JSONObject page : allPages) { 
			if (page.getJSONObject("query").isNullObject()) break;
			JSONArray itemsJson = page.getJSONObject("query").getJSONArray("categorymembers");
		
			// extracting the category members from page 
			for (int i = 0; i < itemsJson.size(); i++) {
				String id = itemsJson.getJSONObject(i).getString("pageid");
				String title = itemsJson.getJSONObject(i).getString("title");
				Item item = new Item();
				item.setId(id);
				item.setTitle(title);
				if (item.equals(parent)) break;
				
				if (!matchesRank.keySet().contains(item)) {
					matchesRank.put(item, 1);
				}
				else {
					matchesRank.put(item, matchesRank.get(item) + 1);
				}
			}
		}
		
		List<MetaMatch> metaMatches = new ArrayList<MetaMatch>();
		for (Entry<Item, Integer> matchRank: matchesRank.entrySet()) {
			metaMatches.add(new MetaMatch(matchRank.getKey(), matchRank.getValue()));
		}
		Collections.sort(metaMatches);
		
		List<MetaMatch> results = new ArrayList<MetaMatch>();
		for (int i=0; i < metaMatches.size(); i++) {
			MetaMatch metaMatch = metaMatches.get(i);
			if (metaMatch.getRate() < MetaMatcher.MIN_RATE) break;
			results.add(metaMatch);
			if (results.size() >= MAX_META_MATCHES * 2) break;
		}
		
		return results;
	}
	
	
}
