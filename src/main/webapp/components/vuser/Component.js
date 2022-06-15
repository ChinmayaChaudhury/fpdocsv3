/**
 * 
 */
jQuery.sap.declare("sap.docs.vuser.Component");
sap.ui.core.UIComponent.extend("sap.docs.vuser.Component",{
	metadata: {
		  name: "Document Collaboration Service",
		  version: "1.0",
		  routing : {
		    config: {
		      viewType: "XML",
		      viewPath: "view.vuser",
		      targetAggregation: "detailPages",
		      clearTarget: false
		    },
		    routes: [
		      {
		        pattern: "",
		        name: "main",
		        view: "Master",
		        targetAggregation: "masterPages",
		        targetControl: "applicationID",
		        subroutes: [
		  		          {
		  		            pattern: "menu/0",
		  		            name: "corp_document",
		  		            view: "CoorporateDocumentList",
		  		            transition: "show"
		  		          },
		  		          {
		  			        pattern: "menu/0/{arguments}",
		  			        name: "modify_vendor",
		  			        view: "VendorModify",
		  			        transition: "show"
		  			       },		          
		  		          {
		  			        pattern: "menu/1",
		  			        name: "inbound_share",
		  			        view: "DocumentList",
		  			        transition: "show"
		  			      },
		  			      {
		  				    pattern: "menu/1/{arguments}",
		  				    name: "modify_user",
		  				    view: "DocumentList",
		  				    transition: "show"
		  				  },
		  		          {
		  				    pattern: "menu/2",
		  				    name: "outbound_share",
		  				    view: "OutBoxDocumentManagement",
		  				    transition: "show"
		  				   },
		  				   {
		  					pattern: "menu/2/{arguments}",
		  					name: "document_share",
		  					view: "DocumentShare",
		  					transition: "show"
		  				   }
		  				   
		  		        ]
		      },
		      {
		        name: "catchallMaster",
		        view: "Master",
		        targetAggregation: "masterPages",
		        targetControl: "applicationID",
		        subroutes: [
		          {
		            pattern: ":all*:",
		            name: "catchallDetail",
		            view: "NotFound",
		            transition: "show"
		          }
		        ]}
		    ]}
			,
		  config: {
		    resourceBundle: "i18n/messageBundle.properties"
		  }, 
		},
		init : function() {  
			//UTILS Declaration
			jQuery.sap.require("util.Formatter");
			jQuery.sap.require("util.Configuration");
			jQuery.sap.require("util.Connection");
			jQuery.sap.require("util.AppHelper");
			jQuery.sap.require("sap.m.MessageBox");
			jQuery.sap.require("sap.m.MessageToast");
			///
	        sap.ui.core.UIComponent.prototype.init.apply(this,arguments); 
	        sap.ui.getCore().getConfiguration().setLanguage('en_US');
	        // configure
	        var mConfig = this.getMetadata().getConfig();

	        // Always use absolute paths relative to our own
	        // component
	        // (relative paths will fail if running in the Fiori
	        // Launchpad)
	        var oRootPath = jQuery.sap.getModulePath("com.sap.fp.docrepo");

	        // Set i18n model
	        var i18nModel = new sap.ui.model.resource.ResourceModel( { bundleUrl : [ oRootPath, mConfig.resourceBundle ].join("/") });
	        this.setModel(i18nModel, "i18n"); 
	        sap.ui.getCore().setModel(i18nModel, "i18n"); 
	        // Set device model
	        var oDeviceModel = new sap.ui.model.json.JSONModel({
	            isTouch: sap.ui.Device.support.touch,
	            isNoTouch: !sap.ui.Device.support.touch,
	            isPhone: sap.ui.Device.system.phone,
	            isNoPhone: !sap.ui.Device.system.phone,
	            listMode: sap.ui.Device.system.phone ? "None" : "SingleSelectMaster",
	            listItemType: sap.ui.Device.system.phone ? "Active" : "Inactive"
	        });
	        oDeviceModel.setDefaultBindingMode("OneWay");
	        this.setModel(oDeviceModel, "device"); 
							
	        // this component should automatically initialize the
	        // router!
	        this.router = this.getRouter();
	        // initialize the router and pass the local instance as
	        // a parameter
	        this.routeHandler = new sap.m.routing.RouteMatchedHandler(this.router);
	        this.router.register("vuserRouter");
	        this.router.initialize(); 
	    },
	    createContent : function() {
	        this.view = sap.ui.view({
	            id : this.createId("VUserApp"),
	            viewName : "view.vuser.App",
	            type : "XML"
	        }); 
	        return this.view;
	    },
	    getEventBus : function() {
	        return sap.ui.getCore().getEventBus();
	    }
	});					
