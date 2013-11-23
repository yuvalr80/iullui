/** 
 * account page script
 * @author yuval 
 */
$(function() {
	
	var myItemsViewModel = new MyItemsViewModel();
	
	// knockout.js ViewModel for parent / child items
	function MyItemsViewModel() { 
		var self = this;
		self.items = ko.observableArray();
		self.selectedItem = ko.observable();
		self.processing = ko.observable(false);
		self.requestPermission = ko.observable(false);
		self.loaded = ko.observable(false);
		
		self.sortedItems = ko.computed(function() {
			return self.items.sort(function(left, right) {
				return (left.ownerId != null ? -1 : 1);
			});
		});
		
		self.loadItems = function() { 
			self.loaded(false);
			self.processing(true);
			$.getJSON("api/user/permissions/", function(data) {
				self.requestPermission($.inArray("manage_pages", data) == -1);
				if (!self.requestPermission()) {
					$.getJSON("api/user/items/", loadItemsHandler);
				}
				else {
					self.processing(false);
				}
			});
		};

		self.updateItem = function(item) { 
			self.processing(true);
			self.selectedItem(item);
			$.ajax({
			    type: "PUT",
			    url: "api/user/items/" + item.id,
			    success: self.loadItems, 
			    error:function (xhr, ajaxOptions, thrownError) {
			    	self.processing(false);
                    alert("Error: " + xhr.status + "\n" + thrownError);
			    }
			});
		};
		
		self.updateVideo = function(item, e) {
			e.preventDefault();
			self.processing(true);
			var videoId = $("#youtubeVideo_" + item.id).val();
			$("#updateVideoSuccess_" + item.id).text("");
			
			// in case a whole url was entered (common mistake)
			if (videoId != null) videoId = extractVideoId(videoId);
			$("#youtubeVideo_" + item.id).val(videoId);
			
			if (videoId != null && videoId.trim().length !== 0) {
				videoId = NS_YOUTUBE + videoId;  
				$.ajax({
				    type: "PUT",
				    url: "/api/user/items/" + item.id + "/media/" + videoId,
				    success: function() {
				    	self.processing(false);
				    	$("#updateVideoSuccess_" + item.id).text("Updated");
				    	$("#updateVideoSuccess_" + item.id).show();
				    }, 
				    error:function (xhr, ajaxOptions, thrownError) {
				    	self.processing(false);
		                alert("Error: invalid video ID. Please verify that you have entered a correct ID.");
				    }
				});
			}
			else {
				$.ajax({
				    type: "DELETE",
				    url: "/api/user/items/" + item.id + "/media",
				    success: function() {
				    	self.processing(false);
				    	$("#updateVideoSuccess_" + item.id).text("Removed");
				    	$("#updateVideoSuccess_" + item.id).show();
				    }, 
				    error:function (xhr, ajaxOptions, thrownError) {
				    	self.processing(false);
		                alert("Error: invalid video ID. Please verify that you have entered a correct ID.");
				    }
				});
			};
		};
		
		self.removeItem = function (item) {
			var agree = confirm("If the item is removed, all related matches will be deleted. Are you sure?");
			if (agree) {
				self.processing(true);
				self.selectedItem(item);
				$.ajax({
				    type: "DELETE",
				    url: "api/user/items/" + item.id,
				    success: self.loadItems, 
				    error:function (xhr, ajaxOptions, thrownError) {
				    	self.processing(false);
	                    alert("Error: " + xhr.status + "\n" + thrownError);
				    }
				});
			}
		};
		
		// parsing the response and adding items to the list
		function loadItemsHandler(data) {
			self.processing(false);
			self.loaded(true);
			self.items.removeAll();
			$.each(data, function() {
				self.items.push(new Item(this));	
			});
			
			if (self.selectedItem() !== undefined && self.selectedItem() != null) {
				scrollToItem(self.selectedItem());
			}

			// if in guide mode
			if ($.cookie("content-maker-guide")) {
				var ownedItemImages = [];
				ko.utils.arrayForEach(self.items(), function(item) {
					if (item.ownerId != null) {
						ownedItemImages.push(item.img());
						$("#btnDone").show();
					}
				});
				$.cookie("content-maker-item-images", ownedItemImages);
			}
			
		};

		function scrollToItem(item) {
			if($("#item_" + item.id).length == 0) return; 
			$("#itemsList").scrollTo("#item_" + item.id, 500);
		};
		
		// extract video id in case user has accidentally entered a whole or partial url
		function extractVideoId(videoString) {
			if (videoString.indexOf("youtube.com") !== -1) {
				videoString = videoString.split("v=")[1];
				var ampersandPosition = videoString.indexOf("&");
				if(ampersandPosition != -1) {
					videoString = videoString.substring(0, ampersandPosition);
				}
			}
			else if (videoString.indexOf("youtu.be") !== -1) {
				videoString = videoString.split("youtu.be/")[1];
				var parametersPosition = videoString.indexOf("?");
				if(parametersPosition != -1) {
					videoString = videoString.substring(0, parametersPosition);
				}
			}
			return videoString;
		}
		self.loadItems();
		
	}

	ko.applyBindings(myItemsViewModel, $("#myItems")[0]);

	if ($.cookie("content-maker-guide")) {
		new Messi('<img src="images/you.png" style="width:70px; height:70px; float:left; margin:0 10px 0 0;"/>' +			
			'<p>Here are your Facebook pages. Please select a page you would like to add as a <span>iullui</span> item by clicking "Add to my items". ' +
			'You can also attach a video to it.</p><a id="btnDone" class="button" style="display:none;" href="/">Next...</a>', 
			{title: 'Welcome to your iullui account!', titleClass: "info", center: false, width: 400});
	}
	
	return false;
	
});