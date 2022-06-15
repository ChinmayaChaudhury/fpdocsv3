sap.ui.controller("controller.main.InitialView", {
	
	onInit:function(){
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		var oModel = new sap.ui.model.json.JSONModel('model/model.json ');
		this.getView().setModel(oModel);
	},  
	/**
	 * 
	 * On document request
	 */
	requestService:function(evt){  
	 	jQuery.sap.log.info("Request link clicked");
	 	var numberUnit=evt.getSource().getNumberUnit();
	 	switch(numberUnit){ 
	 		case "vendorUser":
	 			 window.location.href = "/fp.docs/vuser";
	 			break;
	 		case "internalUser":
	 			 window.location.href = "/fp.docs/fpuser";
	 			
	 			break;
	 		case "vendorAccount": 
	 			  window.location.href = "/fp.docs/vadmin";
	 			break;
	 		case "settings":  
	 		 	  window.location.href = "/fp.docs/fpadmin"; 
	 			break;
	 	
	 	}
		
	},
	/**
	 * 
	 */
	onNavBack :  function (evt){
		this.router.navTo("InitialView", null, false);
	}, 
	 
 

});