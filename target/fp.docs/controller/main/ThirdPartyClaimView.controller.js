/**
 * 
 */
sap.ui.controller("controller.main.ThirdPartyClaimView", {
	
	/**
	 * Initialize the view logic
	 */
	onInit:function(){
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.handleRouteMatched,this);
	},
	/**
	 * 
	 */
	handleRouteMatched : function(evt){ 
	   //Check if the pattern name matches
	   if (evt.getParameter("name") !== "ThirdPartyClaimView"){
				return;
		} 	
	},
	/**
	 * 
	 */
	onNavBack :  function (evt){
		this.router.navTo("InitialView", null, false);
	}, 
	 
	
});