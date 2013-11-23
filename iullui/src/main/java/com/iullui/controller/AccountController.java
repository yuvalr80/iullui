package com.iullui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iullui.domain.data.Item;
import com.iullui.domain.data.ItemAd;
import com.iullui.domain.data.ItemAd.ItemAdParent;
import com.iullui.domain.data.Money;
import com.iullui.domain.view.VItemAd;
import com.iullui.domain.view.VItemAd.VItemAdParent;
import com.iullui.domain.view.VItemAdUpsert;

/**
 * Account & Item Ads controller 
 * @author Yuval
 */
// TODO: implement #15 Promoted Items, #38 Account & Billing
//@Controller
public class AccountController {

	protected final MongoOperations mongo;
    protected final Environment environment;
	protected final Facebook facebook;

	protected Money cpv;
	protected Money cpr;
	protected Money cpc;

	@Inject
	public AccountController(Facebook facebook, MongoOperations mongoTemplate, Environment environment) {
		this.facebook = facebook;
		this.mongo = mongoTemplate;
		this.environment = environment;
		
		this.cpv = new Money(this.environment.getProperty("common.ad.payments.cpv"));
		this.cpr = new Money(this.environment.getProperty("common.ad.payments.cpr"));
		this.cpc = new Money(this.environment.getProperty("common.ad.payments.cpc"));
	}

	protected FacebookProfile getUser() { 
		return facebook.userOperations().getUserProfile();
	}
	
	@RequestMapping(value = "/api/user/ads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<VItemAd> itemAds() {
		List<ItemAd> ads = this.mongo.find(
			Query.query(Criteria.where("userId").is(this.getUser().getId())), ItemAd.class);
		
		// join items
		Set<String> itemIds = new HashSet<String>();		
		for (ItemAd ad : ads) {
			itemIds.add(ad.getItemId());
			for (ItemAd.ItemAdParent parent : ad.getParents()) {
				itemIds.add(parent.getId());
			}
		}
		
		List<Item> items = this.mongo.find(Query.query(Criteria.where("id").in(itemIds)), Item.class);
		Map<String, Item> itemsMap = new HashMap<String, Item>();
		for (Item item : items) { itemsMap.put(item.getId(), item);	}
		
		List<VItemAd> vads = new ArrayList<VItemAd>();
		for (ItemAd ad : ads) {
			VItemAd vad = (VItemAd) ad;
			vad.setItem(itemsMap.get(vad.getItemId()));
			for (ItemAdParent parent : ad.getParents()) {
				VItemAdParent vparent = (VItemAdParent) parent;
				vparent.setItem(itemsMap.get(vparent.getId()));
				vad.getVParents().add(vparent);
			}
		}
		
		return vads;
	}
	
	@RequestMapping(value="/api/user/ads", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value=HttpStatus.OK)
	public void addItemAd(@RequestBody VItemAdUpsert itemAdAddUpdate) {
		ItemAd itemAd = new ItemAd(this.getUser().getId(), 
				itemAdAddUpdate, this.cpv, this.cpr, this.cpc);
		this.mongo.insert(itemAd);
	}	

	
	@RequestMapping(value="/api/user/ads", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value=HttpStatus.OK)
	public void updateItemAd(@RequestBody VItemAdUpsert itemAdAddUpdate) {
		ItemAd itemAd = this.mongo.findOne(
			Query.query(Criteria.where("id").is(itemAdAddUpdate.getItemId())), ItemAd.class);
		
		this.mongo.save(itemAd);
	}	

	@RequestMapping(value="/api/user/ads/{itemAdId}", method = RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.OK)
	public void deleteItemAd(@PathVariable("itemAdId") String itemAdId) {
		this.mongo.findAndRemove(Query.query(Criteria.where("id").is(itemAdId)), ItemAd.class);
	}	

}
