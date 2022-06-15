package com.ntuc.vendorservice.scimservice.proxy;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.entity.UserProfile;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import com.sap.cloud.security.adapter.xs.XSUserInfoAdapter;
import com.sap.xsa.security.container.XSUserInfo;
import com.sap.xsa.security.container.XSUserInfoException;
/*import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PasswordCheckResult;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.UserProvider;
import com.sap.security.um.user.User;*/


public class SCIIdentityProxy {
	/**
	 * Authenticate User
	 * @param userID
	 * @throws AccountManagementException
	 */
	/*public static Result getUserIDAttributes(String userID) throws AccountManagementException {
		try {
			UserProvider userProvider = UserManagementAccessor.getUserProvider();
			User user = userProvider.getUser(userID);
			return new Result(ResultType.SUCCESS,user.listAttributes(	));
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new AccountManagementException(e.getMessage());
		}
	}*/
	/**
	 * Get the Logged in user details
	 * @param xsUserInfoAdapter
	 * @param systemAccount {@link SystemAccount}
	 * @return {@link SystemAccount}
	 */
	public static <E extends SystemAccount, Z  extends com.sap.xsa.security.container.XSUserInfo> UserProfile getLoggedUserProfile (final Z xsUserInfoAdapter, E  systemAccount) {
		if (xsUserInfoAdapter == null) {
			throw new IllegalArgumentException("Request must not be null");
		}
		UserProfile userProfile;
		try{
			///String userName = idmUser.getName();
			final String userName = xsUserInfoAdapter.getLogonName();
			String displayName= "";
			// String email= idmUser.getAttribute("email");
			//
			String email= xsUserInfoAdapter.getEmail();
			//String lastName = idmUser.getAttribute("lastName");

			String lastName = xsUserInfoAdapter.getFamilyName();
			//
			//String firstName = idmUser.getAttribute("firstName");
			String firstName = xsUserInfoAdapter.getGivenName();
			SystemUserRole systemUserRole=SystemUserRole.FairPriceAdminstrator;
			boolean accountActivated=false;
			Long systemUserID= null;
			Long vendorID=null;
			userProfile =new UserProfile();
			if(systemAccount instanceof VendorUser){
				VendorUser vendorUser=(VendorUser)systemAccount;
				email=vendorUser.getVendorUserEmail();
				lastName=vendorUser.getLastName();
				firstName=vendorUser.getFirstName();
				systemUserRole=vendorUser.getSystemUserRole();
				accountActivated=vendorUser.isAccountValidated();
				systemUserID=vendorUser.getVendorUserID();
				vendorID=vendorUser.getVendorID();
				displayName=vendorUser.getDisplayName();
			}
			if(systemAccount instanceof InternalUser){
				InternalUser fairPriceUser=(InternalUser)systemAccount;
				email=fairPriceUser.getFairPriceEmail();
				lastName=fairPriceUser.getLastName();
				firstName=fairPriceUser.getFirstName();
				systemUserRole=fairPriceUser.getSystemUserRole();
				accountActivated=fairPriceUser.isAccountValidated();
				systemUserID=fairPriceUser.getFairPriceUserID();
				displayName=fairPriceUser.getDisplayName();
			}
			if(systemAccount instanceof VendorAccountAdministrator){
				VendorAccountAdministrator fairPriceUser=(VendorAccountAdministrator)systemAccount;
				email=fairPriceUser.getVendorUserEmail();
				lastName=fairPriceUser.getLastName();
				firstName=fairPriceUser.getFirstName();
				systemUserRole=fairPriceUser.getSystemUserRole();
				accountActivated=fairPriceUser.isAccountValidated();
				systemUserID=fairPriceUser.getVendorAdminID();
				displayName=fairPriceUser.getDisplayName();
				userProfile.setLoginName(fairPriceUser.getUserName());
			}
			if(systemAccount instanceof VendorAccount){
				final String vendorCode = ((VendorAccount)systemAccount).getVendorCode();
				userProfile.setVendorCode(vendorCode);
			}
			userProfile.setUserName(userName);
			userProfile.setEmail(email);
			userProfile.setAccountActivated(accountActivated);
			userProfile.setVendorID(vendorID);
			userProfile.setSystemUserID(systemUserID);
			userProfile.setSystemUserRole(systemUserRole);
			userProfile.setDisplayName(displayName);
			userProfile.setFirstName(firstName);
			userProfile.setLastName(lastName);
		}
		catch (Exception exc) {
			throw new RuntimeException("Could not retrieve currently logged in user due to a PersistenceException", exc);
		}
		return userProfile;
	}

	/**
	 * This method returns only the unique user ID that belongs to the currently logged-on user.
	 *
	 * @param xsUserInfoAdapter from which the user ID is extracted
	 * @return the unique user ID
	 * */
	public static String getLoggedUserId (final XSUserInfo xsUserInfoAdapter) {
		if (xsUserInfoAdapter == null) {
			throw new IllegalArgumentException("Request must not be null.");
		}
	 	return xsUserInfoAdapter.getLogonName();
	}
	/**
	 * Get the Administrator Profile
	 * @param xsUserInfoAdapter
	 * @return
	 */
	public static  UserProfile getFPAdministratorProfile(final XSUserInfo xsUserInfoAdapter){
		if (xsUserInfoAdapter == null) {
			throw new IllegalArgumentException("Request must not be null.");
		}
		final UserProfile userProfile = new UserProfile();
		try{
			//String firstName = idmUser.getAttribute("firstName");
			userProfile.setFirstName(xsUserInfoAdapter.getFamilyName());

			//String lastName = idmUser.getAttribute("lastName");
			userProfile.setLastName(xsUserInfoAdapter.getGivenName());

			//String displayName = idmUser.getAttribute("displayName");
			userProfile.setDisplayName(xsUserInfoAdapter.getLogonName());

			userProfile.setEmail(xsUserInfoAdapter.getEmail());

			userProfile.setAccountActivated(true);

			userProfile.setSystemUserRole(SystemUserRole.FairPriceAdminstrator);
		}catch (Exception exc) {
			throw new RuntimeException("Could not retrieve currently logged in user due to an UnsupportedUserAttributeException", exc);
		}
		return userProfile;
	}

}
