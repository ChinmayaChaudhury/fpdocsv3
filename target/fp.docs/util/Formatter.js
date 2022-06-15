sap.ui.define([], function () {
    "use strict";
    return {
        resolveAccountVerification: function (sStatus) {
            if (sStatus === true || sStatus === "true") {
                return "Account verified";
            } else {
                return "Account un-verified";
            }
        },
        status: function (sStatus) {
            if (sStatus === true || sStatus === "true") {
                return "Success";
            } else {
                return "Warning";
            }
        },
        count: function (value) {
            return (value) ? value.length : 0;
        },
        accountStatus: function (value) {
            if (value === "1") {
                return true;
            } else {
                return false;
            }
        },
        vendorCategoryName: function (vendorCategoryEnum, oVendorCategory) {
            for (var index in oVendorCategory) {
                if (oVendorCategory[index].key === vendorCategoryEnum) {
                    return oVendorCategory[index].val;
                }
            }
            return "Not found";

        },
        showUtilizationState: function (oUtilPerc) {
            if (oUtilPerc < 70) {
                return "Success";
            } else if ((oUtilPerc > 70) && (oUtilPerc < 90)) {
                return "Warning";
            } else {
                return "Error";
            }
        },
        showSpaceRecycler: function (oUtilPerc) {
            return oUtilPerc > 95;
        },
        emailDomain: function(value) {
            return value || "Email not set";
        },
        vendorCategoryName: function(vendorCategoryEnum, oVendorCategory) {
            for (var index in oVendorCategory) {
                if (oVendorCategory[index].key === vendorCategoryEnum) {
                    return oVendorCategory[index].val;
                }
            }
            return "Not found";
    
        },
        selectOneItem: function(sPath, selectedField, model, data, isValid) {
            var index = Number(sPath.substr(sPath.length - 1, sPath.length));
            sPath = sPath.substr(0, sPath.length - 1);
            for (var i = 0; i < data.length; i++) {
                model.setProperty(sPath + i + "/" + selectedField, false);
                if (i === index && isValid) {
                    model.setProperty(sPath + i + "/" + selectedField, true);
                }
            }
        },
        getUserGroupName: function (iGroupID) {
            var result = this.getView().getModel("ug").getData().result;
            for (var i = 0; i < result.length; i++) {
                var oGroup = result[i];
                if (oGroup.fairPriceGroupID === iGroupID) {
                    return oGroup.fairPriceUserGroupDesc;
                }
            }
        },
        status: function(sStatus) {
            if (sStatus === true || sStatus === "true") {
                return "Success";
            } else {
                return "Warning";
            }
        },
        resolveAccountVerification: function(sStatus) {
            if (sStatus === true || sStatus === "true") {
                return "Account verified";
            } else {
                return "Account un-verified";
            }
        },
        showSwitchField:function(oValue){
            return (oValue)?true:false;
        },
        date: function(value) {
            if (value) {
                var oDateFormat = sap.ui.core.format.DateFormat
                    .getDateTimeInstance({
                        pattern: "yyyy-MM-dd"
                    });
                return oDateFormat.format(new Date(value));
            } else {
                return value;
            }
        },
        expireOn: function(value) {
            if (value) {
                var oDateFormat = sap.ui.core.format.DateFormat
                    .getDateTimeInstance({
                        pattern: "yyyy-MM-dd"
                    });
                return "Expiry Date:" + oDateFormat.format(new Date(value));
            } else {
                return value;
            }
        },
        quantity: function(value) {
            try {
                return (value) ? parseFloat(value).toFixed(0) : value;
            } catch (err) {
                return "Not-A-Number";
            }
        },
        count: function(value) {
            return (value) ? value.length : 0;
        },
        emailDomain: function(value) {
            return value || "Email not set";
        },
        fileIcon: function(fileType) {
            switch (fileType) {
                case "doc/folder":
                    return "sap-icon://folder-full";
                case "doc/html":
                    return "sap-icon://attachment-html";
                case "doc/xml":
                    return "sap-icon://document";
                case "doc/pdf":
                    return "sap-icon://pdf-attachment";
                case "doc/xls":
                case "doc/xlsx":
                    return "sap-icon://excel-attachment";
                case "doc/txt":
                    return "sap-icon://attachment-text-file";
                case "doc/zip":
                    return "sap-icon://attachment-zip-file";
                case "doc/video":
                    return "sap-icon://attachment-video";
                case "doc/word":
                case "doc/doc":
                case "doc/docx":
                    return "sap-icon://doc-attachment";
                case "doc/ppt":
                case "doc/pptx":
                    return "sap-icon://ppt-attachment";
                default:
                    return "sap-icon://document";
            }
        },
        /**
         * Document list type
         * 
         */
        listType: function(sContentType) {
            return Formatter.isFolder(sContentType) ? "Navigation" : "Active";
        },
        /**
         * show document count
         *
         */
        showDocumentsCount: function(sContentType) {
            return Formatter.isFolder(sContentType);
        },
        /**
         * 
         * Is folder
         * 
         */
        isFolder: function(sContentType) { 
            return (sContentType === "doc/folder");
        },
        /**
         * Format the category
         * @param vendorCategoryEnum
         * @param oVendorCategory
         */
        vendorCategoryName: function(vendorCategoryEnum, oVendorCategory) {
            for (var index in oVendorCategory) {
                if (oVendorCategory[index].key === vendorCategoryEnum) {
                    return oVendorCategory[index].val;
                }
            }
            return "Not found";
    
        },
        /**
         * @param vendorCategoryEnum
         */
        quotaDescription: function(vendorCategoryEnum) {
            switch (vendorCategoryEnum) {
                case "S":
                    return "Allocated to vendors with low document share";
                case "M":
                    return "Allocated to vendors with medium document share";
                case "L":
                    return "Allocated to vendors with large document share";
                default:
                    return "Allocation criteria not known";
            }
    
        },
    
        /**
         * 
         * Vendor Groups
         * @param iVendorGroupID
         * @param oVendorCategory
         */
        vendorGroups: function(iVendorGroupID, oVendorCategory) {
            for (var index in oVendorCategory) {
                if (oVendorCategory[index].vendorGroupID === iVendorGroupID) {
                    return oVendorCategory[index].vendorUserGroupDesc;
                }
            }
            return "Not found";
        },
        /**
         * Is document in release state
         * 
         */
        isDraft: function(oReleaseState) {
            oReleaseState = oReleaseState || "";
            return (oReleaseState !== 'RELEASED');
        },
    
        mergeEmailNName: function(fairPriceUserFullname, fairPriceEmail) {
            return fairPriceUserFullname + " <" + fairPriceEmail + ">";
        },
    
        showUtilizationState: function(oUtilPerc) {
            if (oUtilPerc < 70) {
                return "Success";
            } else if ((oUtilPerc > 70) && (oUtilPerc < 90)) {
                return "Warning";
            } else {
                return "Error";
            }
        },
        showSpaceRecycler: function(oUtilPerc) {
            return oUtilPerc > 95;
        },
        /**
         * Format Vendor Administrator Data
         */
        formatVendorAdminData : function(data,vendorListObj){
            var vendorData = data.result;
            var userId = (vendorData.userName && vendorData.userName.indexOf("@")>-1) ? vendorData.userName.split("@")[0] : "admin";
            vendorData.userID =  userId.toLowerCase(); 
            vendorData.vendorCategory = vendorListObj.vendorCategory;
            vendorData.systemAccountStatusUpdated = vendorListObj.systemAccountStatusUpdated;
            vendorData.validateEmailDomain = vendorListObj.validateEmailDomain;
            vendorData.email = vendorListObj.vendorAdminEmail;
            vendorData.quotaUtilization = data.relMap.qu;
            vendorData.action = vendorListObj.action;
            vendorData.isEdit = (vendorData.action === "edit")? true : false;
            vendorData.displayName = (data.relMap.sciDetails && data.relMap.sciDetails.displayName)? data.relMap.sciDetails.displayName : (vendorData.firstName+" "  + vendorData.lastName);
            vendorData.salutation = (data.relMap.sciDetails && data.relMap.sciDetails.salutation)? data.relMap.sciDetails.salutation : "";
            vendorData.systemAccountStatus =(data.relMap.sciDetails && data.relMap.sciDetails.systemAccountStatus)? data.relMap.sciDetails.systemAccountStatus : vendorListObj.systemAccountStatus;
            vendorData.work = (data.relMap.sciDetails && data.relMap.sciDetails.work) ?  data.relMap.sciDetails.work : vendorListObj.work;
            vendorData.mobile = (data.relMap.sciDetails && data.relMap.sciDetails.salutation) ? data.relMap.sciDetails.mobile : vendorListObj.mobile;
            vendorData.selectedGroups = (data.relMap.selectedGroups instanceof Array) ? data.relMap.selectedGroups : "";
            vendorData.vendorAccounts = data.relMap.vendorAccounts;
            var element;
            for (var i = 0; i < vendorData.vendorAccounts.length; i++) {
                element = vendorData.vendorAccounts[i];
                element.isNewVendor = false;
                element.isVendorAdmin = (vendorData.userName.indexOf(element.vendorCode)>-1);
                if(element.isVendorAdmin){
                    vendorData.vendorCode = element.vendorCode;
                    vendorData.vendorName = element.vendorName;
                }
                element.isCreate = false;
                element.isDelete = false;
            }
            return vendorData;
        },
        /*
         * Select One Check Box at a time
         */
        selectOneItem: function(sPath, selectedField, model, data, isValid) {
            var index = Number(sPath.substr(sPath.length - 1, sPath.length));
            sPath = sPath.substr(0, sPath.length - 1);
            for (var i = 0; i < data.length; i++) {
                model.setProperty(sPath + i + "/" + selectedField, false);
                if (i === index && isValid) {
                    model.setProperty(sPath + i + "/" + selectedField, true);
                }
            }
        },
        /**
         * Format Vendor User Data 
         */
        formatVendorUserData : function(userModel, data){
            var userData = userModel.getData();
            userData.work = (data.sciDetails && data.sciDetails.work) ?  data.sciDetails.work : "";
            userData.mobile = (data.sciDetails && data.sciDetails.mobile) ? data.sciDetails.mobile : "";
            userData.userID = (userData.userName && userData.userName.indexOf("@")>-1) ? userData.userName.split("@")[0] : "";
            userData.displayName = (data.sciDetails && data.sciDetails.displayName)? data.sciDetails.displayName : (userData.firstName+" "  + userData.lastName);
            userData.email = userData.vendorUserEmail;
            userData.salutation = (data.sciDetails && data.sciDetails.salutation)? data.sciDetails.salutation : "";
            userData.isEdit = true;
            userData.systemAccountStatusUpdated = (data.sciDetails && data.sciDetails.systemAccountStatus)? data.sciDetails.systemAccountStatus : "";
            userData.accountValidated = (userData.systemAccountStatusUpdated === "ACTIVE");
            userData.groupsAssigned = "";
            //Format Authorization List
            this.prepareAuthorizationList(userData,data);
            userModel.setData(userData);
        },
        /**
         * Prepare Authorization List  
         */
        prepareAuthorizationList : function(userData,data){
            userData.authorizationList = [];
            var element;
            var authProperty = {};
            for (var i = 0; i < data.authorizations.length; i++) {
                element = data.authorizations[i];
                if(!authProperty[element.vendorCode]){
                    authProperty[element.vendorCode] = element;
                }
                authProperty[element.vendorCode].isVendorUser = (authProperty[element.vendorCode].vendorCode === userData.vendorCode);
                if(authProperty[element.vendorCode].vendorCode === userData.vendorCode){
                    userData.vendorName = element.vendorName;
                }
                authProperty[element.vendorCode].selectedGroups = (authProperty[element.vendorCode].selectedGroups instanceof Array)? authProperty[element.vendorCode].selectedGroups : [];
                authProperty[element.vendorCode].selectedGroups.push(element.groupName);
                authProperty[element.vendorCode].isCreate = false;
                authProperty[element.vendorCode].isDelete = !authProperty[element.vendorCode].isVendorUser;
                authProperty[element.vendorCode].isVendorCode = false;
            }
            
            //Assign to Authorization List
            jQuery.sap.each(authProperty,function(vCode,element){
                userData.authorizationList.push(element);
            });
        }
    };
});