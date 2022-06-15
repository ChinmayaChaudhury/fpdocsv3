sap.ui.controller("controller.vadmin.Master", {
	/**
	 * 
	 */
	onInit:function(){
		this.prevousListItem=null;
		this.oInitialLoadFinishedDeferred = jQuery.Deferred();
		var oModel = sap.ui.getCore().getModel("_admin_menu");  
		this.getView().setModel(oModel);
		this.oMenu=oModel.getProperty("/menu");		
		var oEventBus = this.getEventBus();
		oEventBus.subscribe("Detail", "TabChanged", this.onDetailTabChanged, this);
		
		var oList = this.getView().byId("menuList"); 
		oList.attachEvent("updateFinished",function(){
			this.oInitialLoadFinishedDeferred.resolve()
		},this); 
		
		//On phone devices, there is nothing to select from the list. There is no need to attach events.
		if (sap.ui.Device.system.phone) {
			return;
		}

		this.router=this.getRouter("adminRouter");
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);

		oEventBus.subscribe("Detail", "Changed", this.onDetailChanged, this);
		oEventBus.subscribe("Detail", "NotFound", this.onNotFound, this);
	},

	onRouteMatched: function(oEvent) {
		var sName = oEvent.getParameter("name");
	
		if (sName !== "main") {
			return;
		}
	
		//Load the detail view in desktop
		this.loadDetailView();
	
		//Wait for the list to be loaded once
		this.waitForInitialListLoading(function() {
	
			//On the empty hash select the first item
			this.selectFirstItem();
	
		}); 
	},
	
	onListItemPress: function (evt) {
        try{    
            var oListItem=evt.getSource(); 
            this.setListItemSelected(oListItem);
			var context = oListItem.getBindingContext();
            var path = context.sPath;

            var start = path.lastIndexOf('/') + 1; 
            var menuIndex = path.substring(start, path.length); 
            this.router.navTo(this.oMenu[menuIndex].menu_action, { index: menuIndex }); 

        } catch (e) {
        }
    },
    
    setListItemSelected:function(oListItem){
    	var oList = this.getView().byId("menuList");
		var aItems = oList.getItems();
		for(var i=0;i<aItems.length;i++){
			var oCurrentListItem = aItems[i];
			if(oCurrentListItem.hasStyleClass('sapMLIBSelected')){  
			    oCurrentListItem.removeStyleClass('sapMLIBSelected'); 
				break;
			}
		} 
		oListItem.addStyleClass('sapMLIBSelected');
    },
	loadDetailView: function() {
//		this.getRouter().myNavToWithoutHash({
//			currentView: this.getView(),
//			targetViewName: "view.admin.Detail",
//			targetViewType: "XML"
//		});	
	},
	waitForInitialListLoading: function(fnToExecute) {
		jQuery.when(this.oInitialLoadFinishedDeferred).then(jQuery.proxy(fnToExecute, this));
	},
	
	onNotFound: function() {
		this.getView().byId("menuList").removeSelections();
	},

	selectFirstItem: function() {
		var oList = this.getView().byId("menuList");
		var aItems = oList.getItems();
		if (aItems.length) {
			oList.setSelectedItem(aItems[0], true);
			aItems[0].addStyleClass('sapMLIBSelected');
			//Load the detail view in desktop
			this.loadDetailView();
			oList.fireSelect({
				"listItem": aItems[0]
			});
		} else {
//			this.getRouter().myNavToWithoutHash({
//				currentView: this.getView(),
//				targetViewName: "view.admin.NotFound",
//				targetViewType: "XML"
//			});
		}
	},
	onSelect: function(oEvent) {
		// Get the list item either from the listItem parameter or from the event's
		// source itself (will depend on the device-dependent mode)
		this.showDetail(oEvent.getParameter("listItem") || oEvent.getSource());
	},

	showDetail: function(oItem) {
		// If we're on a phone device, include nav in history
		var bReplace = jQuery.device.is.phone ? false : true;
		var path=oItem.getBindingContext().getPath();
		var start = path.lastIndexOf('/') + 1; 
        var menuIndex = path.substring(start, path.length);  
        this.router.navTo(this.oMenu[menuIndex].menu_action, { index: menuIndex },bReplace); 
	},

	getEventBus: function() {
		return sap.ui.getCore().getEventBus();
	},

	getRouter: function() {
		return sap.ui.core.UIComponent.getRouterFor(this);
	},

	onExit: function(oEvent) {
		var oEventBus = this.getEventBus();
		oEventBus.unsubscribe("Detail", "TabChanged", this.onDetailTabChanged, this);
		oEventBus.unsubscribe("Detail", "Changed", this.onDetailChanged, this);
		oEventBus.unsubscribe("Detail", "NotFound", this.onNotFound, this);
	}
});