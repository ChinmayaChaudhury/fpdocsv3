/**
 * 
 */
jQuery.sap.declare("sap.docs.cmis.Component");
sap.ui.core.UIComponent.extend("sap.docs.cmis.Component",
				{
					metadata : {
						name : "Document Collaboration Service",
						// indicates the component uses a descriptor
						// manifest : "json",// indicates that the component
						// uses the descriptor
						// which is on manifest.json file
						libary : "sap.ui.core",// this states the library or
						// package where the
						// component can be found
						version : "1.0", // version of the component
						routing : {
							config : {
								viewType : "XML",
								viewPath : "view.main", // the path to the views in
								// this case the views are
								// in the shoppingcart
								// folder
								targetControl : "idAppControl",
								targetAggregation : "pages", // "pages",
								clearTarget : false
							},
							routes : [ {
								pattern : "",
								name : "InitialView",
								view : "InitialView"
							}, {

								pattern : "tclaim",
								name : "tclaim",
								view : "ThirdPartyClaimView"

							},

							{

								pattern : "tclaimsz",
								name : "tclaimsz",
								view : "SplitContainer",
								targetAggregation : "pages",
								targetControl : "idAppControl",
								subroutes : [ {
									pattern : "tclaimx",
									name : "tclaimx",
									view : "ThirdPartyClaimView",
									targetAggregation : "masterPages",
									targetControl : "idSplitContainerControl"
								} ]
							} ]

						},
						config : {
							resourceBundle : "i18n/messageBundle.properties",
							serviceConfig : {
								name : "RMTSAMPLEFLIGHT",
								serviceUrl : "/destinations/RMTSAMPLEFLIGHT/sap/opu/odata/IWFND/RMTSAMPLEFLIGHT/"
							}
						},
					},
					init : function() {

						sap.ui.core.UIComponent.prototype.init.apply(this,
								arguments);

						// configure
						var mConfig = this.getMetadata().getConfig();

						// Always use absolute paths relative to our own
						// component
						// (relative paths will fail if running in the Fiori
						// Launchpad)
						var oRootPath = jQuery.sap.getModulePath("com.sap.fp.docrepo");

						// Set i18n model
						var i18nModel = new sap.ui.model.resource.ResourceModel(
								{
									bundleUrl : [ oRootPath,
											mConfig.resourceBundle ].join("/")
								});
						this.setModel(i18nModel, "i18n");

						// Set device model
						var oDeviceModel = new sap.ui.model.json.JSONModel(
								{
									isTouch : sap.ui.Device.support.touch,
									isNoTouch : !sap.ui.Device.support.touch,
									isPhone : sap.ui.Device.system.phone,
									isNoPhone : !sap.ui.Device.system.phone,
									listMode : sap.ui.Device.system.phone ? "None"
											: "SingleSelectMaster",
									listItemType : sap.ui.Device.system.phone ? "Active"
											: "Inactive"
								});
						sap.ui.getCore().getConfiguration()
								.setLanguage('en_US');
						oDeviceModel.setDefaultBindingMode("OneWay");
						this.setModel(oDeviceModel, "device");

						// this component should automatically initialize the
						// router!
						this.router = this.getRouter();
						// initialize the router and pass the local instance as
						// a parameter
						this.routeHandler = new sap.m.routing.RouteMatchedHandler(this.router);
						this.router.register("appRouter");
						this.router.initialize();
						
					},
					createContent : function() {
						this.view = sap.ui.view({
							id : this.createId("App"),
							viewName : "view.main.App",
							type : "XML"
						});
						return this.view;
					},
					getEventBus : function() {
						return sap.ui.getCore().getEventBus();
					}
				});