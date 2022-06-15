sap.ui.controller("controller.fpuser.App", {
 
	onInit:function(){ 
		var oModel = new sap.ui.model.json.JSONModel();
	    oModel.loadData("model/_menu_fpuser.json",null,false); 
		sap.ui.getCore().setModel(oModel, '_fpuser_menu');
		
		var oModel = new sap.ui.model.json.JSONModel();
		oModel.setData(Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.profileEndPoint).result);
		sap.ui.getCore().setModel(oModel, 'profile');
		  
	}
});