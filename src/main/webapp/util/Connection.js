jQuery.sap.declare("Connection");
jQuery.sap.require("sap.ui.core.format.DateFormat");
jQuery.sap.require("util.AppHelper");

Connection = {
	/**
	 * Handle server requests
	 * 
	 * @param url
	 * @returns
	 */
	requestData : function(url) { 
		var that=this;
		var model = new sap.ui.model.json.JSONModel();
		model.attachRequestFailed(function(data) {
			that.handleExpiredSession();
		});  
		model.loadData(url, null, false); 
		return model.getData();
	},
	/**
	 * Request With Call back data
	 * 
	 * @param url
	 * @param exceptionCallBack
	 * @param successCallBack
	 */
	requestWithCallbackData : function(url,exceptionCallBack,successCallBack) {  
		var that=this;
		var model = new sap.ui.model.json.JSONModel(); 
		var response={};
		model.attachRequestCompleted(function(data) { 
			try{  
				var isSuccess=data.getParameter("success");
				if(isSuccess){
					return successCallBack(data.getSource().getData());
				} 
				var errorObject=data.getParameter('errorobject');  
				if(errorObject.statusCode===200){
					that.handleExpiredSession();
					return false;
				}
				response=JSON.parse(errorObject.responseText);
				return exceptionCallBack(response); 
			}catch(e){
				response={result:"Exception occured",resultType:"ERROR"};
				return exceptionCallBack(response);
			}
		});
		model.loadData(url);  
	},
	/**
	 * Download Request
	 */
	dowloadRequest:function(oDocument,url){
//		var data={documentID:oDocument.openCmisID,parentID:oDocument.openCmisParentID,contentType:oDocument.contentType}; 
		url +="?documentID="+oDocument.openCmisID;
//		window.location=url+"?"+JSON.stringify(data); 
		this.downloadAttachment(url,oDocument.documentName);
	},
	/**
	 * Download Attachments
	 */
	downloadAttachment: function(documentUrl, documentName) {
		var http;
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			http = new XMLHttpRequest();
		} else {
			// code for IE6, IE5
			http = new ActiveXObject("Microsoft.XMLHTTP");
		}
		http.open("GET", documentUrl, true);
		http.responseType = "blob";
		http.onload = function() { //Call a function when the state changes.
			if (this.status === 200) {
				var blob = this.response;
				if (typeof window.navigator.msSaveBlob !== "undefined") {
					// IE workaround
					window.navigator.msSaveBlob(blob, documentName);
				} else {
					var URL = window.URL || window.webkitURL;
					var downloadUrl = URL.createObjectURL(blob);
					// use HTML5 a[download] attribute to specify filename
					var a = document.createElement("a");
					// safari doesn't support this yet
					if (typeof a.download === "undefined") {
						window.location = downloadUrl;
					} else {
						a.href = downloadUrl;
						a.download = documentName;
						// a.style.display = "none";
						document.body.appendChild(a);
						a.click();
					}

					setTimeout(function() {
						URL.revokeObjectURL(downloadUrl);
					}, 100); // cleanup
				}
			}
		};
		http.send();
	},
	/**
	 * Post data
	 * @param url
	 * @param data
	 * @param onSuccessCallback
	 */
	postData:function(url,data,onSuccessCallback,onErrorCallback){
		var that=this;
		AppHelper.showLoader();
		jQuery.ajax({
			method : "POST",
			url : url,
			contentType : "application/json",
			data: JSON.stringify(data),
			success : function(response,status,xhr) {
				AppHelper.hideLoader() ;
				if(xhr.getResponseHeader("com.sap.cloud.security.login")){  
					that.handleExpiredSession(); 
				}else{
					return onSuccessCallback(response); 
				}
				
			},
			error : function(xhr, textStatus, errorThrown) {  
				AppHelper.hideLoader() ;
				return onErrorCallback(xhr,textStatus);
			}
		}); 
	},
	getData:function(url,data,onSuccessCallback,onErrorCallback){
		AppHelper.showLoader();
		jQuery.ajax({
			method : "GET",
			url : url, 
			data: JSON.stringify(data),
			headers: {          
                Accept : "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
			},
			success : function() {  
				AppHelper.hideLoader() ;
				return onSuccessCallback(); 
				
			},
			error : function(xhr, textStatus, errorThrown) {  
				AppHelper.hideLoader() ;
				return onErrorCallback(xhr,textStatus,errorThrown);
			}
		});
	},
	/**
	 * Start the busy waiting cursor
	 */
	startWaitingCursor : function() {
		jQuery("*").addClass("waitingCursor");
	},
	/**
	 * Stop the busy waiting cursor
	 */
	stopWaitingCursor : function() {
		jQuery("*").removeClass("waitingCursor");
	},
	/**
	 * Handle expired sessions
	 */
	handleExpiredSession : function() {  
		window.location.reload();
		 
	},
	/**
	 * Redirect to the url sent
	 * @param url
	 */
	logout : function(redirectTo) { 
		AppHelper.showLoader();
		jQuery.ajax({
			method : "GET",
			url : "logout",
			success : function() {
				window.location.href = redirectTo;
				AppHelper.hideLoader();
			},
			error : function() {
				AppHelper.hideLoader();
				sap.ui.commons.MessageBox.alert(
						"An error has occurred while trying to logout!", "",
						"Please note");
				
			}
		});
	}

}