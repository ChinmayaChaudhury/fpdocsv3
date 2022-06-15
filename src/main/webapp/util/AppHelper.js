jQuery.sap.declare("AppHelper");
jQuery.sap.require("util.Configuration");


/**
* This is AppHelper class which is responsible for  
*
* @class AppHelper
* 
*/
AppHelper={
		/**
		 * 
		 * @param oTarget
		 * @param oType
		 * @param oMessage
		 */
		showMsgStrip: function(oTarget,oType,oMessage) {   
			oMsgStrip = new sap.m.MessageStrip( {
					text: oMessage,
					showCloseButton: true,
					showIcon: true,
					type: oType
			 }); 
			 oTarget.addContent(oMsgStrip); 
		}, 
		/***
		 * Display Busy loader
		 * 
		 * @method showLoader
		 * @memberOf util.AppHelper
		 */
		showLoader:function(){
			if(sap.ui.getCore().byId("busyBox") == undefined)
			   new sap.m.BusyDialog("busyBox",{text:'Please wait', title: 'Loading'});
			   sap.ui.getCore().byId('busyBox').open();
		},
		/***
		 * Hide Busy loader
		 * 
		 * @method hideLoader
		 */
		hideLoader:function(){
			sap.ui.getCore().byId('busyBox').close();
			
		},
        
	  
		/**
		 * Prepare the profile details
		 * @param that
		 */
		initProfileDetails:function(that){ 
			that.getView().setModel(AppHelper.profile(),"prof");  
		},
		/**
		 * Display the user profile
		 * @param that
		 * @param oEvent
		 */
		displayProfileDetails:function(that,oEvent){
			if (! that._oPopover) {
				that._oPopover = sap.ui.xmlfragment("view.all.ProfilePopOver", that);
				that.getView().addDependent(that._oPopover); 
			} 
			var oButton = oEvent.getSource();
			jQuery.sap.delayedCall(0, that, function () {
				that._oPopover.openBy(oButton);
			});
		}, 
		closeProfileDetails:function(that){
			that._oPopover.close();
		},
		 
		/**
		 * Hides keyboard if it is visible
		 * 
		 * @method hideKeyboard
		 */
		hideKeyboard:function() {
		    document.activeElement.blur();
		    $("input").blur();
		},
		/**
		 * Display alert message
		 * @param msg
		 */
		displayMsgAlert:function(msg,type){
			var msgIcon = (type)? sap.m.MessageBox.Icon.SUCCESS : sap.m.MessageBox.Icon.ERROR;
			type = (type)? type : "Error";
			sap.m.MessageBox.show(msg,msgIcon,type,["OK"]);
		},
		/**
		 * Message types
		 */
		messageType: {
			success: "SUCCESS",
			error: "ERROR",
			info: "INFO",
			alert: "ALERT",
			warning: "WARNING"
		},
		/**
		 * Display Alert message
		 */
		displayAlertMsg: function(oMessageType, vMessage) {
			switch (oMessageType) {
				case this.messageType.error:
					sap.m.MessageBox.error(vMessage, []);
					break;
				case this.messageType.alert:
					sap.m.MessageBox.alert(vMessage, []);
					break;
				case this.messageType.warning:
					sap.m.MessageBox.warning(vMessage, []);
					break;
				case this.messageType.success:
					sap.m.MessageBox.success(vMessage, []);
					break;
				case this.messageType.info:
				default:
					sap.m.MessageBox.information(vMessage, []);
					break;
			}

		},
    /**
		 * Display toast message
		 * @param msg
		 */
		displayToast: function (msg) {
		    sap.m.MessageToast.show(
					msg 
			);
		},
		/**
		 * Handle model
		 * @param that
		 * @param oModelName
		 * @returns {sap.ui.model.json.JSONModel}
		 */
		getModel:function(that,oModelName){
			var oModel=that.getView().getModel(oModelName);
			if(!oModel){ 
				oModel=new sap.ui.model.json.JSONModel();
				sap.ui.getCore().setModel(oModel,oModelName);
			}
			return oModel;
		},
		/**
		 * Get the i18n model
		 * @returns
		 */
		i18n : function() { 
			return sap.ui.getCore().getModel('i18n');
		},
		/**
		 * Get the i18n model
		 * @returns
		 */
		profile : function() { 
			return sap.ui.getCore().getModel('profile');
		},
		isValidEmail: function (oValue) { 
			var mailregex = /^\w+[\w-+\.]*\@\w+([-\.]\w+)*\.[a-zA-Z]{2,}$/;
			return oValue.match(mailregex);
		},
		isDomainValid:function(oValue){
			var mailregex = /^\*\@\w+([-\.]\w+)*\.[a-zA-Z]{2,}$/;
			return oValue.match(mailregex);
		}
		 
    
}