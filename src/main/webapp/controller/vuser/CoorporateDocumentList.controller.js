sap.ui.controller("controller.vuser.CoorporateDocumentList", {

	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf controller.vuser.CoorporateDocumentList
	 */
	onInit : function() {   
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		this.loadRequiredData();
		AppHelper.initProfileDetails(this);
		
	},
	loadRequiredData:function(){
		AppHelper.showLoader()
		var rootData=Connection.requestData(Configuration.vendorUserAccountServiceUrl.queryCoorporateDocs); 
		this.getModel('root').setData(rootData);
		if(rootData){
			this.loadLocationContent(rootData.result.openCmisID, rootData.result.openCmisID, rootData.result.contentType); 
		}
		AppHelper.hideLoader();
	},
	/**
	 * handle routing 
	 */
	onRouteMatched:function(oEvent){
		var sName=oEvent.getParameter('name');
		if(sName==="corp_document"){  
			this.loadRequiredData();
		}else{ 
			return;
		} 
	},
	/**
	 * Handle the click events
	 */
	onItemClickEvent : function(oEvent) { 
		this.oDocument=this.getView().getModel('corp').getProperty(oEvent.getSource().getBindingContext('corp').sPath); 
		if(this.oDocument.contentType==="doc/folder"){  
			 this.getModel("croot").setData(this.oDocument);
			 this.loadLocationContent(this.oDocument.openCmisID,this.oDocument.openCmisParentID,this.oDocument.contentType);
		} 
		else{
			this.initDownload(this.oDocument);
		}
	},
	
	onItemSelectedEvent:function(oEvent){    
		this.oDocument=this.getView().getModel('corp').getProperty(oEvent.getSource().getSelectedItem().getBindingContext('corp').sPath); 
		var visible=this.oDocument.contentType!=="doc/folder"?true:false;
		this.toggleDownloadButton(visible);
	},
	toggleDownloadButton:function(isVisible){ 
	    this.getView().byId('btnDownload').setVisible(isVisible); 
	},
	
	loadLocationContent:function(oOpenCmisDocumentID,oOpenCmisDocumentParent,oContentType){ 
		var that=this;
		var data={documentID:oOpenCmisDocumentID,parentID:oOpenCmisDocumentParent};   
		if(oContentType==="doc/folder"){  
			var url=Configuration.vendorUserAccountServiceUrl.documentQuery
			Connection.postData(url, data, 
					function(response){
							if(response.result.length>0){
								that.getModel('corp').setData(response);
							}else{
								sap.m.MessageToast.show("No items found"); 
							}
							that.updateBreadCrumb();
				             
					}, 
					function(xhr, textStatus, errorThrown){
						if(xhr.responseJSON.resultType!=="SUCCESS"){ 
							 sap.m.MessageToast.show(xhr.responseJSON.result); 
					} 
			});
			
		}
		
	},
	/**
	 * Handle download
	 */
	handleDownload:function(oEvent){
		var sPath=this.byId('documentList').getSelectedItem().getBindingContext("corp").sPath;
		var oDocument=this.getView().getModel('corp').getProperty(sPath);
		this.initDownload(oDocument);
	}
	,
	initDownload:function(oDocument){
		var url=Configuration.vendorUserAccountServiceUrl.documentDownload; 
		Connection.dowloadRequest(oDocument, url);
	},
	/**
	 * Display the user profile
	 * @param oEvent
	 */
	onProfileShowEvent: function (oEvent) { 
		AppHelper.displayProfileDetails(this, oEvent);
	},
	/**
	 * Handle system logout
	 * @param oEvent
	 */
	onLogoutRequest:function(oEvent){ 
		Connection.logout(Configuration.home); 
	},
	/**
	 * Toggle the back button
	 * @param isFolder
	 */
	toggleBackButton:function(isFolder){ 
	    this.getView().byId('navigate_back').setVisible(isFolder); 
	    this.removeDocumentListSelection();
	},
	/**
	 * Remove document list selection
	 */
	removeDocumentListSelection:function(){
		this.byId('documentList').removeSelections(true);
		this.toggleDownloadButton(false); 
	},
	/**
	 * Navigate back
	 * @param oEvent
	 */
	onBackPress:function(oEvent){
		var that=this;
		var oDocument=this.getModel('croot').getData();
		this.loadLocationContent(oDocument.openCmisParentID, oDocument.openCmisParentID, oDocument.contentType);
		var url=Configuration.vendorUserAccountServiceUrl.generalDocumentQuery;
		var data={documentID:oDocument.openCmisParentID};
		Connection.postData(url, data, 
				function(response){  
					that.getModel('croot').setData(response.result); 
					that.updateBreadCrumb();
				}, 
				function(xhr, textStatus, errorThrown){
					if(xhr.responseJSON.resultType!=="SUCCESS"){ 
						 sap.m.MessageToast.show(xhr.responseJSON.result); 
				} 
		});  
	},
	/**
	 * Update the document list bread crumbs
	 */ 
	updateBreadCrumb:function(){ 
		var documentPath = this.getModel('croot').getData().documentPath;
		if(!documentPath){
			documentPath= this.getModel("root").getData().result.documentPath;
		}
		var pathSections=documentPath.split("/");
		var oContent=[];
		for(var v=2;v<pathSections.length;v++){
			var section=pathSections[v];
			oContent.push(new sap.m.Label({text:section,design:"Bold"}));
			if(v!==pathSections.length-1){
				var pathIcon=new sap.ui.core.Icon({src:"sap-icon://slim-arrow-right"})
				oContent.push(pathIcon);
			}   
		} 
		var infoToolBar=new sap.m.OverflowToolbar({design:"Solid",content:oContent}); 
		this.byId("documentListPanel").setInfoToolbar(infoToolBar ); 
		if(pathSections.length>3){ 
			this.toggleBackButton(true);
		}else{  
			this.toggleBackButton(false);
		}  
	},
	
	/**
	 * Create Corporate Document Event
	 * @param oEvent
	 */
	getModel:function(oModelName){
		var oModel=this.getView().getModel(oModelName);
		if(!oModel){ 
			oModel=new sap.ui.model.json.JSONModel();
			this.getView().setModel(oModel,oModelName);
		}
		return oModel;
	},
	/**
	 * Handle user live filters to the documents being accessed
	 * @param oEvent
	 */
	onFilterRequest:function(oEvent){
		var aFilters = [];
	    var sQuery = oEvent.getSource().getValue();
	    if (sQuery && sQuery.length > 0) {
	      var filter = new sap.ui.model.Filter("documentName", sap.ui.model.FilterOperator.Contains, sQuery);
	      aFilters.push(filter);
	    } 
	    var list = this.getView().byId("documentList");
	    var binding = list.getBinding("items");
	    binding.filter(aFilters, "Application");
	},
	onProfileClosePress:function(oEvent){
		AppHelper.closeProfileDetails(this);
	}
});