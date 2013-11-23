package com.iullui.controller;

import static com.iullui.common.Util.isEmpty;
import static com.rosaloves.bitlyj.Bitly.shorten;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iullui.common.Email;
import com.iullui.common.Util;
import com.iullui.domain.data.Item;
import com.iullui.domain.data.Match;
import com.iullui.domain.data.Vote;
import com.iullui.domain.view.VMatch;
import com.iullui.domain.view.VMatchChild;
import com.iullui.domain.view.VMatches;
import com.iullui.service.ItemService;
import com.iullui.service.MetaMatcher;
import com.iullui.service.MetaMatcher.MetaMatch;
import com.rosaloves.bitlyj.Bitly.Provider;

/**
 * Main controller
 * @author Yuval
 */
@Controller
public class MainController {

	public static final String NS_MATCHES = "/matches";
	public static final String NS_ACCOUNT = "/account";
	public static final String NS_EXPLORE = "/explore";
	
	public static final String FACEBOOK_PAGE_GRAPH_URL = "https://graph.facebook.com/${pageId}";
	public static final String FACEBOOK_APP_URL = "http://www.facebook.com/apps";
	public static final String FACEBOOK_THUMB_TEMPLATE_URL = "http://s3.amazonaws.com/iullui/logo/fb_thumb_template.png";
	public static final String FACEBOOK_THUMB_CHILD_URL = "http://s3.amazonaws.com/iullui/logo/fb_thumb_child.png";
	public static final String ROUND_CORNERS_TEMPLATE_URL = "http://s3.amazonaws.com/iullui/logo/thumb.png";
	public static final String YOUTUBE_SEARCH_VIDEO_URL = "https://gdata.youtube.com/feeds/api/videos?q=${title}&orderby=relevance&start-index=1&max-results=1&v=2&alt=jsonc";
	public static final String YOUTUBE_GET_VIDEO_BY_ID_URL = "https://gdata.youtube.com/feeds/api/videos/${videoId}?v=2&alt=jsonc";
	
	public static final Integer MAX_SEARCH_RESULTS = 50;
	public static final Integer MAX_LATEST_MATCHES = 25;
	public static final Integer DEFAULT_THUMB_SIZE = 100;
	
	protected final Facebook facebook;
	protected final MongoOperations mongo;
    protected final Environment environment;
    protected final Email email;
	protected final Provider bitly;
	
    protected final MetaMatcher metaMatcher;
	protected final ItemService itemService;
	
	private String conf;
	private BufferedImage fbThumbTemplate;
	private BufferedImage fbThumbChild;
	
	@Inject
	public MainController(Facebook facebook, MongoOperations mongo, Environment environment, 
			Email email, MetaMatcher metaMatcher, ItemService itemService, Provider bitly) {
		this.facebook = facebook;
		this.mongo = mongo;
		this.environment = environment;
		this.conf = this.environment.getProperty(Util.APP_ENV) + ".";
		this.email = email;
		this.bitly = bitly;

		this.metaMatcher = metaMatcher;
		this.itemService = itemService;
	}

	@PostConstruct
	public void init()  throws MalformedURLException, IOException { 
		this.fbThumbTemplate = this.initImage(FACEBOOK_THUMB_TEMPLATE_URL);
		this.fbThumbChild = this.initImage(FACEBOOK_THUMB_CHILD_URL);
	}

	protected BufferedImage initImage(String url) throws MalformedURLException, IOException { 
		URL logoUrl = new URL(url);
		InputStream is = logoUrl.openStream();
		BufferedImage template = ImageIO.read(is);
		is.close();
		return template;
	}
	
	
	/*********** General ************/

	@RequestMapping(value="/start", method = RequestMethod.GET)
	public String start(Model model) throws IOException, MalformedURLException {
		this.prepareModel(model, null, null);
		return "start";
	}
	
	@RequestMapping(value="/about", method = RequestMethod.GET)
	public String about(Model model) throws IOException, MalformedURLException {
		this.prepareModel(model, null, null);
		return "about";
	}

	@RequestMapping(value = "/support", method = RequestMethod.GET)
	public String getSupport(Model model) throws IOException, MalformedURLException {
		this.prepareModel(model, null, null);
		return "support";
	}
	
	@RequestMapping(value = "/terms", method = RequestMethod.GET)
	public String getTermsOfService(Model model) throws IOException, MalformedURLException {
		this.prepareModel(model, null, null);
		return "terms";
	}

	@RequestMapping(value = "/privacy", method = RequestMethod.GET)
	public String getPrivacyPolicy(Model model) throws IOException, MalformedURLException {
		this.prepareModel(model, null, null);
		return "privacy";
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String getContact(Model model) throws IOException, MalformedURLException {
		this.prepareModel(model, null, null);
		return "contact";
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.POST) 
	public String postContact(Model model,
			@RequestParam(value="name", required=true) String name,
			@RequestParam(value="email", required=true) String email,
			@RequestParam(value="subject", required=true) String subject,
			@RequestParam(value="message", required=true) String message) 
				throws IOException, MalformedURLException {
		this.prepareModel(model,  null, null);
		if (isEmpty(name) || isEmpty(email) || isEmpty(subject) || isEmpty(message)) {
			model.addAttribute("confirmContact", false);
		}
		else {
			model.addAttribute("confirmContact", true);
			this.email.sendContact(name, email, subject, message);
		}
		return "contact";
	}
	
	@RequestMapping(value="/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String robots() {
		String res = "";
		String disallow = this.environment.getProperty(this.conf + "robots.disallow");
		res += "User-agent: " + this.environment.getProperty(this.conf + "robots.userAgent") + "\n";
		if (!isEmpty(disallow))	{
			res += "Disallow: " + disallow + "\n";
		}
		return res;
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	public void exceptionHandler(Throwable t) {
		t.printStackTrace();
		this.email.sendExceptionLog(t);
	}

	
	/************* App **************/
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) throws IOException, MalformedURLException {
		this.prepareModel(model, null, null);
		return "home";
	}

	@RequestMapping(value = NS_MATCHES, method = RequestMethod.GET) 
	public String canonicalView(Model model, 
			@RequestParam(value="parent", required=true) String parentId,
			@RequestParam(value="child", required=false) String childId) 
				throws IOException, MalformedURLException {
		this.prepareModel(model, parentId, childId);
		return "home";
	}
	
	@RequestMapping(value = NS_ACCOUNT, method = RequestMethod.GET)
	public String account(Model model) throws IOException, MalformedURLException { 
		try {
			model.addAttribute("appId", this.environment.getProperty(conf + "facebook.clientId"));
			model.addAttribute("appName", this.environment.getProperty(conf + "facebook.appName"));
			model.addAttribute("appBaseUrl", this.environment.getProperty(conf + "app.baseUrl"));
			model.addAttribute("user", this.getUser());
		}
		catch (Exception ex) { 
			return this.home(model);
		}
		return "account";
	}
	
	@RequestMapping(value = "/user/signedin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean isUserSignedIn() { 
		try { return facebook.userOperations().getUserProfile() != null; }
		catch (Exception ex) { return false; }
	}
	
	@RequestMapping(value = "/api/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public FacebookProfile getUser() { 
		return facebook.userOperations().getUserProfile();
	}

	@RequestMapping(value = "/api/user/permissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> permissions() { 
		return this.facebook.userOperations().getUserPermissions();
	}

	@RequestMapping(value = "/api/hash/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String hashParent(@PathVariable("parentId") String parentId) {
		return this.hashMatch(parentId, null);
	}
	
	@RequestMapping(value = "/api/hash/{parentId}/{childId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String hashMatch(@PathVariable("parentId") String parentId, @PathVariable("childId") String childId) {
		String longUrl = this.getMatchUrl(parentId, childId, NS_MATCHES);
		try {
			String shortUrl = bitly.call(shorten(longUrl)).getShortUrl();
			return "{\"shortUrl\":\"" + shortUrl + "\"}";
		}
		catch (Exception ex) { 
			return null;
		}
	}
	
	/**
	 * Prepares the model for returning meta data
	 * @param model
	 * @param parentId
	 * @param childId
	 * @throws Exception
	 */
	protected void prepareModel(Model model, String parentId, String childId) throws IOException, MalformedURLException {
		if (this.isUserSignedIn()) model.addAttribute("user", this.getUser());

		model.addAttribute("appId", this.environment.getProperty(conf + "facebook.clientId"));
		model.addAttribute("appName", this.environment.getProperty(conf + "facebook.appName"));
		model.addAttribute("appBaseUrl", this.environment.getProperty(conf + "app.baseUrl"));
		
		// Facebook & SEO meta tags
		if (!isEmpty(parentId)) { 
			Item parent = this.itemService.getItem(parentId);
			model.addAttribute("parent", parent.getTitle());
			model.addAttribute("parentDesc", parent.getDescription());
			model.addAttribute("image", this.environment.getProperty(conf + "app.baseUrl") + "/api/image/" + parent.getId());
			model.addAttribute("url", this.getMatchUrl(parent.getId(), null, NS_MATCHES));
			
			VMatches matches = this.getMatches(parentId);
			model.addAttribute("matches", matches);
			
			if (!isEmpty(childId)) {
				Item child = this.itemService.getItem(childId);
				model.addAttribute("url", this.getMatchUrl(parent.getId(), child.getId(), NS_MATCHES));
				model.addAttribute("child", child.getTitle());
				model.addAttribute("image", this.environment.getProperty(conf + "app.baseUrl") + "/api/image/" + parent.getId() + "/" + child.getId());
				model.addAttribute("childDesc", child.getDescription());
			}
		}
		else { // tag ticker matches
			List<VMatch> latestMatches = this.loadLatestMatches(MAX_LATEST_MATCHES);
			model.addAttribute("latestMatches", latestMatches);
		}
	}

	
	
	/************* Items **************/
	
	@RequestMapping(value = "/api/items", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Item> searchItem(@RequestParam(value="search", required=true) String search)
			 throws MalformedURLException, IOException {
		if (isEmpty(search)) return new ArrayList<Item>(); 
	    return this.itemService.loadItems(search.trim(), MAX_SEARCH_RESULTS) ;
	}

	@RequestMapping(value = "/api/items/{itemId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Item loaditem(@PathVariable("itemId") String itemId, 
			@RequestParam(value="parent", required=false) String parentId) throws IOException {
		return this.itemService.getItem(itemId, parentId, this.isUserSignedIn() ? this.getUser() : null, false);
	}
	
	@RequestMapping(value = "/api/user/items", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Set<Item> userPages() {		
		Set<Item> items = new HashSet<Item>();
		
		// current items from db
		List<Item> existingItems = this.mongo.find(new Query()
			.addCriteria(Criteria.where("ownerId").is(this.getUser().getId())), Item.class);
		for (Item i : existingItems) i.setHideOwnerId(false);
		items.addAll(existingItems);
		
		// new items from the user owned fb pages
		List<Account> accounts = this.facebook.pageOperations().getAccounts();
		for (Account a : accounts) { 
			Page page = this.facebook.pageOperations().getPage(a.getId());
			if (!page.getLink().startsWith(FACEBOOK_APP_URL)) {
				Item item = new Item(page);
				item.setHideOwnerId(false);
				items.add(item);
			}
		}
		
		return items;
	}

	@RequestMapping(value="/api/user/items/{itemId}", method = RequestMethod.PUT)
	@ResponseStatus(value=HttpStatus.OK)
	public void upsertItem(@PathVariable("itemId") String itemId) {
		if (isEmpty(itemId)) return;
		Page page = this.facebook.pageOperations().getPage(itemId.replace(Item.NS_FACEBOOK, ""));
		if (page == null) return;
		Item item = this.mongo.findOne(new Query(Criteria.where("id").is(itemId)), Item.class);
		if (item == null) item = new Item(page); else item.update(page);
		item.setOwnerId(this.getUser().getId());
		this.mongo.save(item);
	}
	
	@RequestMapping(value = "/api/user/items/{itemId}/media/{mediaId}", method = RequestMethod.PUT)
	@ResponseStatus(value=HttpStatus.OK) 
	public void addItemMedia(
			@PathVariable(value = "itemId") String itemId,
			@PathVariable(value = "mediaId") String mediaId) throws IOException {
		
		if (isEmpty(itemId)) return;
		if (isEmpty(mediaId) || mediaId.contains("\"") || mediaId.contains("'")) throw new IOException("Invalid id");
		if (this.facebook.pageOperations().getPage(itemId.replace(Item.NS_FACEBOOK, "")) == null) return; // make sure the item indeed belongs to the user

		Item item = this.mongo.findOne(new Query(Criteria.where("id").is(itemId)), Item.class);

		// validating media
		if (mediaId.startsWith(Item.NS_YOUTUBE)) {
			if (mediaId.trim().equalsIgnoreCase(Item.NS_YOUTUBE)) throw new IOException("Invalid id");
			URL url = new URL(YOUTUBE_GET_VIDEO_BY_ID_URL.replace("${videoId}", URLEncoder.encode(mediaId.replace(Item.NS_YOUTUBE, ""), "utf-8")));
			InputStream is = url.openStream();
			String json = IOUtils.toString(is);
			is.close();
			if (json.contains("Invalid id") || json.contains("Video not found")) throw new IOException("Invalid id");
		}
		else throw new IOException("Invalid id");
		
		item.setMedia(mediaId);
		this.mongo.save(item);
	}

	@RequestMapping(value = "/api/user/items/{itemId}/media", method = RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.OK) 
	public void removeItemMedia(@PathVariable(value = "itemId") String itemId) throws IOException {
		if (isEmpty(itemId)) return;
		if (this.facebook.pageOperations().getPage(itemId.replace(Item.NS_FACEBOOK, "")) == null) return; // make sure the item indeed belongs to the user

		Item item = this.mongo.findOne(new Query(Criteria.where("id").is(itemId)), Item.class);
		item.setMedia(null);
		this.mongo.save(item);
	}
	
	@RequestMapping(value="/api/user/items/{itemId}", method = RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.OK)
	public void removeItem(@PathVariable("itemId") String itemId) {
		if (isEmpty(itemId)) return;
		// make sure the item indeed belongs to the user
		if (this.facebook.pageOperations().getPage(itemId.replace(Item.NS_FACEBOOK, "")) == null) return;
		
		Item item = this.mongo.findOne(new Query(Criteria.where("id").is(itemId)), Item.class);
		item.setOwnerId(this.getUser().getId());
		
		this.mongo.remove(new Query(new Criteria().orOperator(
			Criteria.where("parentId").is(item.getId()), 
			Criteria.where("childId").is(item.getId()))), Match.class);
		this.mongo.remove(item, "items");
	}
	
	/**
	 * Refreshing the item, thus correcting broken image urls
	 * (even if loaded from db, not updating the image url at this scope for performance considerations)
	 * @param itemId
	 * @return the corrected item
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	@RequestMapping(value = "/api/refresh/{itemId}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Item refreshItem(@PathVariable(value="itemId") String itemId) throws IOException, MalformedURLException {
		if (isEmpty(itemId)) return null;
		Item item = this.itemService.getItem(itemId);
		this.refreshItemImage(item);
		return item;
	}

	/**
	 * Refreshing an item image
	 * @param item
	 * @throws IOException
	 */
	protected void refreshItemImage(Item item) throws IOException { 
		Item updateItem = this.itemService.getItem(item.getId(), null, null, true);
		if (updateItem.getImg() != null && !updateItem.getImg().equals(item.getImg())) {
			item.setImg(updateItem.getImg());
		}
		else {
			item.setImg(this.parseWikiImage(item));
		}
	}
	
	@RequestMapping(value = "/api/image/{itemId}", method = RequestMethod.GET, produces={MediaType.IMAGE_PNG_VALUE})
	@ResponseBody
	public byte[] getItemImage(@PathVariable(value="itemId") String itemId) throws IOException, MalformedURLException {
		BufferedImage parentImg = this.getThumbImage(itemId);
		
		BufferedImage img = new BufferedImage(
				 DEFAULT_THUMB_SIZE * 2, DEFAULT_THUMB_SIZE * 2, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawImage(this.fbThumbTemplate, 0, 0, null); // draws the template first
		g.drawImage(parentImg, 0, fbThumbTemplate.getHeight() / 4, null); // draws the parent image on the top-left square
		g.drawImage(this.fbThumbChild, DEFAULT_THUMB_SIZE, fbThumbTemplate.getHeight() / 4, null); // draws the child image on the bottom-right square

		return this.getImageByteArray(img);
	}

	@RequestMapping(value = "/api/image/{parentId}/{childId}", method = RequestMethod.GET, produces={MediaType.IMAGE_PNG_VALUE})
	@ResponseBody
	public byte[] getMatchImage(@PathVariable(value="parentId") String parentId, 
			@PathVariable(value="childId") String childId) throws IOException, MalformedURLException {
		
		// load source images
		BufferedImage parentImg = this.getThumbImage(parentId);
		BufferedImage childImg = this.getThumbImage(childId);

		// create the new image, canvas size double the thumb size
		// and paint both images, child right to the parent
		BufferedImage img = new BufferedImage(
				 DEFAULT_THUMB_SIZE * 2, DEFAULT_THUMB_SIZE * 2, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawImage(this.fbThumbTemplate, 0, 0, null); // draws the template first
		g.drawImage(parentImg, 0, fbThumbTemplate.getHeight() / 4, null); // draws the parent image on the top-left square
		g.drawImage(childImg, DEFAULT_THUMB_SIZE, fbThumbTemplate.getHeight() / 4, null); // draws the child image on the bottom-right square

		return this.getImageByteArray(img);
	}

	
	/**
	 * @param itemId
	 * @return a thumb image for the item id
	 */
	protected BufferedImage getThumbImage(String itemId) throws IOException {
		Item item = this.itemService.getItem(itemId);
		URL imgUrl = null;
		InputStream is = null;
		BufferedImage fullImg = null;
		if (item.getImg() == null) {
			imgUrl = new URL(this.environment.getProperty(conf + "app.baseUrl") + Item.ITEM_IMAGE_PLACEHOLDER);
			is = imgUrl.openStream();
			fullImg = ImageIO.read(is); 
		}
		else {
			imgUrl = new URL(ItemService.getWikiThumbUrl(item.getImg()));
			try { // wiki thumbnail first
				is = imgUrl.openStream();
				fullImg = ImageIO.read(is);
			}
			catch (IOException ex) { // if wiki thumbnail could not be loaded
				imgUrl = new URL(item.getImg());
				is = imgUrl.openStream();
				fullImg = ImageIO.read(is);
			}
		}
		
		is.close();
		
		return this.getThumbImage(fullImg, DEFAULT_THUMB_SIZE);
	}
	
	/**
	 * Scales to the smaller between the img width and height and crops it by its horizontal center
	 * @param image
	 * @param thumbSize
	 * @return a cropped, resized image
	 */
	protected BufferedImage getThumbImage(BufferedImage img, Integer thumbSize) {
		float scale = thumbSize / new Float(Math.min(img.getWidth(), img.getHeight()));
		BufferedImage res = new BufferedImage(
			Math.round(img.getWidth() * scale), Math.round(img.getHeight() * scale), 
			img.getType());
		
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp scaleOp = 
		   new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		res = scaleOp.filter(img, res);
		
		if (res.getWidth() <= res.getHeight()) {
			res = res.getSubimage(0, 0, thumbSize, thumbSize);
		}
		else {
			res = res.getSubimage(res.getWidth() / 2 - thumbSize / 2, 0, thumbSize, thumbSize);
		}
		return res;
	}
	
	/**
	 * @param img
	 * @return the byte array of the image png encoding
	 */
	protected byte[] getImageByteArray(BufferedImage img) throws IOException { 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(img, "PNG", os);
		os.flush();
		byte[] barr = os.toByteArray();
		os.close();
		return barr; 
	}
	
	/**
	 * Parsing an item image from its wiki page
	 * @param item
	 * @return new image url
	 */
	protected String parseWikiImage(Item item) throws IOException { 
		Document doc = Jsoup.connect(item.getUrl()).get();
		Elements ps = doc.getElementsByClass("infobox");
		if (ps.isEmpty()) return null;
		
		Element infobox = ps.first();
		Elements images = infobox.getElementsByTag("img");
		if (images.isEmpty()) return null;
		String url = images.first().attr("src");

		url = (!isEmpty(url) ? ItemService.canonizeImageUrl(url) : null); 

		return url;
	}

	
	
	/************* Matches and Votes **************/
	
	@RequestMapping(value = "/api/matches/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public VMatches getMatches(@PathVariable("parentId") String parentId) 
			throws IOException, MalformedURLException {
		if (isEmpty(parentId)) return new VMatches();
		
		VMatches matches = this.itemService.getMatches(parentId, null, this.isUserSignedIn() ? this.getUser() : null);
		List<VMatchChild> children = matches.getChildren();

		// adds meta matches if no votes are found
		if (children.size() < MetaMatcher.MAX_META_MATCHES) {
			List<MetaMatch> metaMatches = this.metaMatcher.getMetaMatches(this.itemService.getItem(parentId));
			List<VMatchChild> metaMatchItems = this.itemService.loadItems(metaMatches, new HashSet<VMatchChild>(children));
			children.addAll(metaMatchItems);
		}
		
		Collections.sort(matches.getChildren(), Collections.reverseOrder());
			
		return matches;
	}
	
	@RequestMapping(value = "/api/video/{itemId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getItemVideo(@PathVariable("itemId") String itemId) throws IOException { 
		if (isEmpty(itemId)) return null;
		
		Item item = this.itemService.getItem(itemId);
		if (item == null) return null;
		if (item.isFacebook()) return item.getMedia();
		
		URL url = new URL(YOUTUBE_SEARCH_VIDEO_URL.replace("${title}", "\"" + URLEncoder.encode(item.getTitle(), "utf-8") +  "\""));
		InputStream is = url.openStream();
		String json = IOUtils.toString(is);
		is.close();
		
		return json;
	}
	
	/**
	 * @param limit how many latest matches to load
	 * @return the latest matches
	 */
	@RequestMapping(value="/api/matches/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<VMatch> loadLatestMatches(@RequestParam(value="limit", required=false) Integer limit) {
		if (limit == null) limit = 1; else if (limit > MAX_LATEST_MATCHES) limit = MAX_LATEST_MATCHES;
		Query q = new Query().limit(limit);
		q.sort().on("timestamp", Order.DESCENDING);
		List<Vote> latestMatches = mongo.find(q, Vote.class);
		Map<String, Item> mapItems = this.itemService.loadItems(latestMatches);

		// join items 
		List<VMatch> latestViewMatches = new ArrayList<VMatch>();
		for (Vote v : latestMatches) {
			VMatch vm = new VMatch();
			vm.setParent(mapItems.get(v.getParentId()));
			vm.setChild(mapItems.get(v.getChildId()));
			vm.setUserId(v.getUserId());
			vm.setComment(v.getComment());
			vm.setTimestamp(v.getTimestamp());
			latestViewMatches.add(vm);
		}

		return latestViewMatches;
	}

	@RequestMapping(value="/api/matches/{parentId}/{childId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value=HttpStatus.OK)
	public void addVote(@PathVariable("parentId") String parentId, 
			@PathVariable("childId") String childId) throws MalformedURLException, IOException {
		
		if (isEmpty(parentId) || isEmpty(childId)) throw new MalformedURLException();
		parentId = parentId.trim();	childId = childId.trim();
		
		Item parent = this.itemService.getItem(parentId);
		Item child = this.itemService.getItem(childId);
		
		if (parent.equals(child)) return; // recommendation to the same item is not allowed
		
		// refreshing the images if not found
		if (parent.getImg() == null) this.refreshItemImage(parent);
		if (child.getImg() == null) this.refreshItemImage(child);

		// save the recommendation
		this.saveMatch(parent, child, this.getUser());

		// publish the action to facebook object graph
		try {
			this.facebook.openGraphOperations().publishAction(
				"recommend", "match", this.getMatchUrl(parent.getId(), child.getId(), NS_MATCHES));
		}
		catch (UncategorizedApiException ucaEx) {
			if (!environment.getProperty(Util.APP_ENV).equals(Util.ENV_DEV)) {
				this.exceptionHandler(ucaEx);
			}
		}
	}


	@RequestMapping(value="/api/matches/{parentId}/{childId}", method = RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.OK)
	public void removeVote(@PathVariable("parentId") String parentId, 
			@PathVariable("childId") String childId) throws MalformedURLException, IOException {
		if (isEmpty(parentId) || isEmpty(childId)) throw new MalformedURLException();
		parentId = parentId.trim(); childId = childId.trim();

		Vote vote = this.mongo.findOne(
				new Query(Criteria.where("parentId").is(parentId).and("childId").is(childId)
						.and("userId").is(this.getUser().getId())), Vote.class); 

		this.mongo.remove(vote);
	}

	/**
	 * Saves the items if don't exist and the user's match  
	 * @param parent
	 * @param child
	 * @param user
	 * @return vote
	 */
	protected Vote saveMatch(Item parent, Item child, FacebookProfile user) {
		Vote vote = new Vote(parent.getId(), child.getId(), user.getId());
		mongo.save(parent);
		mongo.save(child);
		mongo.save(vote);
		return vote;
	}
	
	/**
	 * @param parent
	 * @param child
	 * @param ns the url namespace
	 * @return the match object url
	 */
	protected String getMatchUrl(String parentId, String childId, String ns) { 
		return this.environment.getProperty(conf + "app.baseUrl") 
			+ ns + "?parent=" + parentId + (childId != null ? "&child=" + childId : "");
	}
	
	
}