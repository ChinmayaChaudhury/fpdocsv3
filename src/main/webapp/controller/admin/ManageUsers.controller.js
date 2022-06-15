sap.ui.controller("controller.admin.ManageUsers", {

/**
* Called when a controller is instantiated and its View controls (if available) are already created.
* Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
* @memberOf controller.admin.ManageUsers
*/
	onInit : function() { 
		this.oInitialLoadFinishedDeferred = jQuery.Deferred(); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"users");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"ug");
		
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		if (sap.ui.Device.system.phone) {
			// Do not wait for the master when in mobile phone resolution
			this.oInitialLoadFinishedDeferred.resolve();
		} 
		this.loadRequiredData();

	},
	vendorRelActions:{add:"ADD",remove:"REMOVE",subscription:"SUBSCRIPTIONS"},
	actions:{edit:"edit",create:"create",none:"none"},
	onRouteMatched : function(oEvent) {
		var sName = oEvent.getParameter("name");
		if (sName !== 'admin_users') { 
			return;
		}else{
			this._showFragment("UserList");
			this.loadRequiredData();
		}
	},
	models:{user:"users"},
	loadRequiredData:function(){ 
		AppHelper.showLoader();
		this.getView().getModel('users').setData(Connection.requestData(Configuration.manageInternalUsersServiceUrl.queryInitializedUsers));
		this.getView().getModel('ug').setData(Connection.requestData(Configuration.manageSetupServiceUrl.queryFairPriceGroups));
		this.getModel("status").setData(sap.ui.getCore().getModel('status').getData());
		this.getModel("salutation").setData(sap.ui.getCore().getModel('salutation').getData()); 
		AppHelper.initProfileDetails(this);
		AppHelper.hideLoader();
	},
	/**
	 * Called when the new vendor button is clicked.
	 * 
	 * @memberOf controller.admin.ManageUsers
	 */
	onCreateNewUserPress : function(oEvent) {
		this.toggleToSaveButton();
		this.oUserData={};
		this.oUserData["action"]=this.actions.create;
		this.getModel("user").setData(this.oUserData); 
		this._showFragment("SAPInternalUserAccount");
	}, 
	getUploadDialog : function () {
		if (!this.oUploadDialog) {
			// create dialog via fragment factory
			this.oUploadDialog = sap.ui.xmlfragment("view.admin.UploadUsersDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oUploadDialog);
		}
		return this.oUploadDialog;
	},

	onCreateUserPressBulk : function () {
		this.getUploadDialog().open();
	},
	onCloseDialog:function(){
		this.getUploadDialog().close();
	},
 
	/**
	 * On User search Request
	 * @param oEvent
	 */
	onUserSearch:function(oEvent){
		if( typeof oEvent.getParameter('refreshButtonPressed')===typeof undefined){
			return false;
		}
	
		
		var sInput= oEvent.getParameter('query').trim();  
		if(AppHelper.isValidEmail(sInput)){ 
				var query=Configuration.manageInternalUsersServiceUrl.queryInitializedUsers+"?email="+sInput;   
				var onErrorCallBack=function(data){ 
					AppHelper.hideLoader();
					AppHelper.displayMsgAlert(data.result);  
				};
				var that=this;
				var onSuccessCallBack=function(data){
					var action = that.actions.create;
					var model=that.getModel("user");
					var result = data.result;
					if(Object.keys(result).length>0){
						result["action"]=action;
						model.setData(result);
					}else{  
						model.setData({action:action});	  
					}
					that.byId('email_input').setValue(sInput);
					AppHelper.hideLoader();
				}; 
				setTimeout(function(){
					AppHelper.showLoader(); 
					Connection.requestWithCallbackData(query,onErrorCallBack,onSuccessCallBack);  
				}, 1000); 
			 
		}else{	
			AppHelper.hideLoader();
			AppHelper.displayMsgAlert("'"+sInput+"' is an invalid email address");
		}
	},
	/**
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadPress:function(oEvent){
		
		var oFileUploader= sap.ui.getCore().byId("userFileUploader");
		if(!oFileUploader.getValue()){
			sap.m.MessageToast.show("Please choose file");
			return;
		} 
		
		var fairPriceGroupIDCombo= sap.ui.getCore().byId("user_group_combo_id");
		var fairPriceGroupID = fairPriceGroupIDCombo.mProperties.selectedKey.trim();
		if(!fairPriceGroupID){
			fairPriceGroupIDCombo.setValueState(sap.ui.core.ValueState.Error);
			sap.m.MessageToast.show("Select user group"); 
			return;
		}else{
			fairPriceGroupIDCombo.setValueState(sap.ui.core.ValueState.None); 
		} 
		AppHelper.showLoader();
		oFileUploader.upload();
		
	},
	/**
	 * Handle Upload on completion
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadComplete:function(oEvent){ 
		var sResponse=oEvent.getParameter("responseRaw");
		if(sResponse){  
			var oResponse=JSON.parse(sResponse);
			if(oResponse.resultType==='SUCCESS'){ 
				this.getView().getModel('users').setData(oResponse); 
			}else{ 
				oTarget= this.getView().byId('userListPage');
				for(var v=0;v<oResponse.result.length;v++){
					var resultType=oResponse.result[v].resultType;
					var result=oResponse.result[v].result; 
					if(resultType==="SUCCESS"){
						var oType="Success";
						AppHelper.showMsgStrip(oTarget,oType,result);
					}else{
						var oType="Error";
						AppHelper.showMsgStrip(oTarget,oType,result);
					}
				} 
			}
		}  
		sap.ui.getCore().byId("userFileUploader").setValue("");
		this.getUploadDialog().close();
		AppHelper.hideLoader();
	},

	handleTypeMissmatch: function(oEvent) {
		var aFileTypes = oEvent.getSource().getFileType();
		jQuery.each(aFileTypes, function(key, value) {aFileTypes[key] = "*." +  value});
		var sSupportedFileTypes = aFileTypes.join(", ");
		var sMsg = "*." + oEvent.getParameter("fileType") +
								" is not supported. Supported types are: " +
								sSupportedFileTypes; 
		jQuery.sap.require("sap.m.MessageToast");
		sap.m.MessageToast.show(sMsg);
	},

	handleValueChange: function(oEvent) {
		var sMsg = "Press 'Upload File' to upload file '" + oEvent.getParameter("newValue") + "'";
		jQuery.sap.require("sap.m.MessageToast");
		sap.m.MessageToast.show(sMsg);
	},
	onExit : function () {
		for ( var sPropertyName in this._fragments) {
			if (!this._fragments.hasOwnProperty(sPropertyName)) {
				return;
			} 
			this._fragments[sPropertyName].destroy();
			this._fragments[sPropertyName] = null;
		}
		if (this._oPopover) {
			this._oPopover.destroy();
		}
	},
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
	onFilterRequest : function(oEvent) {  
	    var aFilters = [];
	    var sQuery = oEvent.getSource().getValue();
	    if (sQuery && sQuery.length > 0) {
	      //First Name Filter
	      var filter = new sap.ui.model.Filter("firstName", sap.ui.model.FilterOperator.Contains, sQuery);
	      aFilters.push(filter);
	    } 
	    var list = this.getView().byId("userList");
	    var binding = list.getBinding("items");
	    binding.filter(aFilters, "Application"); 
	},
	
	onItemSelectEvent:function(oEvent){
		this.byId("btn_edit_user").setVisible(true); 
		this.toggleDeleteButton(true);
	},
	
	
	_fragments : {},
	
	_getFragment : function(sFragmentName) {
		var oFragment = this._fragments[sFragmentName]; 
		if (oFragment) {
			return oFragment;
		} 
		oFragment = sap.ui.xmlfragment(this.getView().getId(),"view.admin." + sFragmentName,this);
		this.getView().addDependent(oFragment);
		return this._fragments[sFragmentName] = oFragment;
	},
	
	_showFragment : function(sFragmentName) {
		var oPage = this.getView().byId("userDetailPage"); 
		oPage.removeAllContent();
		oPage.insertContent(this._getFragment(sFragmentName));
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
	onEditUserEvent:function(oEvent){
		var sPath= this.byId('userList').getSelectedItem().getBindingContext('users').sPath;
		this.oUserData=this.getView().getModel('users').getProperty(sPath); 
		this.oUserData["action"]=this.actions.edit;
		this.getModel("user").setData(this.oUserData);
		var url=Configuration.manageInternalUsersServiceUrl.vendorRel+this.vendorRelActions.subscription;
		var data={fairPriceUserID:this.oUserData.fairPriceUserID};
		var that=this;
		Connection.postData(url,data, 
				function(response){  
					that.getModel('vendorRel').setData(response); 
				  }, 
				function(xhr, textStatus, errorThrown){ 
					 var result = xhr.responseJSON.result;
   					 if(typeof result!=="undefined"){ 
						 AppHelper.displayMsgAlert(result);	
						}
					  else{
						AppHelper.displayMsgAlert("Error occured"); 
				} 
			} 
		);	
		this.toggleToSaveButton();
		this._showFragment("UserChange");
	},
	toggleToSaveButton:function(){  
		this.byId("btn_edit_user").setVisible(false);  
		this.byId("btn_new_user").setVisible(false);  
		this.byId("bulkUserCreate").setVisible(false); 
		this.toggleDeleteButton(false);
		this.byId("btn_cancel_user").setVisible(true);  
		this.byId("btn_save_user").setVisible(true);  
	},
	toggleToListViewButton:function(){  
		var isVisible=this.byId('userList').getSelectedItems().length>0;
		this.byId("btn_edit_user").setVisible(isVisible);  
		this.toggleDeleteButton(isVisible);
		this.byId("btn_new_user").setVisible(true);  
		this.byId("bulkUserCreate").setVisible(true);  
		this.byId("btn_cancel_user").setVisible(false);  
		this.byId("btn_save_user").setVisible(false);  
	},
	onCancelUserModifyEvent:function(oEvent){
		this.toggleToListViewButton();
		this._showFragment("UserList");
	},
	
	onSaveUserEvent : function(oEvent) {
		var selectedAction=oEvent.getSource().getAggregation("customData")[0].getProperty("value");
		var that=this; 
		if(selectedAction===this.actions.create){
			try{
				var url=Configuration.manageInternalUsersServiceUrl.create;
				SCIMAccount.getInternalUserData(that);
				Connection.postData(url, this.getModel("user").getData(), 
						function(response){ 
							that.getView().getModel('users').setData(response); 
					        that.onCancelUserModifyEvent();
						}, 
						function(xhr, textStatus, errorThrown){ 
							var result = xhr.responseJSON.result;
							if(typeof result!=="undefined"){ 
								AppHelper.displayMsgAlert(result);	
							}
							else{
								AppHelper.displayMsgAlert("Error occured"); 
							}
					
				});
			}catch (e) {
				AppHelper.displayMsgAlert(e.message); 
			}
		}else{ 
			var isValid=true;
			var sapUserIDInput = this.byId('user_sap_serviceid_input');
			var sapServiceID = sapUserIDInput.getValue().trim();
			if(sapServiceID===""){
				sapUserIDInput.setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}else{
				sapUserIDInput.setValueState(sap.ui.core.ValueState.None); 
			}  
			
			
			var firstNameInput = this.byId('user_first_name_input');
			var firstName = firstNameInput.getValue().trim();
			if(firstName===""){
				userIDPUIDInput.setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}else{
				firstNameInput.setValueState(sap.ui.core.ValueState.None); 
			}  
			
			
			var lastNameInput = this.byId('user_last_name_input');
			var lastName = lastNameInput.getValue().trim();
			if(lastName===""){
				lastNameInput.setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}else{
				lastNameInput.setValueState(sap.ui.core.ValueState.None); 
			}  
			
			var displayNameInput = this.byId('user_displayname_input');
			var displayName = displayNameInput.getValue().trim();
			if(displayName===""){
				displayNameInput.setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}else{
				displayNameInput.setValueState(sap.ui.core.ValueState.None); 
			} 
			
			var fairPriceEmailInput = this.byId('user_email_input');
			var fairPriceEmail = fairPriceEmailInput.getValue().trim();
			if(fairPriceEmail===""){
				fairPriceEmailInput.setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}else{
				if(!AppHelper.isValidEmail(fairPriceEmail)){
					fairPriceEmailInput.setValueState(sap.ui.core.ValueState.Error);
					isValid=false;
				}else{
				  fairPriceEmailInput.setValueState(sap.ui.core.ValueState.None); 
				}
			}  
			
			
			var fairPriceGroupIDCombo=this.byId('user_group_combo');
			var fairPriceGroupID = fairPriceGroupIDCombo.mProperties.selectedKey.trim();
			if(fairPriceGroupID.length==0){
				fairPriceGroupIDCombo.setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}else{
				fairPriceGroupIDCombo.setValueState(sap.ui.core.ValueState.None); 
			} 
			var accountStatusCombo=this.byId('accountStatus_combo');
			var accountStatus = accountStatusCombo.mProperties.selectedKey.trim();
			if(accountStatus.length==0){
				accountStatusCombo.setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}else{
				accountStatusCombo.setValueState(sap.ui.core.ValueState.None); 
			}  
			if(isValid){
				var url=Configuration.manageInternalUsersServiceUrl.update;  
				var user=this.getView().getModel("user").getData();
				Connection.postData(url, user, 
						function(response){ 
							that.getView().getModel('users').setData(response); 
					        that.onCancelUserModifyEvent();
						}, 
						function(xhr, textStatus, errorThrown){ 
							var result = xhr.responseJSON.result;
							if(typeof result!=="undefined"){ 
								AppHelper.displayMsgAlert(result);	
							}
							else{
								AppHelper.displayMsgAlert("Error occured"); 
							}
					
				});
				 
			}else{
				sap.m.MessageToast.show(AppHelper.i18n().getProperty("validation_error_msg"));
			}
		}
		
	},
	/**
	 * On delete user event
	 * @param oEvent
	 */
	onDeleteUserEvent:function(oEvent){
		var that=this;
		sap.m.MessageBox.show( AppHelper.i18n().getProperty("msg_delete_user"), {
            icon: sap.m.MessageBox.Icon.ERROR,
            title: AppHelper.i18n().getProperty("title_delete_user"),
            actions: [sap.m.MessageBox.Action.YES,sap.m.MessageBox.Action.NO],
            onClose: function (oAction) { 
            	if(oAction===sap.m.MessageBox.Action.YES){
            		var oUserList=that.byId('userList');
					var sPath= oUserList.getSelectedItem().getBindingContext('users').sPath;
            		var oInternalUser=that.getView().getModel('users').getProperty(sPath);  
            		var url=Configuration.manageInternalUsersServiceUrl.drop;
    				Connection.postData(url, oInternalUser, 
    						function(response){  
    					        that.getView().getModel('users').setData(response); 
    					        that.toggleToListViewButton();
    						}, 
    						function(xhr, textStatus, errorThrown){ 
    							var result = xhr.responseJSON.result;
    							if(typeof result!=="undefined"){ 
    								AppHelper.displayMsgAlert(result);	
    							}
    							else{
    								AppHelper.displayMsgAlert("Error occured"); 
    							}
    					
    				}); 
            	}
            }
		});
		
	},
	toggleDeleteButton:function(isVisible){  
		this.byId("btn_delete_user").setVisible(isVisible); 
	}, 
	handleChangedSelectItem:function(oAction){
		var selectedKey=oAction.getSource().getSelectedKey();
		this.getModel("bulk").setData({"fairPriceGroupID":selectedKey});
		
	},
	getFragmentTitle:function(sAction){
		var sDailogTitle=AppHelper.i18n().getProperty("title_create_user");
		if(sAction===this.actions.edit){
			sDailogTitle=AppHelper.i18n().getProperty("title_update_user");
		}
		return sDailogTitle;
	},
	getUserGroupName:function(iGroupID){
		var result = this.getView().getModel("ug").getData().result;
		for ( var i=0;i<result.length;i++) {
			var oGroup=result[i];
			if(oGroup.fairPriceGroupID===iGroupID){
				return oGroup.fairPriceUserGroupDesc;
			}
		} 
	},
	/**
	 * 
	 * @param oEvent
	 */
	onSearchVendorEvent:function(oEvent){
		if( typeof oEvent.getParameter('refreshButtonPressed')===typeof undefined){
			return false;
		}
		var that=this; 
		var sInput= oEvent.getParameter('query').trim(); 
		AppHelper.showLoader(); 
		if(sInput){
			var query=Configuration.manageVendorServiceUrl.query+"?vendorName="+sInput;   
				var onErrorCallBack=function(data){ 
					AppHelper.hideLoader();
					AppHelper.displayMsgAlert(data.result); 
					
				};
				var onSuccessCallBack=function(data){ 
					var model=that.getModel("vendors");  
					if(Object.keys(data).length>0){ 
						model.setData(data);
					} 
					that._getFragment("VendorListDialog").open();
					AppHelper.hideLoader();
				};
				Connection.requestWithCallbackData(query,onErrorCallBack,onSuccessCallBack);  
			 
		}else{	
			AppHelper.hideLoader();
			AppHelper.displayMsgAlert("Input cannot be empty");
		}
	},
	/**
	 * 
	 * 
	 * @param oEvent
	 */
	onAddSelectedVendors:function(oEvent){
		var selectedItems=this.getView().byId('availableVendorList').getSelectedItems();  
		var selectedVendors=[];
		for ( var selectedIndex in selectedItems) {
			var oVendor=this.getModel('vendors').getProperty(selectedItems[selectedIndex].getBindingContext('vendors').sPath); 
			selectedVendors.push(oVendor);
		} 
		if(selectedVendors.length!=0){
			var url=Configuration.manageInternalUsersServiceUrl.vendorRel+this.vendorRelActions.add;
			var data={vendors:selectedVendors,user:this.getModel('user').getData()};
			var that=this;
			Connection.postData(url,data, 
					function(response){  
						that.getModel('vendorRel').setData(response); 
						that.onCloseVendorDialog();
    				}, 
    				function(xhr, textStatus, errorThrown){
    					 var result=xhr.responseJSON;
    					 var failed =result.failed; 
       					 if(typeof failed!== typeof undefined){ 
       						 var msg="";
       						 for(var count in failed){
       							msg=msg+ failed[count].vendorName+" ";
       							if(count!==(failed.length-1)){
       								msg=msg+","
       							}
       						  } 
       						  msg=msg+ " failed";
       						  AppHelper.displayMsgAlert(msg)
    						}
    					  else{
    						that.getModel('vendorRel').setData(result); 
    					} 
    			} 
			);	
		} 
	}
	,
	/**
	 * On Close Vendor Dialog
	 * @param oEvent
	 */
	onCloseVendorDialog:function(oEvent){
		this._getFragment("VendorListDialog").close();
	},
	/**
	 * On Remove Vendor Request
	 * @param oEvent
	 */
	onRemoveVendorRequestEvent:function(oEvent){
		var url=Configuration.manageInternalUsersServiceUrl.vendorRel+this.vendorRelActions.remove;
		var sPath=oEvent.getParameter("listItem").getBindingContext('vendorRel').sPath;  
		var data=this.getModel('vendorRel').getProperty(sPath);
		var that=this;
		Connection.postData(url,data, 
				function(response){  
					that.getModel('vendorRel').setData(response); 
				}, 
				function(xhr, textStatus, errorThrown){
					 var result=xhr.responseJSON.result; 
   					 if(typeof result!== typeof undefined){ 
   						  AppHelper.displayMsgAlert(result)
					 }else{
						 AppHelper.displayMsgAlert("Error encountered!!")
					} 
			} 
		);
	}
});