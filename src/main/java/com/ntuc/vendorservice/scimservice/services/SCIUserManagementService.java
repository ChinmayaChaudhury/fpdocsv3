package com.ntuc.vendorservice.scimservice.services;

import java.util.List;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;
import com.ntuc.vendorservice.foundationcontext.catalog.model.VendorAccountModel;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import org.apache.http.client.HttpResponseException;


/**
 * Handle the user management functions via the API services
 * @author I305675
 *
 */
public interface SCIUserManagementService extends BaseSCIManagementService {
	
	String USER_REGISTRATION_END_POINT="users";
	String USER_UME_GET="users?name_id=%s";
	String USER_SCIM_END_POINT="scim/Users/%s";
	
	String USER_SCIM_GET="scim/Users/%s";
	String USER_SCIM_SEARCH="scim/Users?%s";
	String SAP_GW_GET = "sap/opu/odata/SAP/ZMM_GET_VENDORLIST_SRV_01/VENDORLISTSet";
	 
	/**
	 * Create a user account in SCI
	 * @param fpsciUserAccount
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException 
	 */
	public FPSCIUserAccount createUserAccount(FPSCIUserAccount fpsciUserAccount, HttpDestination httpDestination) throws HttpResponseException;

	/**
	 * Create a user account in SCI
	 * @param scimUser
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException
	 */
	public SCIMUser createUserAccount(SCIMUser scimUser, HttpDestination httpDestination) throws HttpResponseException;
	/**
	 * Update User Account in SCI
	 * @param scimUser
	 * @param httpDestination {@link HttpDestination} to be used; already maintained in HCP
	 * @return the parsed {@link SCIMUser} record
	 * @throws HttpResponseException
	 */
	public  SCIMUser updateUserAccount(SCIMUser scimUser, HttpDestination httpDestination) throws HttpResponseException;

	/**
	 * Search profile
	 * @param profileID
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException
	 */
	public  FPSCIUserAccount getUserProfile(String profileID, HttpDestination httpDestination) throws HttpResponseException;

	/**
	 * Search profile
	 * @param queryString
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException
	 */
	public  List<SCIMUser> parseUserQuery(String queryString, HttpDestination httpDestination) throws HttpResponseException;
	/**
	 * Search profile
	 * @param profileID
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException
	 */
	public  SCIMUser getUserDetail(String profileID, HttpDestination httpDestination) throws HttpResponseException;
    /**
     * 
     * @param userID
     * @param httpDestination
     * @return
     */
	ResultType parseDeleteAccount(String userID, HttpDestination httpDestination)throws HttpResponseException;
	/**
	 * Create the User Account and respond with ActivationUrl
	 * @param fpsciUserAccount
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException
	 */
	String createUserAccountWithCallBackActivationUrl(FPSCIUserAccount fpsciUserAccount, HttpDestination httpDestination) throws HttpResponseException;
	/**
	 * 
	 * @param attributeName
	 * @param umeDestinationHttpClient
	 * @return
	 * @throws HttpResponseException
	 */
	 List<KeyVal> getAttribute(String attributeName, HttpDestination umeDestinationHttpClient)throws HttpResponseException;
	
	/**
	 * @param vendorCode
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException
	 */
	 VendorAccountModel getVendorCodeFromSap(String vendorCode, HttpDestination httpDestination)throws HttpResponseException;
	
	
}
