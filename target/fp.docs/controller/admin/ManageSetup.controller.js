sap.ui.controller("controller.admin.ManageSetup", {
	
	actions:{edit:"edit",create:"create",none:"none"},
	/**
	 * @memberOf controller.admin.ManageSetup
	 */
	onInit : function() { 
		this.oInitialLoadFinishedDeferred = jQuery.Deferred();  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"vc"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"root"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"corp"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"ds"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"cl"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"fs");  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"quotas");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"ug");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"fug");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"vug");  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"allocation");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"clevel");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"clevels");
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		if (sap.ui.Device.system.phone) {
			// Do not wait for the master when in mobile phone resolution
			this.oInitialLoadFinishedDeferred.resolve();
		} 
		this.loadRequiredData();
		
	},
	loadRequiredData:function(){
		AppHelper.showLoader();
		AppHelper.getModel(this,'vc').setData(Connection.requestData(Configuration.manageSetupServiceUrl.vendorCategory));
		AppHelper.getModel(this,'ds').setData(Connection.requestData(Configuration.manageSetupServiceUrl.directoryRestrictions));
		AppHelper.getModel(this,'cl').setData(Connection.requestData(Configuration.manageSetupServiceUrl.confidentiality));
		AppHelper.getModel(this,'root').setData(Connection.requestData(Configuration.manageSetupServiceUrl.queryCoorporateDocs));
		this.initFolderStructureDataRequest();
		AppHelper.getModel(this,'ug').setData(Connection.requestData(Configuration.manageSetupServiceUrl.userGroupTypes));
		AppHelper.getModel(this,'fug').setData(Connection.requestData(Configuration.manageSetupServiceUrl.queryFairPriceGroups));
		AppHelper.getModel(this,'vug').setData(Connection.requestData(Configuration.manageSetupServiceUrl.queryVendorUserGroups));
		AppHelper.getModel(this,'clevels').setData(Connection.requestData(Configuration.manageCLevelServiceUrl.query));
		AppHelper.initProfileDetails(this);
		AppHelper.hideLoader();
	},
	initFolderStructureDataRequest:function(){
		AppHelper.getModel(this,'fs').setData(Connection.requestData(Configuration.manageSetupServiceUrl.folderStructure).result);
	},
	/**
	 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
	 * This hook is the same one that SAPUI5 controls get after being rendered.
	 * @memberOf controller.admin.ManageSetup
	 */
	 onBeforeRendering: function() { 
		 AppHelper.getModel(this,'quotas').setData(Connection.requestData(Configuration.manageQuotaServiceUrl.queryQuotaAllocations)); 
		 this.toggleQuotaAllocation(this.actions.create);  
		 
	 },
	/**
	 * @memberOf controller.admin.ManageSetup
	 */
	onRouteMatched : function(oEvent) {
		var sName = oEvent.getParameter("name");
		if (sName !== 'admin_setup') {
			return;
		}
	},
	/**
	 * Folder zoom in
	 * 
	 * @memberOf controller.admin.ManageSetup
	 */
	onZoomIn : function() {
		var pFolderStructure = this.getView().byId("folderStructure");
		pFolderStructure.zoomIn();
		pFolderStructure.getZoomLevel(); 
	},
	/**
	 * Folder zoom out
	 * 
	 * @memberOf controller.admin.ManageSetup
	 */
	onZoomOut : function() {
		var pFolderStructure = this.getView().byId("folderStructure");
		pFolderStructure.zoomOut();
		pFolderStructure.getZoomLevel(); 
	},
	/**
	 * Node click indicator
	 * 
	 * @memberOf controller.admin.ManageSetup
	 */
	onNodePress : function(oEvent) {
		jQuery.sap.require("sap.m.MessageToast");
		var textToDisplay =  oEvent.getParameters().getTitle();
		textToDisplay += " was clicked";
		sap.m.MessageToast.show(textToDisplay);
	},
	/**
	 * Check on tab changes
	 * @param oEvent
	 */
	onTabFocus:function(oEvent){
		jQuery.sap.require("sap.m.MessageToast"); 
		var sSelectedTab= oEvent.getParameter("selectedItem").getAggregation("customData")[0].getProperty("value");
		switch (sSelectedTab) {
		case "setupTab":
			this.toggleQuotaAllocation(this.actions.create);
			break;
		case "confidentialityTab":
			this.toggleConfidentiality(this.actions.create);
			break;
		case "userGroupManagementTab":
		case "folderStructureTab":
			this.toggleGroupManagementButtons(this.actions.create);
			break;
		case "coorporateDocumentTab":
			this.toggleCoorporateDocumentButtons(this.actions.create);
			var rootData=this.getView().getModel('root').getData();
			if(rootData){
				this.loadLocationContent(rootData.result.openCmisID, rootData.result.openCmisID, rootData.result.contentType); 
			}
			this.oDocument=rootData.result;
			this.updateBreadCrumb();
			break; 
		}
	},
	
	/**
	 * Handle Button toggles
	 * @param oAction
	 */
	toggleCoorporateDocumentButtons:function(oAction){
		oAction=oAction==null?"":oAction;
		if(oAction==='edit'){
			this.byId("btn_create_folder").setVisible(true);
			this.byId("btn_upload_content").setVisible(true);
			this.toggleEditButton(false)
			this.toggleDeleteButton(false);
			this.toggleQuotaAllocation(this.actions.none);
			this.toggleConfidentiality(this.actions.none);
			this.toggleGroupManagementButtons(this.actions.none);
		}else if(oAction==='create'){
			this.byId("btn_create_folder").setVisible(true);
			this.byId("btn_upload_content").setVisible(true);
			this.byId("btn_edit_content").setVisible(false);
			this.toggleDeleteButton(false);
			this.toggleQuotaAllocation(this.actions.none);
			this.toggleConfidentiality(this.actions.none);
			this.toggleGroupManagementButtons(this.actions.none); 
		}else{
			this.byId("btn_upload_content").setVisible(false);
			this.byId("btn_create_folder").setVisible(false);
			this.byId("btn_edit_content").setVisible(false);
			this.toggleDeleteButton(false);
		} 
		var selectedItems=this.byId('documentList').getSelectedItems();
		if(selectedItems.length>0){ 
			this.toggleDeleteButton(true);
			this.toggleUploadButton(false);
		}
		if(selectedItems.legth===1 && this.isSelectedItemAFolder()){
			this.toggleEditButton(true) 
		}
	},
	toggleGroupManagementButtons:function(oAction){
		oAction=oAction==null?"":oAction;
		if(oAction==='edit'){
			this.byId("btn_create_group").setVisible(true);
			this.byId("btn_edit_group").setVisible(true);
			this.toggleQuotaAllocation(this.actions.none);
			this.toggleConfidentiality(this.actions.none);
			this.toggleCoorporateDocumentButtons(this.actions.none);
		}else if(oAction==='create'){
			this.byId("btn_create_group").setVisible(true);
			this.byId("btn_edit_group").setVisible(false);
			this.toggleQuotaAllocation(this.actions.none);
			this.toggleConfidentiality(this.actions.none);
			this.toggleCoorporateDocumentButtons(this.actions.none);
			var selectedItems=this.byId('vendorUserGroupList').getSelectedItems();
			if(selectedItems.length>0){
				this.byId("btn_edit_group").setVisible(true);
			}
			selectedItems=this.byId('fairPriceUserGroupList').getSelectedItems();
			if(selectedItems.length>0){
				this.byId("btn_edit_group").setVisible(true); 
			}  
		}else{
			this.byId("btn_create_group").setVisible(false);
			this.byId("btn_edit_group").setVisible(false);
		} 
		
	}, 
	toggleConfidentiality:function(oAction){ 
		oAction=oAction==null?"":oAction;
		if(oAction==='edit'){
			this.byId("btn_create_confidentiality").setVisible(false);
			this.byId("btn_edit_confidentiality").setVisible(true);
			this.toggleQuotaAllocation(this.actions.none);
			this.toggleGroupManagementButtons(this.actions.none);
			this.toggleCoorporateDocumentButtons(this.actions.none);
		}else if(oAction==='create'){
			this.byId("btn_create_confidentiality").setVisible(true);
			this.byId("btn_edit_confidentiality").setVisible(false);
			this.toggleQuotaAllocation(this.actions.none);
			this.toggleGroupManagementButtons(this.actions.none);
			this.toggleCoorporateDocumentButtons(this.actions.none);
			var oSelectedItem=this.byId('confidentialityLevelList').getSelectedItems();
			if(oSelectedItem.length){
				this.byId("btn_edit_confidentiality").setVisible(true);
			}
		}else{
			this.byId("btn_create_confidentiality").setVisible(false);
			this.byId("btn_edit_confidentiality").setVisible(false);
		}  
		var confidentialityCount = AppHelper.getModel(this,'clevels').getData().result.length;
		if( confidentialityCount===3){
			this.byId("btn_create_confidentiality").setVisible(false);
		} 
		
	},
	toggleQuotaAllocation:function(oAction){ 
		/* oAction=oAction==null?"":oAction;
		if(oAction==='edit'){
			this.byId("btn_create_allocation").setVisible(false);
			this.byId("btn_edit_allocation").setVisible(true);
			this.toggleConfidentiality(this.actions.none);
			this.toggleGroupManagementButtons(this.actions.none);
			this.toggleCoorporateDocumentButtons(this.actions.none);
		}else if(oAction==='create'){
			this.byId("btn_create_allocation").setVisible(true);
			this.byId("btn_edit_allocation").setVisible(false);
			this.toggleConfidentiality(this.actions.none);
			this.toggleGroupManagementButtons(this.actions.none);
			this.toggleCoorporateDocumentButtons(this.actions.none);
			var oSelectedItem=this.byId('quotaAllocations').getSelectedItems();
			if(oSelectedItem.length){
				this.byId("btn_edit_allocation").setVisible(true);
			}
		}else{
			this.byId("btn_create_allocation").setVisible(false);
			this.byId("btn_edit_allocation").setVisible(false);
		}   
		var quotaCount = this.getView().getModel('quotas').getData().result.length;
		if( quotaCount===3){
			this.byId("btn_create_allocation").setVisible(false);
		} */
	},
	_dialogFragments : {},
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
	 * Handle folder create request
	 * @param oEvent
	 */
	handleCreateCorporateDocumentEvent:function(oEvent){
		this.resolveCurrentFolderSelected(this.actions.create); 
		this._getDialogFragement("FolderDialog").open();
	},
	/**
	 * Handle folder update request
	 * @param oEvent
	 */
	handleEditContentEvent:function(oEvent){ 
		var sPath=this.byId('documentList').getSelectedItem().getBindingContext('corp').sPath;
		var oSelectedDocument=this.getModel('corp').getProperty(sPath);
		if(oSelectedDocument.contentType==="doc/folder"){ 
			var req={openCmisID:oSelectedDocument.openCmisID,openCmisParentID:oSelectedDocument.openCmisParentID,action:this.actions.edit,documentName:oSelectedDocument.documentName,documentInfo:oSelectedDocument.documentInfo}
			this.getModel("req").setData(req);
			this._getDialogFragement("FolderDialog").open(); 
		} 
	},
	/**
	 * Fire the folder fragment close event
	 * @param oEvent
	 */	
	onCreateFolderDialogClose:function(oEvent){
		this._getDialogFragement("FolderDialog").close();
	},
	/**
	 * Handle Delete Corporate Content
	 * @param oEvent
	 */
	/**
	 * Handle Delete Corporate Content
	 * @param oEvent
	 */
	handleDeleteCorporateContentEvent:function(oEvent){
		//XXX handle delete content Event 
		var that=this;
		sap.m.MessageBox.show( AppHelper.i18n().getProperty("msg_delete_document"), {
            icon: sap.m.MessageBox.Icon.ERROR,
            title: AppHelper.i18n().getProperty("title_delete_document"),
            actions: [sap.m.MessageBox.Action.YES,sap.m.MessageBox.Action.NO],
            onClose: function (oAction) { 
            	if(oAction===sap.m.MessageBox.Action.YES){
            		var seletedDocumentIDs={collection:[]};
            		var selectedItems=that.byId('documentList').getSelectedItems();
            		for(var index=0;index<selectedItems.length;index++){ 
            			var sPath=selectedItems[index].getBindingContext('corp').sPath;
            			var oSelectedDocument=that.getModel('corp').getProperty(sPath);
            			seletedDocumentIDs.collection.push({documentID:oSelectedDocument.openCmisID});
            		} 
            		var url=Configuration.manageSetupServiceUrl.documentDelete;
            		Connection.postData(url, seletedDocumentIDs, 
            				function(response){  
            					if(Object.keys(response.result).length>0){
            						/*
            						*Check if current root is corporate
            						*/
            						var oDocument;
            						if(Object.keys(that.getModel('croot').getData()).length===0){
            							oDocument=that.getModel('root').getData().result;
									}else{
            							oDocument=that.getModel('croot').getData();
            						}
            						that.loadLocationContent(oDocument.openCmisID, oDocument.openCmisParentID, oDocument.contentType); 
            						that.removeDocumentListSelection();
            						that.toggleCoorporateDocumentButtons(that.actions.create)
            					} 
            				}, 
            				function(xhr, textStatus, errorThrown){
            					if(xhr.responseJSON.resultType!=="SUCCESS"){ 
            						 sap.m.MessageToast.show(xhr.responseJSON.result); 
            				} 
            		}); 
            	}
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
	 * Navigate back
	 * @param oEvent
	 */
	onBackPress:function(oEvent){
		var that=this;
		var oDocument=this.getModel('croot').getData();
		this.loadLocationContent(oDocument.openCmisParentID, oDocument.openCmisParentID, oDocument.contentType);
		var url=Configuration.manageSetupServiceUrl.generalDocumentQuery;
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
	 * On Save Folder details
	 * @param oEvent
	 */
	onSaveFolderDetailEvent:function(oEvent){ 
		var that=this;
		try{
			var oFormData=this.getFolderFormData();
			var selectedAction=oEvent.getSource().getAggregation("customData")[0].getProperty("value");
			if(selectedAction===this.actions.create){
				var url=Configuration.manageSetupServiceUrl.createCorporateDoc;
				Connection.postData(url, oFormData, 
						function(response){  
							if(Object.keys(response.result).length>0){
								that.addDocumentObjectToArray(response.result); 
								that.onCreateFolderDialogClose(); ;
							} 
						}, 
						function(xhr, textStatus, errorThrown){
							if(xhr.responseJSON.resultType!=="SUCCESS"){ 
								 sap.m.MessageToast.show(xhr.responseJSON.result); 
						} 
				});  
			}
			else if(selectedAction===this.actions.edit){
				oFormData['openCmisParentID']=this.getModel("req").getData().openCmisParentID;
				oFormData['openCmisID']=this.getModel("req").getData().openCmisID;
				var url=Configuration.manageSetupServiceUrl.updateCorporateDoc;
				Connection.postData(url, oFormData, 
						function(response){  
							if(Object.keys(response.result).length>0){
								that.getModel("corp").setData(response); 
								that.onCreateFolderDialogClose(); 
								that.removeDocumentListSelection();
								that.toggleCoorporateDocumentButtons(that.actions.create);
							} 
						}, 
						function(xhr, textStatus, errorThrown){
							if(xhr.responseJSON.resultType!=="SUCCESS"){ 
								 sap.m.MessageToast.show(xhr.responseJSON.result); 
						} 
				});  
			}
			
		}catch (e) {
			AppHelper.displayMsgAlert(e.message);
		}  
	},
	/**
	 * Get FolderFormData
	 * @returns {formData}
	 */
	getFolderFormData:function(){
		var isFormInputValid=true;
		var oFolderData={};
		var oFolderName=sap.ui.getCore().byId('folder_Name');
		var sFolderName=oFolderName.getValue().trim();
		if(!sFolderName){
			oFolderName.setValueState(sap.ui.core.ValueState.Error);
			oFolderName.setValueStateText(AppHelper.i18n().getProperty("input_empty_msg"));
			isFormInputValid=false;
		}else{
			oFolderName.setValueState(sap.ui.core.ValueState.None);
			oFolderData[oFolderName.getName()]=sFolderName;
		}

		var oFolderDesc=sap.ui.getCore().byId('folder_desc');
		var sFolderDesc=oFolderDesc.getValue().trim();
		if(!sFolderDesc){
			oFolderDesc.setValueState(sap.ui.core.ValueState.Error);
			oFolderDesc.setValueStateText(AppHelper.i18n().getProperty("input_empty_msg"));
			isFormInputValid=false;
		}else{
			oFolderDesc.setValueState(sap.ui.core.ValueState.None);
			oFolderData[oFolderDesc.getName()]=sFolderDesc;
		}
		if(!isFormInputValid){ 
			throw new Error(AppHelper.i18n().getProperty("validation_error_msg"));
		}
		else{ 
			oFolderData['openCmisParentID']=this.getModel("req").getData().openCmisID;
			return oFolderData;
		}
	},
	/**
	 * Handle Upload Item
	 * @param oEvent
	 */
	handleUploadItemClickEvent:function(oEvent){ 
		this.resolveCurrentFolderSelected(this.actions.create);
		this._getDialogFragement("ContentUploadDialog").open();
	}, 
	/**
	 * Resolve the current parent folder required for uploads and folders
	 * @param oAction
	 */
	resolveCurrentFolderSelected:function(oAction){ 
		var currentRootModel=this.getModel("croot");
		var selectedItemsCount=this.byId('documentList').getSelectedItems().length
		if(selectedItemsCount==1){ 
			var sPath=this.byId('documentList').getSelectedItem().getBindingContext('corp').sPath;
			var oSelectedDocument=this.getModel('corp').getProperty(sPath);
			if(oSelectedDocument.contentType==="doc/folder"){
				 currentRootModel.setData(oSelectedDocument);
			}
		} 
		var currentFolderIsSet=Object.keys(currentRootModel.getData()).length>0; 
		if(!currentFolderIsSet){ 
			var rootFolder = this.getModel("root").getData().result;
			currentRootModel.setData(rootFolder);
			var req={openCmisID:rootFolder.openCmisID,action:oAction};
			this.getModel("req").setData(req);	
		}else{
			var data=this.getModel("croot").getData(); 
			var req={openCmisID:data.openCmisID,action:oAction}
			this.getModel("req").setData(req);
		} 
	},
	/**
	 * Check if the selected item is a 
	 * @returns {Boolean}
	 */
	isSelectedItemAFolder:function(){ 
		var oSelectedItem=this.byId('documentList').getSelectedItem();  
		var oContentType=oSelectedItem.mAggregations.customData[2].mProperties; 
		return oContentType.value==="doc/folder";
	},
	/**
	 * Quota allocation Item select 
	 * @param oEvent
	 */
	onDocumentItemSelectedEvent:function(oEvent){ 
	    var selectedItems=this.byId('documentList').getSelectedItems();
		var selectedCount=selectedItems.length;
		if(selectedCount==1){
			var sPath=selectedItems[0].getBindingContext('corp').sPath;
			this.oDocument=this.getView().getModel('corp').getProperty(sPath);
			if(this.oDocument.contentType==="doc/folder"){
			 	this.byId("btn_upload_content").setVisible(true);
			}else{
				this.byId("btn_upload_content").setVisible(false);
			}
			if(this.isSelectedItemAFolder()){
				this.toggleEditButton(true);
			}
		}else if(selectedCount>1){
			this.toggleEditButton(false);
			this.byId("btn_upload_content").setVisible(false);
		}else{
			this.byId("btn_upload_content").setVisible(true); 
		}
		if(selectedCount>0){
			this.toggleDeleteButton(true);
			this.toggleUploadButton(false);
			this.toggleCreateButton(false);
		}else{
			this.toggleDeleteButton(false);
			this.toggleEditButton(false);
			this.toggleUploadButton(true);
			this.toggleCreateButton(true);
		}
	},
	/**
	 * Handle item click event:
	 * <bold>Event will drill down if the document is a folder<bold>
	 * <bold>This will set the upload request object for any possible upload request actions<bold>
	 * @param oEvent
	 */
	handleCorporateItemClickEvent:function(oEvent){
		this.oDocument=this.getModel('corp').getProperty(oEvent.getSource().getBindingContext('corp').sPath); 
		if(this.oDocument.contentType==="doc/folder"){
			 var req={openCmisID:this.oDocument.openCmisID,action:this.actions.create}
			 this.getModel("req").setData(req);	
			 this.getModel("croot").setData(this.oDocument);
		}
		this.loadLocationContent(this.oDocument.openCmisID,this.oDocument.openCmisParentID,this.oDocument.contentType);
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
	
	/**
	 * Handle Close Upload Item Event
	 * @param oEvent
	 */
	onCloseUploadItemClickEvent:function(oEvent){
		//XXX Handle upload Item Event
		this._getDialogFragement("ContentUploadDialog").close();
	},
	/**
	 * Handle Content Upload Event
	 * @param oEvent
	 */
	handleContentUploadPress:function(oEvent){
		//XXX handle content upload Press
		AppHelper.showLoader();
		var oFileUploader= sap.ui.getCore().byId("corporateDocumentUploader");
		if(!oFileUploader.getValue()){
			sap.m.MessageToast.show("Please choose file");
			AppHelper.hideLoader();
			return;
		} 
		oFileUploader.upload();
		
	},
	/**
	 * Handle the Corporate Upload event completion
	 * @param oEvent
	 */
	handleCorporateUploadEventCompletion:function(oEvent){
		//XXX handle corporate complete event
		var sResponse=oEvent.getParameter("responseRaw");
		if(sResponse){ 
			var oResponse=JSON.parse(sResponse);
			if(oResponse.resultType==='SUCCESS'){
				this.addDocumentObjectToArray(oResponse.result); 
				this._getDialogFragement("ContentUploadDialog").close();
			}else{
			   sap.m.MessageToast.show(oResponse.result);
			}
		} 
		sap.ui.getCore().byId("corporateDocumentUploader").setValue("");
		AppHelper.hideLoader();
	},
	addDocumentObjectToArray:function(oDocument){
		var data=this.getModel("corp").getData(); 
		data.result.push(oDocument);
		this.getModel("corp").setData(data);
	},
	/**
	 * Resolve the corporate header by action
	 * @param action
	 */
	folderCorporateDialogTitle:function(sAction){
		var sDailogTitle=AppHelper.i18n().getProperty("title_create_folder");
		if(sAction===this.actions.edit){
			sDailogTitle=AppHelper.i18n().getProperty("title_update_folder");
		}
		return sDailogTitle;
	},
	/**
	 * Load location content and refresh the {@link corp} model
	 * @param oOpenCmisDocumentID
	 * @param oOpenCmisDocumentParent
	 * @param oContentType
	 */
	loadLocationContent:function(oOpenCmisDocumentID,oOpenCmisDocumentParent,oContentType){ 
		var that=this;
		var data={documentID:oOpenCmisDocumentID,parentID:oOpenCmisDocumentParent};   
		if(oContentType==="doc/folder"){   
				var url=Configuration.manageSetupServiceUrl.documentQuery;
				Connection.postData(url, data, 
						function(response){ 
								that.getView().getModel('corp').setData(response);
								if(response.result.length===0){
									sap.m.MessageToast.show("No items found"); 
								}	
								that.updateBreadCrumb();
								  
						}, 
						function(xhr, textStatus, errorThrown){
							if(xhr.responseJSON.resultTyp!=="SUCCESS"){ 
								 sap.m.MessageToast.show(xhr.responseJSON.result); 
						} 
				}); 
		} else{
			sap.m.MessageToast.show("Cannot Drill down");
		}
	}, 
	/**
	 * Create Quota allocation for category
	 * @param oEvent
	 */
	handleActionOnCategory:function(oEvent){
		var selectedAction=oEvent.getSource().getAggregation("customData")[0].getProperty("value");
		var that=this;
		var quotaAllocation={};
		var isValid=true;   
		var oCategoryQuota=sap.ui.getCore().byId('category_quota_size_input');
		var sCategoryQuota = oCategoryQuota.getValue().trim();
		if(sCategoryQuota===""){
			oCategoryQuota.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			oCategoryQuota.setValueState(sap.ui.core.ValueState.None);
		} 
		quotaAllocation.quotaAllocated=sCategoryQuota;
		var oCategoryCombo=sap.ui.getCore().byId('category_quota_combo');
		var sVendorCategory = oCategoryCombo.mProperties.selectedKey.trim();
		if(sVendorCategory.length==0){
			oCategoryCombo.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			oCategoryCombo.setValueState(sap.ui.core.ValueState.None);
		}
		 
		quotaAllocation.vendorCategory=sVendorCategory; 
		if(isValid){ 
			var url=Configuration.manageQuotaServiceUrl.createQuotaAllocation;
			var isEdit = selectedAction===this.actions.edit;
			if(isEdit){
			   url=Configuration.manageQuotaServiceUrl.updateQuotaAllocation;
			   quotaAllocation["quotaAllocationID"]=that.oQuotaAllocation.quotaAllocationID;
			}
			Connection.postData(url, quotaAllocation, 
					function(response){
				        that.getView().getModel('quotas').setData(response);
				        that.toggleQuotaAllocation(that.actions.create);
				        if(!isEdit){
				            that._resetForm("VendorCategoryQuotaAllocation");
				        }
				        that.onCloseAllocationDialog();
					}, 
					function(xhr, textStatus, errorThrown){
						if(xhr.responseJSON.resultType!=="SUCCESS"){ 
							 sap.m.MessageToast.show(xhr.responseJSON.result); 
						} 
			});
		}else{
			sap.m.MessageToast.show(AppHelper.i18n().getProperty("validation_error_msg"));
		}
	}, 
	_resetForm:function(sFragment){ 
		if(sFragment==="VendorCategoryQuotaAllocation"){ 
			sap.ui.getCore().byId('category_quota_size_input').setValue(""); 
			sap.ui.getCore().byId("category_quota_combo").setSelectedKey(null);
			sap.ui.getCore().byId("category_quota_combo").setSelectedItem(null);
		}
	},
	/** 
	 * @param sFragmentName
	 * @returns
	 */
	_getDialogFragement:function(sFragmentName){ 
		var oDialogFragment = this._dialogFragments[sFragmentName]; 
		if (oDialogFragment) {
			return oDialogFragment;
		} 
		oDialogFragment = sap.ui.xmlfragment("view.admin."+sFragmentName, this);
		this.getView().addDependent(oDialogFragment); 
		return this._dialogFragments[sFragmentName] = oDialogFragment;
	},
	
	onAllocationCreateRequest : function (oEvent) {
	  if(!this.oQuotaAllocation){
		  this.oQuotaAllocation={};
	   }
	   this.oQuotaAllocation["action"]=this.actions.create;
	   this.getView().getModel("allocation").setData(this.oQuotaAllocation);
	   this._getDialogFragement("VendorCategoryQuotaAllocation").open();
	},

	onCloseAllocationDialog : function (oEvent) {
		this._getDialogFragement("VendorCategoryQuotaAllocation").close();
	},
	/**
	 * Create new user group
	 * @param oEvent
	 */
	onSaveUserGroup:function(oEvent){
		var selectedAction=oEvent.getSource().getAggregation("customData")[0].getProperty("value");
		var that=this; 
		var isValid=true;   
		var users_group_name_input=sap.ui.getCore().byId('users_group_name_input');
		var userGroupName = users_group_name_input.getValue().trim();
		if(userGroupName===""){
			users_group_name_input.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			users_group_name_input.setValueState(sap.ui.core.ValueState.None);
		}  
		var users_group_desc_input=sap.ui.getCore().byId('users_group_desc_input');
		var userGroupDesc = users_group_desc_input.getValue().trim();
		if(userGroupDesc.length==0){
			users_group_desc_input.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			users_group_desc_input.setValueState(sap.ui.core.ValueState.None);
		} 
		
		var oUserCategoryCombo=sap.ui.getCore().byId('user_group_type_combo');
		var sUserGroupCategory = oUserCategoryCombo.mProperties.selectedKey.trim();
		if(sUserGroupCategory.length==0){
			oUserCategoryCombo.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			oUserCategoryCombo.setValueState(sap.ui.core.ValueState.None);
		} 
		if(isValid){ 
			if(sUserGroupCategory==="Vendor"){
				var url=Configuration.manageVendorGroupUrl.create;
				var isEdit = selectedAction===this.actions.edit;
				if(isEdit){
				   url=Configuration.manageVendorGroupUrl.update;
				}
				var oVendorUserGroup={vendorUserGroupName:userGroupName,vendorUserGroupDesc:userGroupDesc};
				 Connection.postData(url, oVendorUserGroup, 
						function(response){
					        that.getView().getModel('vug').setData(response);
					        that.initFolderStructureDataRequest(); 
					        that._resetNewUserGroupForm();
					        that.onUserGroupCloseDialog(); 
						}, 
						function(xhr, textStatus, errorThrown){
				});
			}
			else if(sUserGroupCategory==="FairPrice"){
				var url=Configuration.manageFairPriceGroupUrl.create;
				var isEdit = selectedAction===this.actions.edit; 
				var oFairPriceUserGroup={ fairPriceUserGroupName:userGroupName,fairPriceUserGroupDesc:userGroupDesc};
				if(isEdit){
					   url=Configuration.manageFairPriceGroupUrl.update;
					   oFairPriceUserGroup["fairPriceGroupID"]=that.oUserGroup.fairPriceGroupID;
				}
				Connection.postData(url, oFairPriceUserGroup, 
						function(response){
					        that.getView().getModel('fug').setData(response);
					        that._resetNewUserGroupForm();
					        that.onUserGroupCloseDialog(); 
						}, 
						function(xhr, textStatus, errorThrown){
					
				});
			}
		}else{
			sap.m.MessageToast.show(AppHelper.i18n().getProperty("validation_error_msg"));
		}
		 
	},
	_resetNewUserGroupForm:function(){
		sap.ui.getCore().byId('users_group_name_input').setValue("");
		sap.ui.getCore().byId('users_group_desc_input').setValue("");
	},
	/**
	 * Close
	 * @param oEvent
	 */
	onUserGroupCloseDialog:function(oEvent){
		this._getDialogFragement("UserGroup").close();
	}, 
	/**
	 * Invoke new group creation
	 * @param oEvent
	 */
	getGroupModel:function(){
		var oModel=this.getView().getModel("group");
		if(typeof oModel === "undefined"){
			oModel=new sap.ui.model.json.JSONModel();
			this.getView().setModel(oModel,"group"); 
		} 
		return oModel;
	},
	onCreateNewGroup:function(oEvent){
		var oUserGroup={};
	    oUserGroup["action"]=this.actions.create;
	    this.getGroupModel().setData(oUserGroup);
	    this._getDialogFragement("UserGroup").open();
	    sap.ui.getCore().byId('users_group_name_input').setValue("");
		sap.ui.getCore().byId('users_group_desc_input').setValue("");
	    
	},
	/**
	 * 
	 * @param oEvent
	 */
	onEditGroupEvent:function(oEvent){
		 var selectedItems=this.byId('vendorUserGroupList').getSelectedItems();
		 if(selectedItems.length>0){
			var sPath=selectedItems[0].getBindingContext('vug').sPath;
		 	this.oUserGroup=this.getView().getModel('vug').getProperty(sPath);  
		 	this.oUserGroup["action"]=this.actions.edit;
		 	this.oUserGroup["key"]="Vendor";
		 	this.getGroupModel().setData(this.oUserGroup); 
		}
		selectedItems=this.byId('fairPriceUserGroupList').getSelectedItems();
		if(selectedItems.length>0){
			var sPath=selectedItems[0].getBindingContext('fug').sPath;
		 	this.oUserGroup=this.getView().getModel('fug').getProperty(sPath);  
		 	this.oUserGroup["action"]=this.actions.edit;
		 	this.oUserGroup["key"]="FairPrice";
		 	this.getGroupModel().setData(this.oUserGroup); 
		}  
		this._getDialogFragement("UserGroup").open();
		this.initGroupEditDetails();
	},
	/**
	 * Instantiate dialog with group details
	 */
	initGroupEditDetails:function(){ 
		var userGroupDesc="",userGroupName="";
		var key = this.oUserGroup.key;
		if(key==="FairPrice"){
			userGroupDesc=this.oUserGroup.fairPriceUserGroupDesc;
			userGroupName=this.oUserGroup.fairPriceUserGroupName;
		}
		if(key==="Vendor"){
			userGroupDesc=this.oUserGroup.vendorUserGroupDesc;
			userGroupName=this.oUserGroup.vendorUserGroupName;
		}
		sap.ui.getCore().byId('users_group_name_input').setValue(userGroupName);
		sap.ui.getCore().byId('users_group_desc_input').setValue(userGroupDesc);
	},
	/**
	 * 
	 * @param oEvent
	 */
	onItemGroupSelectedEvent:function(oEvent){
		var groupType= oEvent.getSource().getSelectedItem().getAggregation("customData")[0].getProperty("value");
		if(groupType==="vendor"){
			var selectedGroups=this.byId('fairPriceUserGroupList');
			if(selectedGroups.getSelectedItems().length>0){
				selectedGroups.removeSelections(true);
			}
		}
		if(groupType==="fairprice"){
			var selectedGroups=this.byId('vendorUserGroupList');
			if(selectedGroups.getSelectedItems().length>0){
				selectedGroups.removeSelections(true);
			}
		}
		this.toggleGroupManagementButtons(this.actions.edit);
	},
	onExit : function () {
		if (this._oPopover) {
			this._oPopover.destroy();
		}
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
	 * Quota allocation Item select 
	 * @param oEvent
	 */
	onItemSelectedEvent:function(oEvent){ 
		var sPath=oEvent.getParameter("listItem").getBindingContext('quotas').sPath;
		this.oQuotaAllocation=this.getView().getModel('quotas').getProperty(sPath);  
		this.toggleQuotaAllocation(this.actions.edit);
	},
	
	/**
	 * Handle the Quota allocation Edit
	 * @param oEvent
	 */
	handleEditSelectedAllocation:function(oEvent){ 
		 this._getDialogFragement("VendorCategoryQuotaAllocation").open(); 
		 this.oQuotaAllocation["action"]=this.actions.edit;
		 this.getView().getModel("allocation").setData(this.oQuotaAllocation);
		
	},
	/**
	 * Enable /Disable Vendor Category Edit
	 * @param oAction
	 * @returns {Boolean}
	 */
	comboEditable:function(oAction){
		if(oAction===this.actions.create){
			return true;
		}
		return false;
	},
	/**
	 * On Confidentiality level item select
	 * @param oEvent
	 */
	onConfidentialityLevelItemEvent:function(oEvent){ 
		this.toggleConfidentiality(this.actions.edit); 
	},
	
	groupDialogTitle:function(sAction){
		var sDailogTitle=AppHelper.i18n().getProperty("title_group_create");
		if(sAction===this.actions.edit){
			sDailogTitle=AppHelper.i18n().getProperty("title_group_edit");
		}
		return sDailogTitle;
	},
	
	/**
	 * Handle edit level of confidentiality request
	 * @param oEvent
	 */
	handleEditConfidentialityLevel:function(oEvent){ 
		 var sPath=this.byId('confidentialityLevelList').getSelectedItems()[0].getBindingContext('clevels').sPath;
		 this.oConfidentialityLevel=this.getView().getModel('clevels').getProperty(sPath);  
		 this.oConfidentialityLevel["action"]=this.actions.edit;
		 this.getView().getModel("clevel").setData(this.oConfidentialityLevel); 
		 this._getDialogFragement("ConfidentialityLevel").open(); 
		 this.initConfidentialityEditDetails();
	},
	
	initConfidentialityEditDetails:function(){ 
		
		var passCodeSecure_switch=sap.ui.getCore().byId('passCodeSecure_switch');
		passCodeSecure_switch.setState(this.isRestrictionActive(passCodeSecure_switch.getName()));  
		
		var allowShare_switch=sap.ui.getCore().byId('allowShare_switch'); 
		allowShare_switch.setState(this.isRestrictionActive(allowShare_switch.getName()));
		
		var limitAvailability_switch=sap.ui.getCore().byId('limitAvailability_switch');
		limitAvailability_switch.setState(this.isRestrictionActive(limitAvailability_switch.getName()));
		
		var allowDownload_switch=sap.ui.getCore().byId('allowDownload_switch');
		allowDownload_switch.setState(this.isRestrictionActive(allowDownload_switch.getName()));
		
		var allowReadAndWrite_switch=sap.ui.getCore().byId('allowReadAndWrite_switch');
		allowReadAndWrite_switch.setState(this.isRestrictionActive(allowReadAndWrite_switch.getName()));
	},
	/**
	 * Handle create new level of confidentiality request
	 * @param oEvent
	 */
	handleCreateConfidentialityLevel:function(oEvent){
		 if(!this.oConfidentialityLevel){
			this.oConfidentialityLevel={};
		 }
		 this.oConfidentialityLevel["action"]=this.actions.create;
		 this.getView().getModel("clevel").setData(this.oConfidentialityLevel);
		 this._getDialogFragement("ConfidentialityLevel").open();
	},
	
	handleActionOnConfidentiality:function(oEvent){
		var selectedAction=oEvent.getSource().getAggregation("customData")[0].getProperty("value");
		var oConfidentialityLevelMapping=[];
		var that=this;
		var isValid=true;
		var oConfidentialityCombo=sap.ui.getCore().byId('confidentiality_level_combo');
		var sConfidentialityLevel = oConfidentialityCombo.mProperties.selectedKey.trim();
		if(sConfidentialityLevel.length==0){
			oConfidentialityCombo.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			oConfidentialityCombo.setValueState(sap.ui.core.ValueState.None); 
		} 
		
		var passCodeSecure_switch=sap.ui.getCore().byId('passCodeSecure_switch');
		var isPassCodeSecured = passCodeSecure_switch.getState(); 
		oConfidentialityLevelMapping[0]={directoryShareRestriction:passCodeSecure_switch.getName(),restrictionState:isPassCodeSecured};
		 
		
		var allowShare_switch=sap.ui.getCore().byId('allowShare_switch');
		var allowShare = allowShare_switch.getState(); 
		oConfidentialityLevelMapping[1]={directoryShareRestriction:allowShare_switch.getName(),restrictionState:allowShare};
		
		
		var limitAvailability_switch=sap.ui.getCore().byId('limitAvailability_switch');
		var limitAvailability = limitAvailability_switch.getState(); 
		oConfidentialityLevelMapping[2]={directoryShareRestriction:limitAvailability_switch.getName(),restrictionState:limitAvailability};
		 
		
		var allowDownload_switch=sap.ui.getCore().byId('allowDownload_switch');
		var allowDownload = allowDownload_switch.getState();
		oConfidentialityLevelMapping[3]={directoryShareRestriction:allowDownload_switch.getName(),restrictionState:allowDownload};
		
		
		var allowReadAndWrite_switch=sap.ui.getCore().byId('allowReadAndWrite_switch');
		var allowReadAndWrite = allowReadAndWrite_switch.getState();
		oConfidentialityLevelMapping[4]={directoryShareRestriction:allowReadAndWrite_switch.getName(),restrictionState:allowReadAndWrite};
		if(isValid){
			for(var index=0;index<oConfidentialityLevelMapping.length;index++){
				oConfidentialityLevelMapping[index][oConfidentialityCombo.getName()]=sConfidentialityLevel;
			} 
			var url=Configuration.manageCLevelServiceUrl.create;
			var isEdit = selectedAction===this.actions.edit;
			if(isEdit){
			   url=Configuration.manageCLevelServiceUrl.update;
			}
			Connection.postData(url, oConfidentialityLevelMapping, 
					function(response){
				        that.getView().getModel('clevels').setData(response);  
				        that.toggleConfidentiality(that.actions.create)
				        that.onCloseConfidentialityDialog();
					}, 
					function(xhr, textStatus, errorThrown){
						if(xhr.responseJSON.resultType!=="SUCCESS"){ 
							 sap.m.MessageToast.show(xhr.responseJSON.result); 
						} 
			});
		}  
	},
	/**
	 * Close the confidentiality dialog
	 * @param oEvent
	 */
	onCloseConfidentialityDialog:function(oEvent){
		this._getDialogFragement("ConfidentialityLevel").close();
	},
	/**
	 * Formatter
	 * @param cLevelEnum
	 * @returns
	 */
	resolveConfidentialityLevel:function(cLevelEnum){
		var result = this.getView().getModel('cl').getData().result;
		for(var i=0;i<result.length;i++){
			var oConfidentialityLevel=result[i];
			if(oConfidentialityLevel.key===cLevelEnum){
				return oConfidentialityLevel.val;
			}
		}
	},
	/**
	 * Enable restrictions count
	 * @param oRestrictions
	 * @returns {Number}
	 */
	enabledRestrictionCount:function(oRestrictions){
		var enabledRestrictions=0;
		for(var i=0;i<oRestrictions.length;i++){ 
			if(oRestrictions[i].restrictionState){
				enabledRestrictions++;
			}
		}
		return enabledRestrictions;
	},
	/**
	 * Resolve the confidentiality level description
	 * @param cLevelEnum
	 * @returns {String}
	 */
	resolveConfidentialityLevelDesc:function(cLevelEnum){
		switch(cLevelEnum){
		case "LOW": 
		return "Defined for documents with low level of confidentiality.";
		case "MEDIUM": 
			return "Defined for documents with Medium level of confidentiality.";
		case "HIGH": 
			return "Defined for documents with High level of confidentiality.";
		default: 
			return "Unknown confidentiality level";
		}  
	},
	/**
	 * Is the restriction Active
	 * @param sRestriction
	 * @returns
	 */
	isRestrictionActive:function(sRestriction){
		var oRestrictions=this.oConfidentialityLevel.restrictions;
		for(var i=0;i<oRestrictions.length;i++){ 
			var oRestriction =oRestrictions[i];
			if(oRestriction.directoryShareRestriction===sRestriction){
				return oRestriction.restrictionState;
			}
		}
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
	 * Toggle the delete button
	 * @param isFolder
	 */
	toggleDeleteButton:function(isVisible){ 
	    this.getView().byId('delete_corp_docs').setVisible(isVisible);  
	    
	},
	/**
	 * Toggle the edit button
	 * @param isFolder
	 */
	toggleEditButton:function(isVisible){ 
	    this.getView().byId('btn_edit_content').setVisible(isVisible);  
	},
	/**
	 * Toggle the upload button
	 * @param isVisible
	 */
	toggleUploadButton:function(isVisible){ 
	    this.getView().byId('btn_upload_content').setVisible(isVisible);  
	},
	/**
	 * Toggle the create button
	 * @param isVisible
	 */
	toggleCreateButton:function(isVisible){ 
	    this.getView().byId('btn_create_folder').setVisible(isVisible);  
	},
	/**
	 * Remove document list selection
	 */
	removeDocumentListSelection:function(){
		this.byId('documentList').removeSelections(true);
		this.toggleDeleteButton(false);
		this.toggleEditButton(false);
	}
	
});