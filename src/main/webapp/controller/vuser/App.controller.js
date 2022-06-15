sap.ui.controller("controller.vuser.App", {
	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf controller.vuser.App
	 */
	onInit : function() {
		var oModel = new sap.ui.model.json.JSONModel();
		oModel.loadData("model/_menu_vuser.json", null, false);
		sap.ui.getCore().setModel(oModel, '_vuser_menu');
  
		var oModel = new sap.ui.model.json.JSONModel();
		oModel.setData(Connection.requestData(Configuration.vendorUserAccountServiceUrl.profileEndPoint).result);
		sap.ui.getCore().setModel(oModel, 'profile');
	} 
});