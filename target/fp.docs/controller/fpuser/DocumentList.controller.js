sap.ui.controller("controller.fpuser.DocumentList", {

	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf controller.fpuser.DocumentList
	 */
	onInit : function() {  
		this.actions={upload:"upload",drilldown:"drilldown",none:"none"};
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"inbox");
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this); 
		AppHelper.initProfileDetails(this);
	},
	loadRequiredData:function(){
		AppHelper.showLoader()
		this.getView().getModel('inbox').setData(Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.documentInBox));
		AppHelper.hideLoader();
	},
	/**
	 * handle routing 
	 */
	onRouteMatched:function(oEvent){
		var sName=oEvent.getParameter('name');
		if(sName==="inbound_share"){ 
			this.loadRequiredData();
			this.byId("inboxList").removeSelections(true);
			this.toggleDownloadButton(true);
		}else{
			return;
		}   
	},
	getModel:function(oModelName){
		var oModel=this.getView().getModel(oModelName);
		if(!oModel){ 
			oModel=new sap.ui.model.json.JSONModel();
			sap.ui.getCore().setModel(oModel,oModelName);
		}
		return oModel;
	},
	
	/**
	 * Handle the click events
	 */
	onItemClickEvent : function(oEvent) { 
		this.oDocument=this.getView().getModel('inbox').getProperty(oEvent.getSource().getBindingContext('inbox').sPath); 
		var isPasscodeSecured=this.isPasscodeSecured(this.oDocument.restriction);
		if(isPasscodeSecured){ 
			if(this.oDocument.contentType==="doc/folder"){
				this.currentAction=this.actions.drilldown; 
				this.getPasscodeDialog().open();
			}else{
				this.initDownload(this.oDocument);
			}
		}else{
			if(this.oDocument.contentType==="doc/folder"){
				this.loadLocationContent(this.oDocument.openCmisID,this.oDocument.openCmisParentID,this.oDocument.contentType);
			}
			else{
				this.initDownload(this.oDocument);
			}
		}
	},
	
	onItemSelectedEvent:function(oEvent){  
		this.oDocument=this.getView().getModel('inbox').getProperty(oEvent.getSource().getSelectedItem().getBindingContext('inbox').sPath);
		this.toggleButton(this.oDocument.contentType);
		
	},
	getUploadDialog : function () {
		if (!this.oUploadDialog) {
			// create dialog via fragment factory
			this.oUploadDialog = sap.ui.xmlfragment("view.all.UploadDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oUploadDialog);
		}
		return this.oUploadDialog;
	},
	onCloseDialog : function (oEvent) {
		this.getUploadDialog().close();
	},
	handleUpload:function(oEvent){
		var oRequestModel=new sap.ui.model.json.JSONModel();
		this.getView().setModel(oRequestModel,"req"); 
		var oSelectedItem=this.getView().getModel('inbox').getProperty(this.byId('inboxList').getSelectedItem().getBindingContext('inbox').sPath);
		var data={openCmisID:oSelectedItem.openCmisID};
		oRequestModel.setData(data);  
		if(isPasscodeSecured(oSelectedItem.restriction)){
			this.currentAction=this.actions.upload; 
			this.getPasscodeDialog().open();
		}else{
		   this.getUploadDialog().open(); 
		}
	}, 
	updateWithContextItems:function(sPath){
		jQuery.sap.require("sap.m.MessageToast");   
		this.shared=this.oModel.getProperty(sPath,this.oModel);
		if(this.shared.contentType==="doc/folder"){  
			this.getView().getModel("inbound").setData(this.shared); 
		}  
	},
	/**
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadPress:function(oEvent){
		var oFileUploader= sap.ui.getCore().byId("docShareFileUploader");
		if(!oFileUploader.getValue()){
			sap.m.MessageToast.show("Please choose file");
			return;
		} 
		oFileUploader.upload();
	},
	/**
	 * Handle Upload on completion
	 * @memberOf controller.fpuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadComplete:function(oEvent){ 
		var sResponse=oEvent.getParameter("responseRaw");
		if(sResponse){ 
			var oResponse=JSON.parse(sResponse);
			if(oResponse.resultType==='SUCCESS'){ 
				this.getUploadDialog().close();
			}else{
			   sap.m.MessageToast.show(oResponse.result);
			}
		} 
		sap.ui.getCore().byId("docShareFileUploader").setValue("");
	}, 
	toggleBackButton:function(isFolder){ 
	    this.getView().byId('inbox_docs_back').setVisible(isFolder);  
	},
	toggleDownloadButton:function(isFolder){ 
	    this.getView().byId('btnDownload').setVisible(!isFolder);  
	},
	toggleButton:function(oContentType){
		var visible=oContentType!=="doc/folder"?true:false;
	    this.getView().byId('btnDownload').setVisible(visible); 
	}, 
	loadLocationContent:function(oOpenCmisDocumentID,oOpenCmisDocumentParent,oContentType){ 
		var that=this;
		var data={documentID:oOpenCmisDocumentID,parentID:oOpenCmisDocumentParent};   
		if(oContentType==="doc/folder"){ 
				var url=Configuration.fairPriceUserAccountServiceUrl.documentQuery;
				Connection.postData(url, data, 
						function(response){
								if(response.result.length>0){
									that.getView().getModel('inbox').setData(response); 
								    that.updateBreadCrumb(false);
									
								}else{
									sap.m.MessageToast.show("No items found"); 
								}
					             
						}, 
						function(xhr, textStatus, errorThrown){
							if(xhr.responseJSON.resultTyp!=="SUCCESS"){ 
								 sap.m.MessageToast.show(xhr.responseJSON.result); 
						} 
				}); 
		}  
	} 
	, 
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
	
	isPasscodeSecured:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		} 
		return oRestrictions.hasOwnProperty("PassCodeSecure");
	},
	isWriteAllowed:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		}
		return oRestrictions.hasOwnProperty("AllowReadAndWrite");
	},
	isLimitedAvailability:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		}
		return oRestrictions.hasOwnProperty("LimitAvailability");
	},
	isDownloadable:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		}
		return oRestrictions.hasOwnProperty("AllowDownload");
	},
	restrictionCount:function(oRestrictions){
		if(!oRestrictions){
			return 0;
		}else{
			return Object.keys(oRestrictions).length;
		}
	} ,
	
	documentRestrictions:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction;
		}
		var restrictions=Object.keys(oRestrictions);
		var sRestriction="";
		var length = restrictions.length;
		for(var i=0;i<length;i++){
			sRestriction=sRestriction+restrictions[i];
			if((length-1)!==i){
				sRestriction=sRestriction+", "
			}
		}
		return sRestriction;
	},
	getPasscodeDialog : function () {
		if (!this.oPasscodeDialog) {
			// create dialog via fragment factory
			this.oPasscodeDialog = sap.ui.xmlfragment("view.all.PasscodeDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oPasscodeDialog);
		}
		return this.oPasscodeDialog;
	},
	 
	onAuthenticationPress:function(oEvent){
		var that=this;
		var passCodeInput=sap.ui.getCore().byId("location_passcode_input");
		var passCode=passCodeInput.getValue().trim(); 
		var isValid=true;
		if(passCode===""){
			passCodeInput.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			passCodeInput.setValueState(sap.ui.core.ValueState.None); 
		} 
		if(isValid){
			var data={documentShareID:this.oDocument.documentShareID,passCode:passCode};
			var url=Configuration.fairPriceUserAccountServiceUrl.authenticate;
			Connection.postData(url, data, 
				function(response){ 
					that.getPasscodeDialog().close();
					if(that.currentAction===that.actions.upload){
						that.getUploadDialog().open();
					}else{
						that.loadLocationContent(that.oDocument.openCmisID,that.oDocument.openCmisParentID,that.oDocument.contentType);  
					}
					passCodeInput.setValue("");
				}, 
				function(xhr, textStatus, errorThrown){ 
					sap.m.MessageToast.show(xhr.responseJSON.result);  
				}
			);
		}else{
			sap.m.MessageToast.show(AppHelper.i18n().getProperty("validation_error_msg"));
		}
	}
	,
	onClosePress:function(oEvent){
		this.getPasscodeDialog().close();
	}, 
	 
	/**
	 * Handle download
	 */
	handleDownload:function(oEvent){ 
		var sPath=this.byId('inboxList').getSelectedItem().getBindingContext("inbox").sPath;
		var oDocument=this.getView().getModel('inbox').getProperty(sPath);
		this.initDownload(oDocument);
	},
	
	
	/**
	 * initialize download
	 */
	initDownload:function(oDocument){
		var url=Configuration.fairPriceUserAccountServiceUrl.documentDownload; 
		Connection.dowloadRequest(oDocument, url);
	},
	/**
	 * 
	 * @param oCount
	 */
	formatCount:function(oCount){
		if(oCount===0){
			return "";
		}
		return oCount;
	},
	sharedBy:function(oFirstName, oLastName,oVendorAccount){
		var vendorName="";
		if(!oFirstName ||!oLastName){
			return "";
		}
		if(oVendorAccount){
			vendorName=oVendorAccount.vendorCode+":"+oVendorAccount.vendorName
		}
		return "SharedBy:"+oFirstName+" "+oLastName+", "+vendorName; 
	},
	onFilterRequest:function(oEvent){
		var aFilters = [];
	    var sQuery = oEvent.getSource().getValue();
	    if (sQuery && sQuery.length > 0) {
	      var filter = new sap.ui.model.Filter("documentName", sap.ui.model.FilterOperator.Contains, sQuery);
	      aFilters.push(filter);
	    } 
	    var list = this.getView().byId("inboxList");
	    var binding = list.getBinding("items");
	    binding.filter(aFilters, "Application");
	},
	/**
	 * 
	 */
	updateBreadCrumb:function(isBase){ 
		var sInboxHeader=AppHelper.i18n().getProperty("hdr_inbox_doc");
		if(!isBase){
			var sDocumentName=this.oDocument.documentName; 
			var oRootLabel=new sap.m.Label({text:sInboxHeader,design:"Bold"});
			var pathIcon=new sap.ui.core.Icon({src:"sap-icon://slim-arrow-right"});
			var infoToolBar=new sap.m.OverflowToolbar({design:"Solid",content:[oRootLabel,pathIcon,new sap.m.Label({text:sDocumentName,design:"Bold"})]}); 
			this.byId("documentListPanel").setInfoToolbar(infoToolBar );
			this.toggleBackButton(true);
		}else{ 
			var oRootLabel=new sap.m.Label({text:sInboxHeader,design:"Bold"});
			var infoToolBar=new sap.m.OverflowToolbar({design:"Solid",content:[oRootLabel]}); 
			this.byId("documentListPanel").setInfoToolbar(infoToolBar );
			this.toggleBackButton(false);
		} 
	},
	/**
	 * 
	 * @param oEvent
	 */
	onBackPress:function(oEvent){
		this.updateBreadCrumb(true);
		this.loadRequiredData();
		this.toggleDownloadButton(true);
		this.byId("inboxList").removeSelections(true);
	},
	onProfileClosePress:function(oEvent){
		AppHelper.closeProfileDetails(this);
	}
});