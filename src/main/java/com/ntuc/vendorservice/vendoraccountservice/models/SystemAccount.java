package com.ntuc.vendorservice.vendoraccountservice.models;

import java.io.Serializable;
import java.util.HashMap;

import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;

/**
 * System accounts can either be 
 * 1. FairPriceUser Account
 * 2. VendorUser Account
 * 3. VendorAccount see {@link VendorAccount}
 * @author I305675
 *
 */
public interface SystemAccount extends Serializable{
	String KEY_USER_ID="systemUserID";
	String KEY_VENDOR_ID="vendorID";
	String KEY_SYSTEM_ROLE="systemRole";
	String KEY_EMAIL="email";
	String KEY_DISPLAY_NAME="diplayName";
	String KEY_ACCOUNT_ACTIVATED="isActivated";
	
	
	HashMap<String, Object> getAccountProfileDetails();
	
	void setSystemAccountStatus(SystemAccountStatus systemAccountStatus);
	
	SystemAccountStatus getSystemAccountStatus();
	
	String getSciAccountID();



}
