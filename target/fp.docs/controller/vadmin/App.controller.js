sap.ui.controller("controller.vadmin.App", {
 
	onInit:function(){ 
		var oModel = new sap.ui.model.json.JSONModel();
	    oModel.loadData("model/_menu_vadmin.json",null,false); 
		sap.ui.getCore().setModel(oModel, '_admin_menu'); 
		
		var oModel = new sap.ui.model.json.JSONModel();
		oModel.setData(Connection.requestData(Configuration.manageVendorAccountServiceUrl.profileEndPoint).result);
		sap.ui.getCore().setModel(oModel, 'profile');
		this.getModel("salutation").setData(Connection.requestData(Configuration.general.scimAttributes+Configuration.scimAttributes.salutation));
		this.getModel("status").setData(Connection.requestData(Configuration.manageSetupServiceUrl.accountStatus));
	},

	getModel:function(oModelName){
		var oModel=this.getView().getModel(oModelName);
		if(!oModel){ 
			oModel=new sap.ui.model.json.JSONModel();
			sap.ui.getCore().setModel(oModel,oModelName);
		}
		return oModel;
	}
});