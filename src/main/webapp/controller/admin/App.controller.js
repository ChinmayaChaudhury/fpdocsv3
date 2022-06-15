sap.ui.controller("controller.admin.App", {
	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf controller.admin.App
	 */
	onInit:function(){ 
		var oModel = new sap.ui.model.json.JSONModel();
	    oModel.loadData("model/_menu_admin.json",null,false); 
		sap.ui.getCore().setModel(oModel, '_admin_menu');
			
		AppHelper.getModel(this,"profile").setData(Connection.requestData(Configuration.administrator.profileEndPoint).result);
		
		this.initializeDataModels();
		
	},
	/**
	 * Initialize data models
	 */
	initializeDataModels:function(){
		AppHelper.showLoader(); 
		//AppHelper.getModel(this,"industry").setData(Connection.requestData(Configuration.general.scimAttributes+Configuration.scimAttributes.industry));
		AppHelper.getModel(this,"salutation").setData(Connection.requestData(Configuration.general.scimAttributes+Configuration.scimAttributes.salutation));
		AppHelper.getModel(this,"status").setData(Connection.requestData(Configuration.manageSetupServiceUrl.accountStatus));
		AppHelper.hideLoader();	
	}
 
});