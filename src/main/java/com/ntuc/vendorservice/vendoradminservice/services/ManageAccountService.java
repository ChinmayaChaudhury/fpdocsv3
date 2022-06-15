package com.ntuc.vendorservice.vendoradminservice.services;

import javax.mail.MethodNotSupportedException;
import javax.servlet.http.HttpServletRequest;

import com.ntuc.vendorservice.scimservice.exceptions.AccountUpdateSCIException;
import com.ntuc.vendorservice.scimservice.models.SCIMUserGroup;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.exceptions.*;
import com.sap.cloud.security.adapter.xs.XSUserInfoAdapter;
import com.sap.xsa.security.container.XSUserInfo;
import org.apache.http.client.HttpResponseException;

import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;

/**
 * 
 * @author I305675
 *
 */
public interface ManageAccountService {
	/**
	 * User Management SCIM End Points
	 */
	String USER_REGISTRATION_END_POINT="users";
	String USER_UME_GET="users?name_id=%s";
	String USER_SCIM_END_POINT="scim/Users/%s";
	String USER_SCIM_GET="scim/Users/%s";
	String USER_SCIM_SEARCH="scim/Users?%s";
	/**
	 * 
	 * @param t
	 * @return
	 */
	public <T extends SystemAccount>Result createAccount(T systemAccount, String serviceUrl);

	/**
	 *
	 * @param fpsciUserAccount
	 * @param request
	 * @return
	 * @throws HttpResponseException
	 */
	public Result createSAPSCIAccount(FPSCIUserAccount fpsciUserAccount, HttpServletRequest request) throws HttpResponseException;

	/**
	 *
	 * @param fpsciUserAccount
	 * @param request
	 * @return
	 */
	Result createSAPSCIAccountWithActivationCallbackUrl(FPSCIUserAccount fpsciUserAccount, HttpServletRequest request);
	/**
	 * Create account with SCI Details
	 * @param scimUser
	 * @param request
	 * @return
	 * @throws HttpResponseException
	 */
	Result createSAPSCIAccount(SCIMUser scimUser, HttpServletRequest request)throws HttpResponseException;

	Result updateVendorAdminAccount(XSUserInfo xsUserInfoAdapter, FPSCIUserAccount userAccount, SCIMUserGroup userGroupType, HttpServletRequest request, String baseUrl);

	VendorAccountAdministrator initializeVendorAdministratorUpdate(XSUserInfo xsUserInfoAdapter, HttpServletRequest request, VendorAccount vendorAccount, boolean userExists, SCIMUser queryResult, boolean allowDocumentShare) throws AccountManagementException, SCIServiceException;

	/**
	 *
	 * @param userAccount
	 * @param compositeAuthGroup
	 * @param request
	 * @return
	 * @throws HttpResponseException
	 */
	public   Result updateSAPSCIAccount(SCIMUser userAccount, HttpServletRequest request) throws HttpResponseException;

	/**
	 *
	 * @param userID
	 * @param request
	 * @return
	 * @throws HttpResponseException
	 */
	public  Result getUMESAPSCIAccountDetails(String userID, HttpServletRequest request) throws HttpResponseException, SCIServiceException;
	/**
	 *
	 * @param userID
	 * @param request
	 * @return
	 * @throws HttpResponseException
	 */
	public  Result getSAPSCIAccountDetails(String userID, HttpServletRequest request) throws HttpResponseException;
	/**
	 * Search the SCI User Details
	 * @param userID
	 * @return
	 */
	public  Result searchSAPSCIAccountDetails(String userID, HttpServletRequest request)throws HttpResponseException;
	/**
	 * Run the search queries
	 * @param queryString
	 * @param request
	 * @return
	 * @throws HttpResponseException
	 */
	Result querySAPSCIAccountDetails(String queryString, HttpServletRequest request) throws HttpResponseException;
	/**
	 *
	 * @param queryString
	 * @param request
	 * @param t
	 * @param <T>
	 * @return
	 * @throws HttpResponseException
	 */
	public <T>  Result parseSCIMUserQuery(String queryString, HttpServletRequest request, Class<T> t) throws HttpResponseException;
	/**
	 *
	 * @param userID
	 * @param request
	 * @return
	 * @throws HttpResponseException
	 */
	Result deleteSAPSCIAccount(String userID, HttpServletRequest request)throws HttpResponseException;
	/**
	 * Get attributes for profile
	 * @param attributeName
	 * @param request
	 * @return
	 */
	Result getAttributeKeyVal(String attributeName, HttpServletRequest request);

	/**
	 *
	 * @param userAccount
	 * @param userGroupType
	 * @param request
	 * @param baseUrl
	 * @param isBulk
	 * @return
	 * @throws AccountNotExistsException
	 * @throws AccountExistsException
	 * @throws AccountManagementException
	 * @throws AccountUpdateSCIException
	 * @throws AccountDetailsException
	 * @throws MethodNotSupportedException
	 */
	Result initializeUserForSCIAccountNDOCShare(final XSUserInfo xsUserInfoAdapter, FPSCIUserAccount userAccount, SCIMUserGroup userGroupType, HttpServletRequest request, String baseUrl, boolean isBulk) throws AccountNotExistsException, AccountExistsException, AccountManagementException, AccountUpdateSCIException, AccountDetailsException, MethodNotSupportedException, SCIServiceException;

	/**
	 *
	 * @param administrator
	 * @return
	 */
	Result createVendorAdministratorAccount(VendorAccountAdministrator administrator);

}
