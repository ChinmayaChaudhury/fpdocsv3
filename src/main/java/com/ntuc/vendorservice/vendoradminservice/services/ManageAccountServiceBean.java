package com.ntuc.vendorservice.vendoradminservice.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MethodNotSupportedException;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ntuc.vendorservice.foundationcontext.catalog.constants.AccountFLPUrl;
import com.ntuc.vendorservice.foundationcontext.catalog.model.*;
import com.ntuc.vendorservice.notificationservice.entity.ActivationNotificationRequest;
import com.ntuc.vendorservice.notificationservice.services.AsyncVendorAccountActivationResend;
import com.ntuc.vendorservice.scimservice.entity.ApplicationGroupsLookup;
import com.ntuc.vendorservice.vendoraccountservice.repository.VendorUserRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.vendoradminservice.exceptions.*;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAccountRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAdministratorRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorToAdminMappingRepositoryBean;
import com.ntuc.vendorservice.notificationservice.services.AsyncAccountActivation;
import com.ntuc.vendorservice.scimservice.services.SCIGroupManagementService;
import com.ntuc.vendorservice.scimservice.services.SCIGroupManagementServiceImpl;
import com.ntuc.vendorservice.scimservice.services.SCIUserManagementService;
import com.ntuc.vendorservice.scimservice.services.SCIUserManagementServiceImpl;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.xsa.security.container.XSUserInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpResponseException;
import com.ntuc.vendorservice.foundationcontext.adapter.BTPDestinationServiceAdapter;
import com.ntuc.vendorservice.scimservice.proxy.SCIIdentityProxy;
import com.ntuc.vendorservice.emailingservice.services.NotificationRequestProxy;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.scimservice.exceptions.AccountUpdateSCIException;
import com.ntuc.vendorservice.emailingservice.models.EmailStatus;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMEmail;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.scimservice.models.SCIMName;
import com.ntuc.vendorservice.scimservice.models.SCIMPhoneNumber;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.scimservice.models.SCIMUserGroup;
import com.ntuc.vendorservice.notificationservice.repository.ActivationNotificationRequestRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.FairPriceUserGroupRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAuthorizationGroups;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorToAdminMapping;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;
import com.ntuc.vendorservice.scimservice.utils.SCIUtils;

/**
 * Session Bean implementation class ManageAccountServiceBean
 */
@Stateless
@Local(ManageAccountService.class)
@LocalBean
public class ManageAccountServiceBean implements ManageAccountService {
	private static final Log LOGGER = LogFactory.getLog(ManageAccountServiceBean.class);
	private static final String VENDOR_ACCOUNT = "vendorAccount";
	@EJB
	private VendorAccountRepositoryBean vendorAccountBean;
	@EJB
	private ActivationNotificationRequestRepositoryBean activationNotificationRequestBean;
	@EJB
	private VendorUserRepositoryBean vendorUserBean;
	@EJB
	private FairPriceUserGroupRepositoryBean fairPriceUserGroupBean;
	@EJB
	private VendorAdministratorRepositoryBean vendorAdministratorBean;
	@EJB
	private VendorToAdminMappingRepositoryBean vendorToAdminMappingBean;
	@EJB
	private AsyncAccountActivation asyncAccountActivation;

	private BTPDestinationServiceAdapter btpDestinationServiceAdapter;
	@PostConstruct
	protected void initializeDestinationProxy(){
		this.btpDestinationServiceAdapter =new BTPDestinationServiceAdapter();
	}

	/**
	 * Default constructor.
	 */
	public ManageAccountServiceBean() {
		super();
	}
	/**
	 * Delete system account
	 * @param t
	 */
	public <T extends SystemAccount> Result deleteAccount(T t ) {
		Result result = new Result();
		if (t instanceof VendorUser) {
			result=vendorUserBean.delete((VendorUser)t);
		}else if(t instanceof InternalUser) {
			result=fairPriceUserGroupBean.delete((InternalUser)t);
		}
		else if(t instanceof VendorAccount) {
			VendorAccount vendorAccount = (VendorAccount)t;
			result= vendorAccountBean.delete(vendorAccount);
		} else{
			throw new UnsupportedOperationException();
		}
		return result;
	}
	/**
	 * @see ManageAccountService
	 * createAccount(T)
	 */
	public <T extends SystemAccount> Result createAccount(T t, String targetUrl) {
		Result result = new Result();
		if(t.getSystemAccountStatus()==null){
			t.setSystemAccountStatus(SystemAccountStatus.NEW);
		}
		if (t instanceof VendorAccount) {
			try {
				VendorAccount add = vendorAccountBean.add((VendorAccount) t);
				result.setResultType(ResultType.SUCCESS);
				result.setResult(add);
			} catch (AccountManagementException e) {
				result.setResultType(ResultType.EXISTS);
				result.setResult(e.getMessage());
			}

		} else if (t instanceof VendorUser) {
			VendorUser vendorUser = (VendorUser) t;
			try {
				VendorUser user = vendorUserBean.add(vendorUser);

//					if (user.getVendorUserID() > 0) {
//						ActivationNotificationRequest createActivationRequestNotification = createActivationRequestNotification(targetUrl, result, user);
				result.setResultType(ResultType.SUCCESS);
				result.setResult(user);
//						asyncAccountActivation.dispatchAccountActivationNotifications(createActivationRequestNotification,user,null);
//					}
			}catch (AccountManagementException e) {
				result.setResultType(ResultType.ERROR);
				result.setResult(e.getMessage());
			}


		} else if (t instanceof InternalUser) {
			try {
				InternalUser fairPriceUser = fairPriceUserGroupBean.addFairPriceUser((InternalUser) t);
				ActivationNotificationRequest createActivationRequestNotification = createActivationRequestNotification(targetUrl, result, fairPriceUser);
				result.setResultType(ResultType.SUCCESS);
				result.setResult(fairPriceUser);
				asyncAccountActivation.dispatchAccountActivationNotifications(createActivationRequestNotification,fairPriceUser,null);
			} catch (AccountManagementException e) {
				result.setResultType(ResultType.EXISTS);
				result.setResult(e.getMessage());
			}
		}
		return result;
	}

	public ActivationNotificationRequest createActivationRequestNotification(String targetUrl, Result result, NotificationRequest notificationRequest) {
		ActivationNotificationRequest activationNotificationRequest = NotificationRequestProxy.getActivationEmailRequest(notificationRequest,targetUrl);
		ActivationNotificationRequest request = activationNotificationRequestBean.add(activationNotificationRequest);
		if (request.getEmailNotificationID() > 0) {
			request.setEmailStatus(EmailStatus.SENT);
		}
		return request;
	}
	/**
	 * Validate the vendor user account dependencies
	 * @param fpsciUserAccount
	 * @return vendorID if all dependencies are fulfilled
	 * @throws AccountDetailsException
	 * @throws AccountManagementException
	 * @throws AccountExistsException
	 */
	private Long validateVendorUserDependencies(FPSCIUserAccount fpsciUserAccount)
			throws AccountDetailsException, AccountManagementException, AccountExistsException,AccountNotExistsException {
		String email = fpsciUserAccount.getEmail();
		VendorUser vendorUser = vendorUserBean.findByVendorUserEmail(email);
		if(vendorUser!=null){
			throw new AccountExistsException("Account already exists with the emails "+email);
		}
		VendorAccount vendorAccount = vendorAccountBean.findByVendorCode(fpsciUserAccount.getVendorCode());
		if(vendorAccount!=null){
			if (vendorAccount.isValidateEmailDomain()) {
				boolean emailCompliant = isEmailCompliant(email, vendorAccount.getVendorEmailDomain());
				if (!emailCompliant) {
					throw new AccountDetailsException( "Invalid email. The expected format is. i.e."
							+ vendorAccount.getVendorEmailDomain().replace("*", "john.doe"));
				}
			}
			List<VendorUser> findAllByVendorID = vendorUserBean.findAllByVendorID(vendorAccount.getVendorID());
			if(!(findAllByVendorID.size()<20)){
				throw new AccountManagementException("Maximum number of accounts reached");
			}
			return vendorAccount.getVendorID();
		}else{
			throw new AccountNotExistsException("Account not exist");
		}
	}

	private boolean isEmailCompliant(String emailAddress, String vendorEmailDomain) {
		String domainName = emailAddress.substring(emailAddress.indexOf("@") + 1);
		boolean isDomainCompliant = vendorEmailDomain.contains(domainName);
		return isDomainCompliant;
	}

	public Result updateInternalUser(InternalUser fairPriceUser) {
		try {
			InternalUser updateFairPriceUser = fairPriceUserGroupBean.updateFairPriceUser(fairPriceUser);
			return new Result(ResultType.SUCCESS, updateFairPriceUser);
		} catch (AccountManagementException e) {
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}
	/**
	 * If the vendor administrator email address has changed
	 * 1. Remove the current vendor administrator SCIM,
	 * Activate newly updated email in SCI
	 * 2. If email not exist in SCI, Create new vendor administrator and notify the user for these updates
	 * 3. If email exists update the user record in SCI for access
	 * @throws AccountExistsException
	 */
	@Override
	public Result updateVendorAdminAccount(final XSUserInfo xsUserInfoAdapter, FPSCIUserAccount userAccount, SCIMUserGroup userGroupType, HttpServletRequest request, final String baseUrl) {
		ResultWithObjectSet updateResponse = new ResultWithObjectSet();
		try {
			VendorAccountAdministrator vendorAdmin = vendorAdministratorBean.findByVendorAdminID(Long.valueOf(userAccount.getVendorAdminID()));

			String targetUrl = MailSenderUtil.getAccountTargetUrl(baseUrl);
			String loggedInUserID= SCIIdentityProxy.getLoggedUserId(xsUserInfoAdapter);
			Result  resolveVendorAccountDependency = resolveVendorAccountDependency(userAccount,request);
			SCIMUser queryResult = (SCIMUser)resolveVendorAccountDependency.getResult();
			if(queryResult == null){
				queryResult = new SCIMUser();
			}

			//SCI Update
			Result sciUpdateResponse = updateExistingSCIMUser(userAccount, userGroupType, request, queryResult,true);
			if(sciUpdateResponse.getResultType() == ResultType.SUCCESS){

				VendorAccount vendorAccount = null;
				// If the Status is Inactive
				if(userAccount.getSystemAccountStatus()==SystemAccountStatus.INACTIVE){
					for (VendorAccountModel vAccount : userAccount.getVendorAccounts()) {
						if(vAccount.getVendorID() != null){
							vendorAccount = vendorAccountBean.findByID(Long.valueOf(vAccount.getVendorID()));
							vendorAccount.setVendorAdminEmail(userAccount.getEmail());
							vendorAccount.setWork(userAccount.getWork());
							vendorAccount.setMobile(userAccount.getMobile());
							vendorAccount.setSystemAccountStatus(userAccount.getSystemAccountStatus());
							vendorAccountBean.update(vendorAccount);
						}

					}
					//Delete all the documents from the Repository if the Vendor is Deactivated
				}else{ // Update the Details in other cases
					for (VendorAccountModel vAccount : userAccount.getVendorAccounts()) {
						if(vAccount.getVendorID() != null && vAccount.getVendorID().trim().length()>0){
							vendorAccount = vendorAccountBean.findByID(Long.valueOf(vAccount.getVendorID()));
							vendorAccount.setVendorAdminEmail(userAccount.getEmail());
							vendorAccount.setWork(userAccount.getWork());
							vendorAccount.setMobile(userAccount.getMobile());

							//Update Vendor Authorization
							maintainVendorAuthorization(vendorAdmin, vendorAccount,userAccount.getSelectedGroups());

							vendorAccountBean.update(vendorAccount);
						}else{ //Create a Vendor Account in the case of Update
							userAccount.setCompanyName(vAccount.getVendorName());
							userAccount.setVendorCode(vAccount.getVendorCode());
							ResultWithObjectSet result = createVendorAccountRecord(userAccount, userGroupType, request, false, loggedInUserID, targetUrl, queryResult, false);
							if(result.getRelMap().get(VENDOR_ACCOUNT+"_"+userAccount.getVendorCode()) instanceof VendorAccount){
								vendorAccount = (VendorAccount) result.getRelMap().get(VENDOR_ACCOUNT+"_"+vAccount.getVendorCode());
								// Make Appropriate Entries in T_VENDOR_ADMIN and T_VENDOR_ADMIN_MAPPING Tables
								initializeVendorAdministratorAccount(userAccount, null, null, vendorAccount,false,vendorAdmin);
							}
						}

						//Update other Vendor Entities
						if(vAccount.isVendorAdmin() && userAccount.getUserID().contains(vendorAccount.getVendorCode()) && userAccount.isAllowDocumentShare()){
							VendorUser administratorUser = vendorUserBean.findByVendorUserEmail(vendorAdmin.getVendorUserEmail());
							if(administratorUser == null){
								administratorUser = vendorUserBean.findByVendorSCIUserID(vendorAdmin.getSciAccountID());
							}

							if(administratorUser!=null){
								administratorUser.setSciAccountID(vendorAdmin.getSciAccountID());
								administratorUser.setVendorUserEmail(vendorAdmin.getVendorUserEmail());
								administratorUser.setFirstName(userAccount.getFirstName());
								administratorUser.setLastName(userAccount.getLastName());
								administratorUser.setUserName(vendorAdmin.getUserName());
								administratorUser.setVendorAccount(vendorAccount);
								vendorUserBean.update(administratorUser);
							}
						}
					}
					//Update Vendor Administrator Details
					vendorAdmin.setVendorUserEmail(userAccount.getEmail());
					vendorAdmin.setFirstName(userAccount.getFirstName());
					vendorAdmin.setLastName(userAccount.getLastName());
					vendorAdmin.setJobTitle(userAccount.getJobTitle());
					vendorAdministratorBean.update(vendorAdmin);
				}
				updateResponse.setResult(vendorAdmin);
				updateResponse.setResultType(ResultType.SUCCESS);
			}
		} catch (AccountManagementException | AccountUpdateSCIException e) {
			updateResponse.setResult(e.getMessage());
			updateResponse.setResultType(ResultType.ERROR);
		} catch (AccountExistsException e) {
			updateResponse.setResult(e.getMessage());
			updateResponse.setResultType(ResultType.ERROR);
		}
		return updateResponse;
	}
	/**
	 * XXX
	 * If the vendor administrator email address has changed
	 * 1. Remove the current vendor administrator SCIM,
	 * Activate newly updated email in SCI
	 * 2. If email not exist in SCI, Create new vendor administrator and notify the user for these updates
	 * 3. If email exists update the user record in SCI for access
	 * @throws AccountExistsException
	 */
	@SuppressWarnings("unchecked")
	public Result updateVendorAccount( VendorAccount vendorAccount, final HttpServletRequest request, final String baseUrl) throws AccountExistsException {
		try {
			String vendorAdminGroup =request.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsAdministrator.name());
			String vendorUserGroup =request.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsEndUser.name());
			validateEmailDependency(vendorAccount.getVendorAdminEmail(), true);
			VendorAccount priorVendorAccountDetials = vendorAccountBean.findByID(vendorAccount.getVendorID());

			//Check if Vendor Administrator's Email is different
			if(!priorVendorAccountDetials.getEmailAddress().equals(vendorAccount.getEmailAddress())){
				String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(priorVendorAccountDetials.getEmailAddress()).append("%22").toString();
				Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);
				List<SCIMUser> scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
				SCIMUser recentSCIMUserDetails = SCIUtils.getRecentSCIMUserDetails(scimUsers);
				List<VendorAccount> accounts=vendorAccountBean.findByVendorAdminEmail(priorVendorAccountDetials.getEmailAddress());
				if(recentSCIMUserDetails!=null){
					Set<SCIMGroup> groups = recentSCIMUserDetails.getGroups();
					boolean contains = groups.contains(new SCIMGroup(vendorAdminGroup));
					if(contains){
						throw new AccountManagementException("Email '"+priorVendorAccountDetials.getEmailAddress()+"' managing the vendor account needs to be removed from group '"+vendorAdminGroup+"' in SCI");
					}
				}
				queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(vendorAccount.getEmailAddress()).append("%22").toString();
				parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);
				scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
				SCIMUser queryResult = SCIUtils.getRecentSCIMUserDetails(scimUsers);
				if(queryResult!=null){
					SCIMUser updateSciUserDetails = new SCIMUser();
					String sciAccountID = queryResult.getId();
					updateSciUserDetails.setId(sciAccountID);
					updateSciUserDetails.setCompany(vendorAccount.getVendorName());
					updateSciUserDetails.setRelationshipToSAP("03");//"refer to 03 in the api"
					queryResult.getGroups().add(new SCIMGroup(vendorAdminGroup));

					vendorAccount.setSciAccountID(sciAccountID);
					String activationUrl=MailSenderUtil.getAccountTargetUrl(baseUrl);
					VendorToAdminMapping vendorToAdminMapping = vendorToAdminMappingBean.findByVendorAccount(vendorAccount.getVendorID());
					VendorAccountAdministrator vendorAccountAdministrator = vendorToAdminMapping.getVendorAdministrator();
					if(vendorAccountAdministrator.isAllowDocumentShare()){
						queryResult.getGroups().add(new SCIMGroup(vendorAdminGroup));
					}
					updateSciUserDetails.setGroups(queryResult.getGroups());

					//Update Vendor Work and Mobile Number
					updateSciUserDetails.setPhoneNumbers(getContactInfo(null,vendorAccount));
					updateSAPSCIAccount(updateSciUserDetails, request) ;

					String userName="";
					Result searchResult=getUMESAPSCIAccountDetails(sciAccountID, request);
					if(searchResult.getResultType()==ResultType.SUCCESS){
						FPSCIUserAccount userAccount = (FPSCIUserAccount) searchResult.getResult();
						userName = getUserName(userAccount, queryResult);
					}
					if(StringUtils.isEmpty(userName)){
						userName=sciAccountID;
					}
					String vendorAdminEmail = vendorAccount.getVendorAdminEmail();
					String givenName = queryResult.getName().getGivenName();
					String familyName = queryResult.getName().getFamilyName();
					if(vendorAccountAdministrator.isAllowDocumentShare()){
						VendorUser administratorUser = vendorUserBean.findByVendorUserEmail(priorVendorAccountDetials.getEmailAddress());
						administratorUser.setSciAccountID(sciAccountID);
						administratorUser.setVendorUserEmail(vendorAdminEmail);
						administratorUser.setFirstName(givenName);
						administratorUser.setLastName(familyName);
						administratorUser.setUserName(userName);
						administratorUser.setVendorAccount(vendorAccount);
						vendorUserBean.update(administratorUser);
					}
					vendorAccountAdministrator.setVendorUserEmail(vendorAdminEmail);
					vendorAccountAdministrator.setFirstName(givenName);
					vendorAccountAdministrator.setSciAccountID(sciAccountID);
					vendorAccountAdministrator.setLastName(familyName);
					vendorAccountAdministrator.setUserName(userName);
					vendorAdministratorBean.update(vendorAccountAdministrator);
					for(VendorAccount account:accounts){
						account.setSciAccountID(sciAccountID);
						account.setVendorAdminEmail(vendorAdminEmail);
						//Update the Contact Numbers
						if(account.getVendorID()==vendorAccount.getVendorID()){
							account.setWork(vendorAccount.getWork());
							account.setMobile(vendorAccount.getMobile());
						}
						vendorAccountBean.update(account);
					}
					activationNotificationRequestBean.add(NotificationRequestProxy.getActivationEmailRequest(vendorAccount,activationUrl));
					//create an account activation email
				}else{
					throw new AccountManagementException("Kindly instantiate the vendor administrator first in SCI with email "+vendorAccount.getEmailAddress());
				}
			}else{ // If Email Address is same
				String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(priorVendorAccountDetials.getEmailAddress()).append("%22").toString();
				Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);
				List<SCIMUser> scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
				SCIMUser queryResult = SCIUtils.getRecentSCIMUserDetails(scimUsers);
				if(queryResult!=null){
					Set<SCIMGroup> groups = queryResult.getGroups();
					boolean contains = groups.contains(new SCIMGroup(vendorAdminGroup));
					SCIMUser updateSciUserDetails = new SCIMUser();
					updateSciUserDetails.setId(queryResult.getId());
					if(!contains){
						updateSciUserDetails.setCompany(vendorAccount.getVendorName());
						updateSciUserDetails.setRelationshipToSAP("03");//"refer to 03 in the api"
						groups.add(new SCIMGroup(vendorAdminGroup));
						updateSciUserDetails.setGroups(groups);
						vendorAccount.setSciAccountID(queryResult.getId());
						VendorToAdminMapping vendorToAdminMapping = vendorToAdminMappingBean.findByVendorAccount(vendorAccount.getVendorID());
						VendorAccountAdministrator vendorAccountAdministrator =null;
						if(vendorToAdminMapping==null){
							try {
								vendorAccountAdministrator = vendorAdministratorBean.findByVendorUserEmail(vendorAccount.getVendorAdminEmail());
							} catch (AccountNotExistsException e) {
								e.printStackTrace();
							}
						}else{
							vendorAccountAdministrator = vendorToAdminMapping.getVendorAdministrator();
						}
						if(vendorAccountAdministrator !=null){
							if(vendorAccountAdministrator.isAllowDocumentShare()){
								queryResult.getGroups().add(new SCIMGroup(vendorUserGroup));
							}
						}
						updateSciUserDetails.setGroups(queryResult.getGroups());
					}
					//Update Phone Numbers
					updateSciUserDetails.setPhoneNumbers(getContactInfo(null,vendorAccount));
					updateSAPSCIAccount(updateSciUserDetails, request) ;
					vendorAccount.setSciAccountID(queryResult.getId());
				}
			}

			vendorAccount = vendorAccountBean.update(vendorAccount);
			return new Result(ResultType.SUCCESS, vendorAccount);
		} catch (AccountManagementException | SCIServiceException e) {
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}
	@Override
	public VendorAccountAdministrator initializeVendorAdministratorUpdate(XSUserInfo xsUserInfoAdapter, HttpServletRequest request, VendorAccount vendorAccount, boolean userExists, SCIMUser queryResult, boolean allowDocumentShare) throws AccountManagementException, SCIServiceException {
		List<VendorAccount> accounts=vendorAccountBean.findByVendorAdminEmail(vendorAccount.getEmailAddress());
		if(!accounts.isEmpty()){
			VendorAccount dsVendorAccount = accounts.get(0);
			VendorAccountAdministrator vendorAccountAdministrator = vendorToAdminMappingBean.findByVendorAccount(dsVendorAccount.getVendorID()).getVendorAdministrator();
			VendorToAdminMapping userToVendorSubscription=new VendorToAdminMapping();
			userToVendorSubscription.setVendorAdministrator(vendorAccountAdministrator);
			userToVendorSubscription.setVendorAccount(vendorAccount);
			if(allowDocumentShare){
				vendorAccountAdministrator.setAllowDocumentShare(allowDocumentShare);
				vendorAdministratorBean.update(vendorAccountAdministrator);
			}
			try {
				vendorToAdminMappingBean.add(userToVendorSubscription);
			} catch (AccountManagementException e) {
				e.printStackTrace();
			}

			return vendorAccountAdministrator;
		}
		String loggedInUserID= SCIIdentityProxy.getLoggedUserId(xsUserInfoAdapter);
		Result searchResult=getUMESAPSCIAccountDetails(queryResult.getId(), request);
		if(searchResult.getResultType()==ResultType.SUCCESS){
			FPSCIUserAccount userAccount = (FPSCIUserAccount) searchResult.getResult();
			VendorAccountAdministrator vendorAccountAdministrator = initializeVendorAdministratorAccount( userAccount,  loggedInUserID,  queryResult, vendorAccount,userExists);
			if(allowDocumentShare){
				vendorAccountAdministrator.setAllowDocumentShare(allowDocumentShare);
				vendorAdministratorBean.update(vendorAccountAdministrator);
			}
			return vendorAccountAdministrator;
		}
		throw  new AccountManagementException("Kindly instantiate the vendor administrator first in SCI with email "+vendorAccount.getEmailAddress());

	}
	//XXX Update the vendor logic
	public Result updateVendorUser(VendorUser vendorUser) {
		try {
			VendorAccount vendorAccount = vendorAccountBean.findByID(vendorUser.getVendorAccount().getVendorID());
			if (vendorAccount.isValidateEmailDomain()) {
				boolean emailCompliant = isEmailCompliant(vendorUser.getEmailAddress(), vendorAccount.getVendorEmailDomain());
				if (!emailCompliant) {
					return new Result(ResultType.ERROR, "Invalid email. The expected format is. i.e."
							+ vendorAccount.getVendorEmailDomain().replace("*", "john.doe"));
				}
			}
			VendorUser update = vendorUserBean.update(vendorUser);
			return new Result(ResultType.SUCCESS, update);
		} catch (AccountManagementException e) {
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}

	/**
	 * If the vendor user email address has changed
	 * 1. Remove the current vendor user SCIM,
	 * Activate newly updated email in SCI
	 * 2. If email not exist in SCI, Create new vendor user and notify the user for these updates
	 * 3. If email exists update the user record in SCI for access
	 * @throws AccountExistsException
	 */
	public Result updateVendorUserAccount(FPSCIUserAccount userAccount,SCIMUserGroup userGroupType,HttpServletRequest request,String baseUrl) {
		ResultWithObjectSet updateResponse = new ResultWithObjectSet();
		try {
//			String targetUrl = MailSenderUtil.getAccountTargetUrl(baseUrl);
//			String activationUrl=targetUrl;
//			String loggedInUserID=IdentityAdapter.getLoggedUserId(request);
			Result  resolveVendorAccountDependency = resolveVendorAccountDependency(userAccount,request);
			SCIMUser queryResult = (SCIMUser)resolveVendorAccountDependency.getResult();
			if(queryResult == null){
				queryResult = new SCIMUser();
				queryResult.setId(userAccount.getSciAccountID());
			}
			Result sciUpdateResponse = updateExistingSCIMUser(userAccount, userGroupType, request, queryResult,false);

			if(sciUpdateResponse.getResultType() == ResultType.SUCCESS){
				VendorUser vUser = vendorUserBean.findByUserID(Long.valueOf(userAccount.getVendorUserID()));
				if(vUser!=null){
//					vendorAccountBean.deleteContactGroups(vUser.getVendorUserID());
					SCIUtils.assignVendorUserAccount(userAccount, queryResult.getId(), vUser);
					vUser = vendorUserBean.update(vUser);
					updateResponse.setResult(vUser);
					updateResponse.setResultType(ResultType.SUCCESS);

					//Maintain Authorizations
					maintainVendorUserAuthorization(vUser.getVendorUserID(), userAccount.getVendorAccounts());
//					vUser.setVendorUserEmail(userAccount.getEmail());
//					vUser.setFirstName(userAccount.getFirstName());
//					vUser.setJobTitle(userAccount.getJobTitle());
//					vUser.setLastName(userAccount.getLastName());
//					vUser.setSystemAccountStatus(userAccount.getSystemAccountStatus());
//					vUser.setUserName(userAccount.getUserID());
//					vUser(userAccount.getEmail());
//					vUser.setWork(userAccount.getWork());
//					vendorAccount.setMobile(userAccount.getMobile());
//					vendorAccount.setSystemAccountStatus(userAccount.getSystemAccountStatus());
				}else{ // To Do
					throw  new AccountExistsException("No account exists with user name "+userAccount.getUserID());
				}
			}else{
				Object response = sciUpdateResponse.getResult();
				StringBuffer message = getSCIResponseMessage(response);
				throw new AccountUpdateSCIException(message.toString());
			}



//			if (vendorAccount.isValidateEmailDomain()) {
//				boolean emailCompliant = isEmailCompliant(userAccount.getEmail(), vendorAccount.getVendorEmailDomain());
//				if (!emailCompliant) {
//					return new Result(ResultType.ERROR, "Invalid email. The expected format is. i.e."
//							+ vendorAccount.getVendorEmailDomain().replace("*", "john.doe"));
//				}
//			}

//			ResultWithObjectSet result = null;

			//SCI Update
//			if(sciUpdateResponse.getResultType() == ResultType.SUCCESS){
//
//				DSVendorAccount vendorAccount = null;
//				// If the Status is Inactive
//				if(userAccount.getSystemAccountStatus()==SystemAccountStatus.INACTIVE){
//					DSVendorUser vendorUser  = new DSVendorUser();
//					vendorUser.setVend
//					for (VendorAccounts vAccount : userAccount.getVendorAccounts()) {
//						if(vAccount.getVendorID() != null){
//							vendorAccount = vendorAccountBean.findByID(Long.valueOf(vAccount.getVendorID()));
//							vendorAccount.setVendorAdminEmail(userAccount.getEmail());
//							vendorAccount.setWork(userAccount.getWork());
//							vendorAccount.setMobile(userAccount.getMobile());
//							vendorAccount.setSystemAccountStatus(userAccount.getSystemAccountStatus());
//							vendorAccountBean.update(vendorAccount);
//						}
//
//						//Delete all the documents if the Vendor Admin is made Inactive
//						if(userAccount.getUserID().contains(vendorAccount.getVendorCode())){
//							Folder vendorRootDirectory = directoryServiceBean.getVendorRootDirectory(vendorAccount.getVendorCode());
//							if(vendorRootDirectory!=null){
//							  directoryServiceBean.deleteDocumentByOpenCmisID(vendorRootDirectory.getId());
//							}
//						}
//					}
//					//Delete all the documents from the Repository if the Vendor is Deactivated
//				}else{ // Update the Details in other cases
//					for (VendorAccounts vAccount : userAccount.getVendorAccounts()) {
//						if(vAccount.getVendorID() != null && vAccount.getVendorID().trim().length()>0){
//							vendorAccount = vendorAccountBean.findByID(Long.valueOf(vAccount.getVendorID()));
//							vendorAccount.setVendorAdminEmail(userAccount.getEmail());
//							vendorAccount.setWork(userAccount.getWork());
//							vendorAccount.setMobile(userAccount.getMobile());
//							vendorAccountBean.update(vendorAccount);
//						}else{ //Create a Vendor Account in the case of Update
//							result = new ResultWithObjectSet();
//							userAccount.setCompanyName(vAccount.getVendorName());
//							userAccount.setVendorCode(vAccount.getVendorCode());
//							createVendorAccountRecord(userAccount, userGroupType, request, false, loggedInUserID, targetUrl, queryResult,result, false);
//							if(result.getRelMap().get(VENDOR_ACCOUNT+"_"+userAccount.getVendorCode()) instanceof DSVendorAccount){
//								vendorAccount = (DSVendorAccount) result.getRelMap().get(VENDOR_ACCOUNT+"_"+vAccount.getVendorCode());
//								// Make Appropriate Entries in T_VENDOR_ADMIN and T_VENDOR_ADMIN_MAPPING Tables
//								initializeVendorAdministratorAccount(userAccount, null, null, vendorAccount,false,vendorAdmin);
//							}
//						}
//
//						//Update other Vendor Entities
//						if(vAccount.isVendorAdmin() && userAccount.getUserID().contains(vendorAccount.getVendorCode()) && userAccount.isAllowDocumentShare()){
//							DSVendorUser administratorUser = vendorUserBean.findByVendorUserEmail(vendorAdmin.getVendorUserEmail());
//							administratorUser.setSciAccountID(vendorAdmin.getSciAccountID());
//							administratorUser.setVendorUserEmail(vendorAdmin.getVendorUserEmail());
//							administratorUser.setFirstName(userAccount.getFirstName());
//							administratorUser.setLastName(userAccount.getLastName());
//							administratorUser.setUserName(vendorAdmin.getUserName());
//							administratorUser.setVendorAccount(vendorAccount);
//							vendorUserBean.update(administratorUser);
//						}
//					}
//					//Update Vendor Administrator Details
//					vendorAdmin.setVendorUserEmail(userAccount.getEmail());
//					vendorAdmin.setFirstName(userAccount.getFirstName());
//					vendorAdmin.setLastName(userAccount.getLastName());
//					vendorAdministratorBean.update(vendorAdmin);
//				}
//				updateResponse.setResult(vendorAdmin);
//				updateResponse.setResultType(ResultType.SUCCESS);
//			}else{
//				updateResponse.setResult("SCI Details for Vendor couldn't be updated");
//				updateResponse.setResultType(ResultType.ERROR);
//			}
		} /*catch (AccountManagamentException | DocumentNotFoundException | DocumentDeletedException | AccountUpdateSCIException e) {
			updateResponse.setResult(e.getMessage());
			updateResponse.setResultType(ResultType.ERROR);
		}*/catch (AccountManagementException | AccountUpdateSCIException e) {
			updateResponse.setResult(e.getMessage());
			updateResponse.setResultType(ResultType.ERROR);
		}catch (AccountExistsException e) {
			updateResponse.setResult(e.getMessage());
			updateResponse.setResultType(ResultType.ERROR);
		}
		return updateResponse;
	}



	@Override
	public  Result updateSAPSCIAccount(SCIMUser userAccount, HttpServletRequest request) {
		try {
			SCIMUser scimUser = SCIUserManagementServiceImpl.getInstance().updateUserAccount(userAccount, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
			return new Result(ResultType.SUCCESS,scimUser);
		} catch (HttpResponseException | NamingException | DestinationAccessException e) {
			String message = e.getMessage();
			if(e instanceof HttpResponseException){
				KeyVal keyVal = new KeyVal(String.valueOf(((HttpResponseException) e).getStatusCode()), message);
				return new Result(ResultType.ERROR,keyVal);
			} else{
				return new Result(ResultType.ERROR,message);
			}
		}
	}
	@Override
	public Result createSAPSCIAccount(SCIMUser scimUser, HttpServletRequest request) {
		try {
			SCIMUser account = SCIUserManagementServiceImpl.getInstance().createUserAccount(scimUser, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
			return new Result(ResultType.SUCCESS,account);
		} catch (HttpResponseException | NamingException | DestinationAccessException e) {
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}
	@Override
	public Result createSAPSCIAccount(FPSCIUserAccount fpsciUserAccount, HttpServletRequest request)  {
		try {
			FPSCIUserAccount userAccount=SCIUserManagementServiceImpl.getInstance().createUserAccount(fpsciUserAccount,  btpDestinationServiceAdapter.getSCIMAttributeDestinationHttpClient(request));
			return new Result(ResultType.SUCCESS,userAccount);
		} catch (HttpResponseException | NamingException | DestinationAccessException e) {
			e.printStackTrace();
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}
	@Override
	public Result createSAPSCIAccountWithActivationCallbackUrl(FPSCIUserAccount fpsciUserAccount, HttpServletRequest request)  {
		try {
			HttpDestination umeDestinationHttpClient = btpDestinationServiceAdapter.getUmeDestinationHttpClient(request);
			String activationUrl=SCIUserManagementServiceImpl.getInstance().createUserAccountWithCallBackActivationUrl(fpsciUserAccount, umeDestinationHttpClient);
			return new Result(ResultType.SUCCESS,activationUrl);
		} catch (HttpResponseException | NamingException | DestinationAccessException e) {
			e.printStackTrace();
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}
	@Override
	public Result searchSAPSCIAccountDetails(String userID, HttpServletRequest request)  {
		try {
			SCIMUser scimUser = SCIUserManagementServiceImpl.getInstance().getUserDetail(userID, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
			return new Result(ResultType.SUCCESS,scimUser);
		} catch (HttpResponseException | NamingException | DestinationAccessException e) {
			e.printStackTrace();
			return new Result(ResultType.ERROR,e.getMessage());
		}

	}
	@Override
	public Result querySAPSCIAccountDetails(String queryString, HttpServletRequest request) {
		try {
			List<SCIMUser> scimUser = SCIUserManagementServiceImpl.getInstance().parseUserQuery(queryString, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
			return new Result(ResultType.SUCCESS,scimUser);
		} catch (HttpResponseException | NamingException | DestinationAccessException e) {
			return new Result(ResultType.ERROR,e.getMessage());
		}

	}

	@Override
	public Result getSAPSCIAccountDetails(String userID, HttpServletRequest request) {
		try {
			return new Result(ResultType.SUCCESS, SCIUserManagementServiceImpl.getInstance().getUserDetail(userID, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request)));
		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
			e.printStackTrace();
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}
	public Result getSAPSCIGroupUsers(String groupName, HttpServletRequest request) {
		try {
			SCIGroupManagementService sciUserManagementService = SCIGroupManagementServiceImpl.getInstance();
			HttpDestination httpDestination = btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request);
			List<FPSCIUserAccount> groupUsers = sciUserManagementService.getUsersInGroup(groupName, httpDestination);
			return new Result(ResultType.SUCCESS, groupUsers);
		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}
	public Result getSAPSCIExpandedGroupUsers(String groupName, HttpServletRequest request) {
		try {
			SCIGroupManagementService sciGroupManagementService = SCIGroupManagementServiceImpl.getInstance();
			SCIUserManagementService sciUserManagementService = SCIUserManagementServiceImpl.getInstance();
			HttpDestination httpDestination = btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request);
			List<FPSCIUserAccount> groupUsers = sciGroupManagementService.getUsersInGroup(groupName, httpDestination);
			List<SCIMUser> scimUsers=new ArrayList<SCIMUser>();
			for(FPSCIUserAccount userAccount:groupUsers){
				String profileID=userAccount.getUserID();
				SCIMUser scimUser = sciUserManagementService.getUserDetail(profileID, httpDestination);
				scimUsers.add(scimUser);

			}
			return new Result(ResultType.SUCCESS, scimUsers);
		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}
	@Override
	public Result getUMESAPSCIAccountDetails(String userID, HttpServletRequest request) throws SCIServiceException {
		try {
			HttpDestination scimDestinationHttpClient = btpDestinationServiceAdapter.getUmeDestinationHttpClient(request);
			return new Result(ResultType.SUCCESS, SCIUserManagementServiceImpl.getInstance().getUserProfile(userID, scimDestinationHttpClient));
		} catch ( NamingException | DestinationAccessException e) {
			return new Result(ResultType.ERROR,e.getMessage());
		} catch (HttpResponseException e) {
			e.printStackTrace();
			throw new SCIServiceException(e.getMessage());
		}
	}


//	public Result getUserSciDetails(String userID, HttpServletRequest request)  {
//		try {
//			return new Result(ResultType.SUCCESS, SCIUserManagementServiceImpl.getInstance().getUserProfile(userID, destinationAdapater.getUmeDestinationHttpClient(request)));
//		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
//			e.printStackTrace();
//			return new Result(ResultType.ERROR,e.getMessage());
//		}
//	}

	@Override
	public Result getAttributeKeyVal(String attributeName, HttpServletRequest request)  {
		try {
			return new Result(ResultType.SUCCESS, SCIUserManagementServiceImpl.getInstance().getAttribute(attributeName, btpDestinationServiceAdapter.getSCIMAttributeDestinationHttpClient(request)));
		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}
	@Override
	public <T>  Result parseSCIMUserQuery(String queryString, HttpServletRequest request, Class<T> t) {
		try {
			List<SCIMUser> scimUsers = SCIUserManagementServiceImpl.getInstance().parseUserQuery(queryString, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
			if(t==SCIMUser.class){
				return new Result(ResultType.SUCCESS, scimUsers);
			} else if(t==FPSCIUserAccount.class){
				if(!scimUsers.isEmpty()){
					FPSCIUserAccount fpsciUserAccount = SCIUtils.toFPSCIUserAccount(scimUsers);
					return new Result(ResultType.SUCCESS,fpsciUserAccount);
				}
				return new Result(ResultType.SUCCESS, scimUsers);
			}throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST,"Unknown request");
		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
			if(e instanceof HttpResponseException){
				int statusCode = ((HttpResponseException)e).getStatusCode();
				if(statusCode==HttpServletResponse.SC_NOT_FOUND){
					return new Result(ResultType.SUCCESS,new ArrayList<SCIMUser>());
				}
			}
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}

	/**
	 * Validate Vendor Code against SAP
	 */
//	public <T>  Result validateVendorCodeFromSap(String queryString, HttpServletRequest request) {
//		try {
//			List<SCIMUser> scimUsers = SCIUserManagementServiceImpl.getInstance().getVendorCodeFromSap(queryString, destinationAdapater.getSCIMDestinationHttpClient(request));
//			if(t==SCIMUser.class){
//				return new Result(ResultType.SUCCESS, scimUsers);
//			} else if(t==FPSCIUserAccount.class){
//				if(!scimUsers.isEmpty()){
//					FPSCIUserAccount fpsciUserAccount = SCIUtils.toFPSCIUserAccount(scimUsers);
//					return new Result(ResultType.SUCCESS,fpsciUserAccount);
//				}
//			    return new Result(ResultType.SUCCESS, scimUsers);
//			}throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST,"Unknown request");
//		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
//			if(e instanceof HttpResponseException){
//				int statusCode = ((HttpResponseException)e).getStatusCode();
//				if(statusCode==HttpServletResponse.SC_NOT_FOUND){
//					return new Result(ResultType.SUCCESS,new ArrayList<SCIMUser>());
//				}
//			}
//			return new Result(ResultType.ERROR,e.getMessage());
//		}
//	}

	@Override
	public Result deleteSAPSCIAccount(String userID, HttpServletRequest request) {
		try {
			ResultType parseDeleteAccount = SCIUserManagementServiceImpl.getInstance().parseDeleteAccount(userID, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
			return new Result(ResultType.SUCCESS, parseDeleteAccount);
		} catch (HttpResponseException|NamingException | DestinationAccessException e) {
			String message = e.getMessage();
			if(e instanceof HttpResponseException){
				KeyVal keyVal = new KeyVal(String.valueOf(((HttpResponseException) e).getStatusCode()), message);
				return new Result(ResultType.ERROR,keyVal);
			} else{
				return new Result(ResultType.ERROR,message);
			}

		}
	}
	public Result addUserToSCIGroup(SCIMUser scimUser,SCIMUserGroup userGroupType, HttpServletRequest request){
		try{
			SCIMUser user=new SCIMUser();
			Set<SCIMGroup> groups = scimUser.getGroups();
			String authGroup=   request.getSession().getServletContext().getInitParameter(userGroupType.name());
			SCIMGroup e = new SCIMGroup(authGroup);
			if(!groups.contains(e)){
				groups.add(e);
				user.setGroups(groups);
				user.setId(scimUser.getId());
				SCIMUser updateUserAccount = SCIUserManagementServiceImpl.getInstance().updateUserAccount(user, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
				return new Result(ResultType.SUCCESS, updateUserAccount);
			}else{
				return new Result(ResultType.SUCCESS, scimUser);
			}
		}catch(HttpResponseException|NamingException | DestinationAccessException e){
			String message = e.getMessage();
			if(e instanceof HttpResponseException){
				KeyVal keyVal = new KeyVal(String.valueOf(((HttpResponseException) e).getStatusCode()), message);
				return new Result(ResultType.ERROR,keyVal);
			} else{
				return new Result(ResultType.ERROR,message);
			}
		}
	}
	public Result removeUserToSCIGroup(SCIMUser scimUser,SCIMUserGroup userGroupType, HttpServletRequest request){
		try{
			SCIMUser user=new SCIMUser();
			Set<SCIMGroup> groups = scimUser.getGroups();
			SCIMGroup o = new SCIMGroup(userGroupType.name());
			if(groups.contains(o)){
				groups.remove(o);
				user.setGroups(groups);
				user.setId(scimUser.getId());
				SCIMUser updateUserAccount = SCIUserManagementServiceImpl.getInstance().updateUserAccount(user, btpDestinationServiceAdapter.getSCIMDestinationHttpClient(request));
				return new Result(ResultType.SUCCESS, updateUserAccount);
			} else{
				return new Result(ResultType.SUCCESS, scimUser);
			}

		}catch(HttpResponseException|NamingException | DestinationAccessException e){
			String message = e.getMessage();
			if(e instanceof HttpResponseException){
				KeyVal keyVal = new KeyVal(String.valueOf(((HttpResponseException) e).getStatusCode()), message);
				return new Result(ResultType.ERROR,keyVal);
			} else{
				return new Result(ResultType.ERROR,message);
			}
		}
	}

	/**
	 * Initialize user both on SCI And Document share
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
	 *
	 * Steps:
	 * 1. Vendor Registration --> Obtain the Activation Link
	 * 2. Retrieve the registered Vendor User --> Obtain the Vendor P User Id
	 * 3. Update the Vendor Account using P User Id with all necessary information
	 * 4. Persist Vendor Records in DSVendorAdministrator, DSVendorAccount and VendorToAdminMapping
	 * 5. Send out an email with Activation Link in Asynchronous mode
	 */
	@Override
	public  Result initializeUserForSCIAccountNDOCShare(final XSUserInfo xsUserInfoAdapter, FPSCIUserAccount  userAccount, SCIMUserGroup userGroupType, HttpServletRequest request, String baseUrl, boolean isBulk) throws AccountNotExistsException, AccountExistsException, AccountManagementException, AccountUpdateSCIException, AccountDetailsException, MethodNotSupportedException {
		String loggedInUserID= SCIIdentityProxy.getLoggedUserId(xsUserInfoAdapter);
		String accountFlpLink = AccountFLPUrl.getUrl(request.getRequestURL().toString());
		String targetUrl = MailSenderUtil.getAccountTargetUrl(baseUrl);
		if(userGroupType==SCIMUserGroup.VendorDocumentsAdministrator){
			Result  resolveVendorAccountDependency = resolveVendorAccountDependency(userAccount,request);
			SCIMUser queryResult = (SCIMUser)resolveVendorAccountDependency.getResult();
			boolean userExists = queryResult!=null;
			if(userExists){
				System.out.println(String.format("User %s exists", userAccount.getUserID()));
				String activationUrl=targetUrl;
				System.out.println(String.format("Issued id  %s exists", queryResult.getId()));
				System.out.println(String.format("Activation url %s ", userAccount.getUserID()));
				try {
					Result searchResult = getUMESAPSCIAccountDetails(queryResult.getId(), request);
					if(searchResult.getResultType()==ResultType.SUCCESS){
						final FPSCIUserAccount fpsciUserAccount = (FPSCIUserAccount) searchResult.getResult();
						final String userID = fpsciUserAccount.getUserID();
						queryResult.setUserName(userID);
						userAccount.setLoginName(userID);
					}
				} catch (SCIServiceException e) {
					throw new AccountNotExistsException(e);
				}
				return createVendorsAndMapping(userAccount, userGroupType, request, isBulk, loggedInUserID, activationUrl, queryResult, true,accountFlpLink);
			}else{

				System.out.println(String.format("User %s doesnt exists", userAccount.getUserID()));
				userAccount.setOrganisationWideID(userAccount.getUserID());
				userAccount.setSpCustomAttribute1(userAccount.getVendorCode());
				userAccount.setTargetUrl(targetUrl);

				//Registering a Vendor Administrator and obtaining an Activation Link
				Result createSAPSCIAccount = createSAPSCIAccountWithActivationCallbackUrl(userAccount, request);
				System.out.println(String.format("User in ume created:%s", createSAPSCIAccount.getResult()));
				Object response = createSAPSCIAccount.getResult();
				System.out.println(String.format("User in ume create stat:%s", createSAPSCIAccount.getResultType()));
				if(createSAPSCIAccount.getResultType()==ResultType.SUCCESS){ // If the Vendor Registration is Successful
					final String activationUrl = String.valueOf(response); //Received Activation URL
					System.out.println(String.format("User in ume created:%s", activationUrl));
					//Retrieve the P-User Id from the registered account
					String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(userAccount.getEmail()).append("%22").toString();
					Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);
					System.out.println(String.format("parseSCIMUserQuery:%s", parseSCIMUserQuery.getResultType()));
					//update the vendor administrator record with additional details
					if(parseSCIMUserQuery.getResultType()==ResultType.SUCCESS){
						@SuppressWarnings("unchecked")
						List<SCIMUser> scimUsers=(List<SCIMUser>)parseSCIMUserQuery.getResult();
						queryResult = scimUsers.get(0);
						System.out.println(String.format("created user:%s", queryResult.getUserID()));
						return createVendorsAndMapping(userAccount, userGroupType, request, isBulk, loggedInUserID, activationUrl, queryResult, false,accountFlpLink);
					}else{
						throw new AccountNotExistsException(String.valueOf(parseSCIMUserQuery.getResult()));
					}
				}else{
					StringBuffer message = getSCIResponseMessage(response);
					throw new AccountManagementException(message.toString());
				}
			}
		}else if(userGroupType==SCIMUserGroup.VendorDocumentsEndUser){
			long vendorID = validateVendorUserDependencies(userAccount);
			Result resolveVendorUserDependency = resolveVendorUserDepedency(userAccount, request);
			SCIMUser queryResult = (SCIMUser)resolveVendorUserDependency.getResult();
//			String targetUrl = MailSenderUtil.getAccountTargetUrl(baseUrl);
			boolean userExists = queryResult!=null;
			if(userExists){
				String activationUrl=targetUrl;
				try {
					Result searchResult = getUMESAPSCIAccountDetails(queryResult.getId(), request);
					if(searchResult.getResultType()==ResultType.SUCCESS){
						FPSCIUserAccount fpsciUserAccount = (FPSCIUserAccount) searchResult.getResult();
						String userID = fpsciUserAccount.getUserID();
						queryResult.setUserName(userID);
						userAccount.setLoginName(userID);
					}
				} catch (SCIServiceException e) {
					e.printStackTrace();
				}

				SCIMUser updateSciUserDetails = new SCIMUser();
				return updateExistingSCIMUserAndCreatedVendorUser(userAccount, userGroupType, request, isBulk, loggedInUserID,
						vendorID, queryResult, activationUrl, updateSciUserDetails, true,targetUrl,accountFlpLink);

			}else{
				userAccount.setOrganisationWideID(userAccount.getUserID());
				userAccount.setSpCustomAttribute1(userAccount.getVendorCode());
				userAccount.setTargetUrl(targetUrl);
				Result createSAPSCIAccount = createSAPSCIAccountWithActivationCallbackUrl(userAccount, request);
				if(createSAPSCIAccount.getResultType()==ResultType.SUCCESS){
					//Send activation message
					String activationUrl = String.valueOf(createSAPSCIAccount.getResult());
					String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(userAccount.getEmail()).append("%22").toString();
					Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);
					//update the vendor administrator record with additional details
					if(parseSCIMUserQuery.getResultType()==ResultType.SUCCESS){
						@SuppressWarnings("unchecked")
						List<SCIMUser> scimUsers=(List<SCIMUser>)parseSCIMUserQuery.getResult();
						queryResult = scimUsers.get(0);
						SCIMUser updateSciUserDetails = new SCIMUser();
						return updateExistingSCIMUserAndCreatedVendorUser(userAccount, userGroupType, request, isBulk,loggedInUserID, vendorID,
								queryResult, activationUrl, updateSciUserDetails,userExists,targetUrl,accountFlpLink);
					}else{
						throw new AccountNotExistsException(String.valueOf(parseSCIMUserQuery.getResult()));
					}
				}else{
					throw new AccountManagementException(String.valueOf(createSAPSCIAccount.getResult()));
				}
			}
		}
		else if(userGroupType==SCIMUserGroup.InternalDocumentsEndUser){
			Result resolveInternalUserDepedency = resolveInternalUserDepedency(userAccount, request);
			SCIMUser queryResult = (SCIMUser)resolveInternalUserDepedency.getResult();
//			String targetUrl = MailSenderUtil.getAccountTargetUrl(baseUrl);
			boolean updateExistingSCIDetails = queryResult!=null;
			if(updateExistingSCIDetails){
				String activationUrl=targetUrl;
				return updateExistingSCIMUserRecordAndCreatedInternalUserAccount(userAccount, userGroupType, request, isBulk, loggedInUserID, activationUrl, queryResult);

			}else{
				InternalUser fairPriceUser = fairPriceUserGroupBean.findByUserEmail(userAccount.getEmail());
				if(fairPriceUser!=null){
					throw new AccountExistsException("Account already exists with the emails "+userAccount.getEmail());
				}
				userAccount.setOrganisationWideID(userAccount.getUserID());
				userAccount.setTargetUrl(targetUrl);
				Result createSAPSCIAccount = createSAPSCIAccountWithActivationCallbackUrl(userAccount, request);
				if(createSAPSCIAccount.getResultType()==ResultType.SUCCESS){
					//Send activation message
					String activationUrl = String.valueOf(createSAPSCIAccount.getResult());
					String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(userAccount.getEmail()).append("%22").toString();
					Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);
					//update the vendor administrator record with additional details
					if(parseSCIMUserQuery.getResultType()==ResultType.SUCCESS){
						@SuppressWarnings("unchecked")
						List<SCIMUser> scimUsers=(List<SCIMUser>)parseSCIMUserQuery.getResult();
						queryResult = scimUsers.get(0);
						return updateExistingSCIMUserRecordAndCreatedInternalUserAccount(userAccount, userGroupType, request, isBulk, loggedInUserID, activationUrl, queryResult);
					}else{
						throw new AccountNotExistsException(String.valueOf(parseSCIMUserQuery.getResult()));
					}
				}else{
					throw new AccountManagementException(String.valueOf(createSAPSCIAccount.getResult()));
				}
			}
		}
		throw new MethodNotSupportedException();
	}

	/**
	 *
	 * @param userAccount
	 * @param userGroupType
	 * @param request
	 * @param isBulk
	 * @param loggedInUserID
	 * @param targetUrl
	 * @param queryResult
	 * @param userExists
	 * @param accountFlpLink
	 * @return
	 * @throws AccountUpdateSCIException
	 */
	private Result createVendorsAndMapping(FPSCIUserAccount userAccount, SCIMUserGroup userGroupType,
										   HttpServletRequest request, boolean isBulk, String loggedInUserID, String targetUrl, SCIMUser queryResult,
										   boolean userExists,String accountFlpLink) throws AccountUpdateSCIException{

		VendorAccount vendorAccount;
		VendorAccountAdministrator vendorAccountAdministrator = null;
		List<VendorAccountModel> vendorAccountModelList;
		final ResultWithObjectSet result = new ResultWithObjectSet();
		// For Bulk Vendors
		if(isBulk){
			VendorAccountModel vendorDto = new VendorAccountModel();
			vendorDto.setVendorCode(userAccount.getVendorCode());
			vendorDto.setVendorName(userAccount.getCompanyName());
			vendorAccountModelList = new ArrayList<>();
			vendorAccountModelList.add(vendorDto);
		}else{ // For Single Vendors
			vendorAccountModelList = userAccount.getVendorAccounts();
		}
		int cnt = 1;
		int listSize = (vendorAccountModelList !=null && !vendorAccountModelList.isEmpty()) ? vendorAccountModelList.size() : 0;
//		String appendVendorsInMail = formatVendorAccountsForEmail(vendorAccountList);
		for (VendorAccountModel account : vendorAccountModelList) {
			userAccount.setVendorCode(account.getVendorCode());
			userAccount.setCompanyName(account.getVendorName());
			System.out.println(String.format("Checking vendor details:%s", account));
			//Create Vendor Record in DSVendor
			ResultWithObjectSet localResults= createVendorAccountRecord (userAccount, userGroupType, request, isBulk, loggedInUserID, targetUrl, queryResult, (cnt==1));

			final String vendorCodeReference = String.format("%s_%s", VENDOR_ACCOUNT, userAccount.getVendorCode());
			final Object o = localResults.getRelMap().get(vendorCodeReference);
			result.addValue(vendorCodeReference,o);
			if(o instanceof VendorAccount){
				vendorAccount = (VendorAccount) result.getRelMap().get(VENDOR_ACCOUNT+"_"+account.getVendorCode());
				// Make Appropriate Entries in DSVendorAdministrator and VendorToAdminMapping Tables
				vendorAccountAdministrator = initializeVendorAdministratorAccount(userAccount, loggedInUserID, queryResult, vendorAccount,userExists, vendorAccountAdministrator);
				// Preparing Data  and Dispatch the Notification in the last Iteration
				if(cnt == listSize){
					ActivationNotificationRequest createActivationRequestNotification = createActivationRequestNotification(targetUrl, result, vendorAccount);
					asyncAccountActivation.dispatchAccountActivationNotifications(createActivationRequestNotification, vendorAccountAdministrator,accountFlpLink);
				}
			}
			cnt++;
		}
		ResultWithObjectSet vendorAccountResponse = new ResultWithObjectSet(ResultType.SUCCESS, result);
		return vendorAccountResponse;
	}

//	private String formatVendorAccountsForEmail(List<VendorAccounts> vendorAccountList){
//		StringBuffer bf = new StringBuffer();
//		for (VendorAccounts account : vendorAccountList) {
//			bf.append(account.getVendorCode()+"  -  "+account.getVendorName());
//			bf.append("\n");
//		}
//		return bf.toString();
//	}

	/**
	 *
	 * @param userAccount
	 * @param userGroupType
	 * @param request
	 * @param isBulk
	 * @param loggedInUserID
	 * @param targetUrl
	 * @param queryResult
	 * @param isFirstRecord
	 * @return
	 * @throws AccountUpdateSCIException
	 */
	private ResultWithObjectSet createVendorAccountRecord(FPSCIUserAccount userAccount, SCIMUserGroup userGroupType,
														  HttpServletRequest request, boolean isBulk, String loggedInUserID, String targetUrl, SCIMUser queryResult,boolean isFirstRecord)  throws AccountUpdateSCIException{
		final ResultWithObjectSet result = new ResultWithObjectSet();
		Result sciUpdateResponse = null;
		if(isFirstRecord || isBulk){ // Update the Registered Vendor Account
			sciUpdateResponse = updateExistingSCIMUser(userAccount, userGroupType, request, queryResult,true);
		}
		if(!isFirstRecord || (sciUpdateResponse!=null && sciUpdateResponse.getResultType()==ResultType.SUCCESS)){
			VendorAccount vendorAccount =new VendorAccount();
			vendorAccount.setActivatedBy(loggedInUserID);
			vendorAccount.setCreatedBy(loggedInUserID);
			vendorAccount.setAllocatedQuota(Long.valueOf(0));
			SCIUtils.updateVendorAccount(userAccount,queryResult.getId(),vendorAccount);
			vendorAccount.setSystemAccountStatus(userAccount.getSystemAccountStatus());
			Result createAccount = createAccount(vendorAccount,targetUrl);
			if(createAccount.getResultType()!=ResultType.SUCCESS){
				throw new AccountUpdateSCIException(String.valueOf(createAccount.getResult()));
			}
			result.addValue(VENDOR_ACCOUNT+"_"+vendorAccount.getVendorCode(), createAccount.getResult());
			return result;
		}else{
			Object response = sciUpdateResponse.getResult();
			StringBuffer message = getSCIResponseMessage(response);
			throw new AccountUpdateSCIException(message.toString());
		}
	}

	/**
	 *
	 *
	 * @param userAccount
	 * @param userGroupType
	 * @param request
	 * @param queryResult
	 * @param isVendorAdmin
	 * @return
	 */
	private Result updateExistingSCIMUser(FPSCIUserAccount userAccount, SCIMUserGroup userGroupType,
										  HttpServletRequest request, SCIMUser queryResult,boolean isVendorAdmin){
		SCIMUser updateSciUserDetails = new SCIMUser();
		updateSciUserDetails.setId((queryResult!=null && queryResult.getId()!=null) ? queryResult.getId() : userAccount.getSciUserId());
		updateSciUserDetails.setCompany(userAccount.getCompanyName());
		updateSciUserDetails.setJobTitle(userAccount.getJobTitle());
		updateSciUserDetails.setUserName(userAccount.getUserID());
		updateSciUserDetails.setSalutation(userAccount.getSalutation());
		//Set Name Properties
		SCIMName nameProperty = new SCIMName();
		nameProperty.setHonorificPrefix(userAccount.getSalutation());
		nameProperty.setGivenName(userAccount.getFirstName());
		nameProperty.setFamilyName(userAccount.getLastName());
		updateSciUserDetails.setName(nameProperty);

		//Email Update
		SCIMEmail emailProperty = new SCIMEmail();
		emailProperty.setValue(userAccount.getEmail());
		Set<SCIMEmail> emailSet = new HashSet<SCIMEmail>();
		emailSet.add(emailProperty);
		updateSciUserDetails.setEmails(emailSet);
		updateSciUserDetails.setDisplayName(userAccount.getDisplayName());

		//Contact Information
		updateSciUserDetails.setPhoneNumbers(getContactInfo(userAccount,null));

		//Groups Information
		updateSciUserDetails.setGroups(getGroupsInfo(userGroupType, queryResult, request,userAccount,isVendorAdmin));
		updateSciUserDetails.setRelationshipToSAP("03");
		//Account Status
		if(userAccount.getSystemAccountStatus() != null && userAccount.getSystemAccountStatus() != SystemAccountStatus.NEW){
			updateSciUserDetails.setActive(userAccount.getSystemAccountStatus() == SystemAccountStatus.ACTIVE);
		}else{
			userAccount.setSystemAccountStatus(SystemAccountStatus.NEW);
		}
		Result updateSAPSCIAccount = updateSAPSCIAccount(updateSciUserDetails, request);
		return updateSAPSCIAccount;
	}

	private VendorAccountAdministrator initializeVendorAdministratorAccount(FPSCIUserAccount userAccount, String loggedInUserID, SCIMUser queryResult, VendorAccount vendorAccount, boolean userExists, VendorAccountAdministrator vendorAccountAdministrator) {
		if(vendorAccountAdministrator == null){
			vendorAccountAdministrator =new VendorAccountAdministrator();
			vendorAccountAdministrator.setActivatedBy(loggedInUserID);
			vendorAccountAdministrator.setCreatedBy(loggedInUserID);
			vendorAccountAdministrator.setSciAccountID(queryResult.getId());
			String userName = getVendorUserName(userAccount, queryResult,userExists);
			vendorAccountAdministrator.setUserName(userName);
			vendorAccountAdministrator.setJobTitle(userAccount.getJobTitle());
			vendorAccountAdministrator.setSystemUserRole(SystemUserRole.VendorAdministrator);
			vendorAccountAdministrator.setCreateDateTime(new Date());
			vendorAccountAdministrator.setVendorUserEmail(userAccount.getEmail());
			vendorAccountAdministrator.setFirstName(userAccount.getFirstName());
			vendorAccountAdministrator.setLastName(userAccount.getLastName());
			try {
				vendorAccountAdministrator = vendorAdministratorBean.findByVendorUserEmail(userAccount.getEmail());
			}
			catch (AccountNotExistsException e1) {
				try {
					vendorAccountAdministrator = vendorAdministratorBean.add(vendorAccountAdministrator);
				} catch (AccountManagementException e) {
					e.printStackTrace();
				}
			}
		}
		//Maintain Vendor Authorization
		maintainVendorAuthorization(vendorAccountAdministrator,vendorAccount,userAccount.getSelectedGroups());

		return mapVendorAccountAndAdmin(vendorAccountAdministrator, vendorAccount);
	}
	/**
	 * Maintain Authorization for Vendor Administrator
	 *
	 * @param vendorAdmin
	 * @param vendorAccount
	 * @param groupDetails
	 */
	public void maintainVendorAuthorization(VendorAccountAdministrator vendorAdmin, VendorAccount vendorAccount, List<String> groupDetails) {
		VendorAuthorizationGroups vendAuth = null;

		//In the case when Temp Vendor account is converted to an Administrator account
		if(groupDetails == null){
			List<ApplicationGroupsLookup> grpLookup = vendorAccountBean.findGroupsByType("ADMIN");
			groupDetails = new ArrayList<String>();
			for (ApplicationGroupsLookup appLk : grpLookup) {
				groupDetails.add(appLk.getGroupName());
			}
		}

		try{
			//Delete All authorizations before creating a new one
//			vendorAccountBean.deleteAuth(vendorAdmin.getVendorAdminID(),true);

			//Iterate and Assign Groups to Vendor Administrator
			for (String grpName : groupDetails) {
				if(grpName!=null && grpName.trim().length()>0){
					vendAuth = vendorAccountBean.findGroupsByUserNVendorCode(vendorAdmin.getVendorAdminID(), vendorAccount.getVendorCode(), grpName, true);
					if(vendAuth == null ) {
						vendAuth = new VendorAuthorizationGroups();
						vendAuth.setVendorAdminID(vendorAdmin.getVendorAdminID());
						vendAuth.setGroupName(grpName);
						vendAuth.setGroupDesc(vendorAccountBean.getGroupDesc(grpName));
						vendAuth.setVendorCode(vendorAccount.getVendorCode());
						vendAuth.setVendorName(vendorAccount.getVendorName());
						vendorAccountBean.addAuth(vendAuth);
					}
				}
			}
		}catch(AccountManagementException e){
			e.printStackTrace();
		}
	}

	private VendorAccountAdministrator mapVendorAccountAndAdmin(VendorAccountAdministrator vendorAccountAdministrator, VendorAccount vendorAccount){
		VendorToAdminMapping userToVendorSubscription=new VendorToAdminMapping();
		userToVendorSubscription.setVendorAdministrator(vendorAccountAdministrator);
		userToVendorSubscription.setVendorAccount(vendorAccount);
		try {
			vendorToAdminMappingBean.add(userToVendorSubscription);
		} catch (AccountManagementException e) {
			e.printStackTrace();
		}
		return vendorAccountAdministrator;
	}

	/**
	 * Maintain Authorization for Vendor User
	 *
	 * @param vendorUserId
	 * @param authorizationList
	 */
	private void maintainVendorUserAuthorization(Long vendorUserId,List<VendorAccountModel> authorizationList) {
		VendorAuthorizationGroups vendAuth = null;
		try{
//			List<ApplicationGroupsLookup> groupDetails = vendorAccountBean.findGroupsByType("USER");
//			HashMap<String, String> groupProp = new HashMap<String,String>();
//			for (ApplicationGroupsLookup grpInfo : groupDetails) {
//				groupProp.put(grpInfo.getGroupName(), grpInfo.getGroupDesc());
//			}
			String[] assignedGroups;
			vendorAccountBean.deleteAuth(vendorUserId,false);
			for (VendorAccountModel auth : authorizationList) {
				assignedGroups = auth.getGroupsAssigned().split(",");
				for (int i = 0; i < assignedGroups.length; i++) {
//					vendAuth = vendorAccountBean.findGroupsByUserNVendorCode(vendorUserId, auth.getVendorCode(), assignedGroups[i], false);
					if(assignedGroups[i]!=null && assignedGroups[i].trim().length()>0){
						vendAuth = new VendorAuthorizationGroups();
						vendAuth.setVendorUserID(vendorUserId);
						vendAuth.setGroupName(assignedGroups[i]);
//					vendAuth.setGroupDesc(groupProp.get(assignedGroups[i]));
						vendAuth.setGroupDesc(vendorAccountBean.getGroupDesc(assignedGroups[i]));
						vendAuth.setVendorCode(auth.getVendorCode());
						vendAuth.setVendorName(auth.getVendorName());
						vendorAccountBean.addAuth(vendAuth);
					}
				}
			}
		}catch(AccountManagementException e){
			e.printStackTrace();
		}
	}

	private VendorAccountAdministrator initializeVendorAdministratorAccount(FPSCIUserAccount userAccount, String loggedInUserID, SCIMUser queryResult, VendorAccount vendorAccount, boolean userExists) {
		VendorAccountAdministrator vendorAccountAdministrator =new VendorAccountAdministrator();
		vendorAccountAdministrator.setActivatedBy(loggedInUserID);
		vendorAccountAdministrator.setCreatedBy(loggedInUserID);
		vendorAccountAdministrator.setSciAccountID(queryResult.getId());
		String userName = getVendorUserName(userAccount, queryResult,userExists);
		vendorAccountAdministrator.setUserName(userName);
		vendorAccountAdministrator.setJobTitle(userAccount.getJobTitle());
		vendorAccountAdministrator.setSystemUserRole(SystemUserRole.VendorAdministrator);
		vendorAccountAdministrator.setCreateDateTime(new Date());
		vendorAccountAdministrator.setVendorUserEmail(userAccount.getEmail());
		vendorAccountAdministrator.setFirstName(userAccount.getFirstName());
		vendorAccountAdministrator.setLastName(userAccount.getLastName());
		try {
			vendorAccountAdministrator = vendorAdministratorBean.findByVendorUserEmail(userAccount.getEmail());
		}
		catch (AccountNotExistsException e1) {
			try {
				vendorAccountAdministrator = vendorAdministratorBean.add(vendorAccountAdministrator);
			} catch (AccountManagementException e) {
				e.printStackTrace();
			}
		}
		VendorToAdminMapping userToVendorSubscription=new VendorToAdminMapping();
		userToVendorSubscription.setVendorAdministrator(vendorAccountAdministrator);
		userToVendorSubscription.setVendorAccount(vendorAccount);
		try {
			vendorToAdminMappingBean.add(userToVendorSubscription);
		} catch (AccountManagementException e) {
			e.printStackTrace();
		}
		return vendorAccountAdministrator;
	}
	/**
	 *
	 * @param userAccount
	 * @param vendorAccount
	 * @return
	 */
	private Set<SCIMPhoneNumber> getContactInfo(FPSCIUserAccount userAccount, VendorAccount vendorAccount){

		userAccount = (userAccount!=null)? userAccount : new FPSCIUserAccount();
		String work = (vendorAccount!=null)? vendorAccount.getWork() : userAccount.getWork();
		String	mobile = (vendorAccount!=null)? vendorAccount.getMobile() : userAccount.getMobile();

		userAccount.addProperty(SCIMPhoneNumber.Type.work.toString(), work);
		userAccount.addProperty(SCIMPhoneNumber.Type.mobile.toString(), mobile);

		HashMap<String, String> props = userAccount.getAddedProperty();
		String key;
		Set<SCIMPhoneNumber> phoneNumbers = null;
		if(props!=null){
			phoneNumbers = new HashSet<SCIMPhoneNumber>();
			SCIMPhoneNumber contactInfo = null;
			for (Iterator<String> iterator = props.keySet().iterator(); iterator
					.hasNext();) {
				key = (String) iterator.next();
				contactInfo = new SCIMPhoneNumber();
				contactInfo.setType(SCIMPhoneNumber.Type.valueOf(key).toString());
				contactInfo.setValue(props.get(key));
				phoneNumbers.add(contactInfo);
			}
		}
		return phoneNumbers;
	}

	/**
	 * @param userGroupType
	 * @param queryResult
	 * @param request
	 * @return
	 */
	private Set<SCIMGroup> getGroupsInfo(SCIMUserGroup userGroupType,SCIMUser queryResult,HttpServletRequest request,FPSCIUserAccount userAccount,boolean isVendorAdmin){

		List<String> tempGroups = new ArrayList<String>();
		HashSet<String> grpSet = new HashSet<String>();
		String[] assignedGroups = null;
		List<String> tempList = null;
//		if(isVendorAdmin){ // Group Assignment for Vendor Admin
////			tempGroups.add(request.getSession().getServletContext().getInitParameter(userGroupType.name()));
////			tempGroups.add(request.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsEndUser.name()));
//			if(userAccount.getSelectedGroups()!=null){
//				assignedGroups = userAccount.getSelectedGroups().split(",");
//				for (int i = 0; i < assignedGroups.length; i++) {
//					if(!grpSet.contains(assignedGroups[i])){
//						tempGroups.add(assignedGroups[i]);
//					}
//				}
//			}
//
//		}else{ //Group Assignment for Vendor Users
//			for (VendorAccounts auth : userAccount.getVendorAccounts()) {
//				assignedGroups = auth.getGroupsAssigned().split(",");
//				for (int i = 0; i < assignedGroups.length; i++) {
//					if(!grpSet.contains(assignedGroups[i])){
//						tempGroups.add(assignedGroups[i]);
//					}
//				}
//			}
//		}
		int tempSize = (!isVendorAdmin && !userAccount.getVendorAccounts().isEmpty() && userAccount.getVendorAccounts().size()>0) ? userAccount.getVendorAccounts().size() : 1;

		for (int i = 0; i < tempSize; i++) {
			if(isVendorAdmin && userAccount.getSelectedGroups()!=null && !userAccount.getSelectedGroups().isEmpty()){
				tempList = userAccount.getSelectedGroups();
				assignedGroups = tempList.toArray(new String[tempList.size()]);
			}else if(!isVendorAdmin){
				assignedGroups = userAccount.getVendorAccounts().get(i).getGroupsAssigned().split(",");
			}

			if(assignedGroups!=null){
				//Iterate on Assigned Groups
				for (String tempGrp : assignedGroups) {
					if(!grpSet.contains(tempGrp)){
						tempGroups.add(tempGrp);
					}
				}
			}
		}


		//Assign Selected Groups in SCIM Object
//		queryResult.getGroups()
		Set<SCIMGroup> updateAssignments = new HashSet<SCIMGroup>();
		for (String grp : tempGroups) {
			updateAssignments.add(new SCIMGroup(grp));
		}
		return updateAssignments;
	}

//	public static void main(String[] args) {
//		List<String> tempList = new ArrayList<String>();
//		tempList.add("a");
//		tempList.add("b");
//
//		String[] tempArray = tempList.toArray(new String[tempList.size()]);
//
//System.out.println(tempArray.length + "/n" + tempArray[0] +" & " + tempArray[1]);
//	}

	private String getVendorUserName(FPSCIUserAccount userAccount, SCIMUser queryResult, boolean userExists) {
		String loginName = userAccount.getLoginName();
		if(!userExists){
			return (loginName==null)?userAccount.getUserID():loginName;
		}else{
			String userName = loginName;
			if(StringUtils.isEmpty(userName)){
				userName= queryResult.getUserName();
			}
			if(StringUtils.isEmpty(userName)){
				userName=queryResult.getId();
			}
			return userName;
		}

	}
	private String getUserName(FPSCIUserAccount userAccount, SCIMUser queryResult) {
		String userName = userAccount.getLoginName();
		if(StringUtils.isEmpty(userName)){
			userName= queryResult.getUserName();
		}
		if(StringUtils.isEmpty(userName)){
			userName=queryResult.getId();
		}
		return userName;
	}
	private Result updateExistingSCIMUserRecordAndCreatedInternalUserAccount(FPSCIUserAccount userAccount,
																			 SCIMUserGroup userGroupType, HttpServletRequest request, boolean isBulk, String loggedInUserID,
																			 String activationUrl, SCIMUser queryResult) throws AccountUpdateSCIException {
		InternalUser fairPriceUser;
		SCIMUser updateSciUserDetails = new SCIMUser();
		updateSciUserDetails.setId(queryResult.getId());
		updateSciUserDetails.setCompany(userAccount.getCompanyName());
		updateSciUserDetails.setRelationshipToSAP("03");//"refer to 03 in the api"
		String authGroup =request.getSession().getServletContext().getInitParameter(userGroupType.name());
		queryResult.getGroups().add(new SCIMGroup(authGroup));
		updateSciUserDetails.setGroups(queryResult.getGroups());
		Result updateSAPSCIAccount = updateSAPSCIAccount(updateSciUserDetails, request);
		if(updateSAPSCIAccount.getResultType()==ResultType.SUCCESS){
			fairPriceUser =new InternalUser();
			fairPriceUser.setActivatedBy(loggedInUserID);
			fairPriceUser.setCreatedBy(loggedInUserID);
			fairPriceUser.setSciAccountID(queryResult.getId());
			fairPriceUser.setCreateDateTime(new Date());
			fairPriceUser.setFairPriceEmail(userAccount.getEmail());
			fairPriceUser.setFirstName(userAccount.getFirstName());
			fairPriceUser.setLastName(userAccount.getLastName());
			fairPriceUser.setDisplayName(userAccount.getDisplayName());
			String userName = getUserName(userAccount, queryResult);
			fairPriceUser.setUserName(userName);
			fairPriceUser.setFairPriceGroupID(userAccount.getFairpriceUserGroupID());
			fairPriceUser.setSystemUserRole(SystemUserRole.FairPriceInternalUser);
			Result result = createAccount(fairPriceUser,activationUrl);
			if(!isBulk){
				if(result.getResultType()==ResultType.SUCCESS){
					List<InternalUser> fairPriceUsers =fairPriceUserGroupBean.findAllInternalUser();
					if (fairPriceUsers == null) {
						fairPriceUsers = new ArrayList<InternalUser>();
					}
					result.setResult(fairPriceUsers);
				}
			}
			return result;
		}else{
			Object response = updateSAPSCIAccount.getResult();
			StringBuffer message = getSCIResponseMessage(response);
			throw new AccountUpdateSCIException(message.toString());
		}
	}
	private Result updateExistingSCIMUserAndCreatedVendorUser(FPSCIUserAccount userAccount, SCIMUserGroup userGroupType,
															  HttpServletRequest request, boolean isBulk, String loggedInUserID, long vendorID, SCIMUser queryResult,
															  String activationUrl, SCIMUser updateSciUserDetails,boolean userExists,String targetUrl,String accountFlpLink) throws AccountUpdateSCIException {

		//Update SCI Details for Vendor User
		Result sciUpdateResponse = updateExistingSCIMUser(userAccount, userGroupType, request, queryResult,false);
		if(sciUpdateResponse.getResultType()==ResultType.SUCCESS){
			VendorUser vendorUser =new VendorUser();
			vendorUser.setActivatedBy(loggedInUserID);
			vendorUser.setCreatedBy(loggedInUserID);
			vendorUser.setVendorAccount(vendorID);
			vendorUser.setCreateDateTime(new Date());
			SCIUtils.assignVendorUserAccount(userAccount, queryResult.getId(), vendorUser);
			Result result = createAccount(vendorUser,activationUrl);

			//Maintain Vendor User Authorization
			if(result.getResult() instanceof VendorUser){
				vendorUser = (VendorUser) result.getResult();
				maintainVendorUserAuthorization(vendorUser.getVendorUserID(), userAccount.getVendorAccounts());

				ActivationNotificationRequest createActivationRequestNotification = createActivationRequestNotification(activationUrl, result, vendorUser);
				asyncAccountActivation.dispatchAccountActivationNotifications(createActivationRequestNotification,vendorUser,accountFlpLink);
			}

//			if(!isBulk){
//				if(result.getResultType()==ResultType.SUCCESS){
//					List<DSVendorUser> vendorUsers =vendorUserBean.findAllByVendorID(vendorID);
//					if (vendorUsers == null) {
//						vendorUsers = new ArrayList<DSVendorUser>();
//					}
//					result.setResult(vendorUsers);
//				}
//			}
			return result;
		}else{
			Object response = sciUpdateResponse.getResult();
			StringBuffer message = getSCIResponseMessage(response);
			throw new AccountUpdateSCIException(message.toString());
		}
	}
	private StringBuffer getSCIResponseMessage(Object response) {
		StringBuffer message=new StringBuffer();
		if(response instanceof KeyVal){
			KeyVal keyVal = (KeyVal)response;
			message.append(keyVal.getKey()).append(":").append(keyVal.getVal());
		}else{
			message.append(String.valueOf(response));
		}
		return message;
	}
	//	private ResultWithObjectSet updateExistingSCIMUserAndCreateVendorAccountRecord(FPSCIUserAccount userAccount, SCIMUserGroup userGroupType,
//			HttpServletRequest request, boolean isBulk, String loggedInUserID, String targetUrl, SCIMUser queryResult,
//			SCIMUser updateSciUserDetails) throws AccountUpdateSCIException {
//		updateSciUserDetails.setId(queryResult.getId());
//		updateSciUserDetails.setCompany(userAccount.getCompanyName());
//		updateSciUserDetails.setJobTitle(userAccount.getJobTitle());
//		updateSciUserDetails.setUserName(userAccount.getUserID());
//
//		//Added for Including Phone numbers on the form and in SCI
//		updateSciUserDetails.setPhoneNumbers(getContactInfo(userAccount,null));
//		updateSciUserDetails.setSalutation(userAccount.getSalutation());
//		updateSciUserDetails.setGroups(getGroupsInfo(userGroupType,queryResult, request));
//		updateSciUserDetails.setRelationshipToSAP("03");
//		Result updateSAPSCIAccount = updateSAPSCIAccount(updateSciUserDetails, request);
//		if(updateSAPSCIAccount.getResultType()==ResultType.SUCCESS){
//			DSVendorAccount vendorAccount =new DSVendorAccount();
//			vendorAccount.setActivatedBy(loggedInUserID);
//			vendorAccount.setCreatedBy(loggedInUserID);
//			SCIUtils.updateVendorAccount(userAccount,queryResult.getId(),vendorAccount);
//			Result createAccount = createAccount(vendorAccount,targetUrl);
//			if(createAccount.getResultType()!=ResultType.SUCCESS){
//				throw new AccountUpdateSCIException(String.valueOf(createAccount.getResult()));
//			}
//			ResultWithObjectSet result = new ResultWithObjectSet(createAccount.getResultType(),createAccount.getResult());
//			result.addValue(VENDOR_ACCOUNT, createAccount.getResult());
//			if(!isBulk){
//				if(result.getResultType()==ResultType.SUCCESS){
//					//XXX
//					List<DSVendorAccount>vendorAccounts =vendorAccountBean.findAll();
//					if (vendorAccounts == null) {
//						vendorAccounts = new ArrayList<DSVendorAccount>();
//					}
//					result.setResult(vendorAccounts);
//				}
//			}
//			return result;
//		}else{
//			Object response = updateSAPSCIAccount.getResult();
//			StringBuffer message = getSCIResponseMessage(response);
//			throw new AccountUpdateSCIException(message.toString());
//		}
//	}
	@SuppressWarnings("unchecked")
	private Result resolveInternalUserDepedency(FPSCIUserAccount userAccount,HttpServletRequest request) throws AccountExistsException {
		validateEmailDependency(userAccount.getEmail(),false);
		String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(userAccount.getEmail()).append("%22").toString();
		Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);

		List<SCIMUser> scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
		SCIMUser recentSCIMUserDetails = SCIUtils.getRecentSCIMUserDetails(scimUsers);
		return new Result(ResultType.SUCCESS, recentSCIMUserDetails);
	}

	@SuppressWarnings("unchecked")
	private ResultWithObjectSet resolveVendorAccountDependency(FPSCIUserAccount userAccount, HttpServletRequest request) throws AccountExistsException {
		if(userAccount.getAction() == null){
			validateEmailDependency(userAccount.getEmail(),true);
			VendorAccount vendorAccount = vendorAccountBean.findByVendorCode(userAccount.getVendorCode());
			if(vendorAccount!=null){
				throw new AccountExistsException("Account already exists with the vendor code "+userAccount.getVendorCode());
			}
		}
		String queryString = new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(userAccount.getEmail()).append("%22").toString();
		Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);
		List<SCIMUser> scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
		SCIMUser recentSCIMUserDetails = SCIUtils.getRecentSCIMUserDetails(scimUsers);
		ResultWithObjectSet resultWithObjectSet = new ResultWithObjectSet(ResultType.SUCCESS, recentSCIMUserDetails);
		return resultWithObjectSet;
	}


	@SuppressWarnings("unchecked")
	private Result resolveVendorUserDepedency(FPSCIUserAccount userAccount,HttpServletRequest request) throws AccountExistsException {
		validateEmailDependency(userAccount.getEmail(),false);
		VendorUser vendorUser = vendorUserBean.findByVendorSCIUserID(userAccount.getVendorCode());
		if(vendorUser!=null){
			throw new AccountExistsException("Account already exists with the emails "+userAccount.getEmail());
		}
		String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(userAccount.getEmail()).append("%22").toString();
		Result parseSCIMUserQuery = parseSCIMUserQuery(queryString, request,SCIMUser.class);

		List<SCIMUser> scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
		SCIMUser recentSCIMUserDetails = SCIUtils.getRecentSCIMUserDetails(scimUsers);
		return new Result(ResultType.SUCCESS, recentSCIMUserDetails);
	}

	public Result deactivateProspectVendorAccount(String sciUserID, Set<SCIMGroup> scimGroups, HttpServletRequest request) {
		Result result = searchSAPSCIAccountDetails(sciUserID, request);
		if(result.getResultType()==ResultType.ERROR){
			return result;
		}
		SCIMUser scimUser = (SCIMUser)result.getResult();
		if(scimUser==null){
			return new Result(ResultType.ERROR, "User Not found");
		}
		Set<SCIMGroup> groups = scimUser.getGroups();
		for(SCIMGroup scimGroup:scimGroups){
			groups.remove(scimGroup);
		}
		SCIMUser userAccount=new SCIMUser();
		userAccount.setId(sciUserID);
		userAccount.setGroups(groups);
		userAccount.setActive(false);
		return updateSAPSCIAccount(userAccount, request);
	}

	public Result removeFromGroupSAPSCIAccount(String sciAccountID,Set<SCIMGroup> scimGroups, HttpServletRequest request)  {
		SCIMUser userAccount=new SCIMUser();
		Result searchSAPSCIAccountDetails = searchSAPSCIAccountDetails(sciAccountID, request);
		Object result = searchSAPSCIAccountDetails.getResult();
		if(searchSAPSCIAccountDetails.getResultType()==ResultType.ERROR){
			return searchSAPSCIAccountDetails;
		}
		SCIMUser scimUser = (SCIMUser)result;
		if(scimUser==null){
			return new Result(ResultType.ERROR, "User Not found");
		}
		Set<SCIMGroup> groups = scimUser.getGroups();
		for(SCIMGroup scimGroup:scimGroups){
			groups.remove(scimGroup);
		}
		userAccount.setId(sciAccountID);
		userAccount.setGroups(groups);
		return updateSAPSCIAccount(userAccount, request);
	}
	public Result activateProspectVendorAccount(String sciAccountID,String loginName,String vendorCode, HttpServletRequest request)  {
		SCIMUser userAccount=new SCIMUser();
		Result searchSAPSCIAccountDetails = searchSAPSCIAccountDetails(sciAccountID, request);
		Object result = searchSAPSCIAccountDetails.getResult();
		if(searchSAPSCIAccountDetails.getResultType()==ResultType.ERROR){
			return searchSAPSCIAccountDetails;
		}
		SCIMUser scimUser = (SCIMUser)result;
		if(scimUser==null){
			return new Result(ResultType.ERROR, "User Not found");
		}
		Set<SCIMGroup> groups = scimUser.getGroups();
		//Removed the dropping or transition
		String vendorAdminGroup =request.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsAdministrator.name());
		groups.add(new SCIMGroup(vendorAdminGroup));
		userAccount.setId(sciAccountID);
		userAccount.setUserName(loginName);
		userAccount.setGroups(groups);
		userAccount.setSpCustomAttribute1(vendorCode);
		return updateSAPSCIAccount(userAccount, request);
	}
	public Result provisionSCIMUserToGroups(String sciAccountID,Set<SCIMGroup> scimGroups, HttpServletRequest request)  {
		final SCIMUser userAccount=new SCIMUser();
		final Result searchSAPSCIAccountDetails = searchSAPSCIAccountDetails(sciAccountID, request);
		Object result = searchSAPSCIAccountDetails.getResult();
		if(searchSAPSCIAccountDetails.getResultType()==ResultType.ERROR){
			return searchSAPSCIAccountDetails;
		}
		SCIMUser scimUser = (SCIMUser)result;
		if(scimUser==null){
			return new Result(ResultType.ERROR, "User Not found");
		}
		final Set<SCIMGroup> groups = scimUser.getGroups();
		for(SCIMGroup scimGroup:scimGroups){
			groups.add(scimGroup);
		}
		userAccount.setId(sciAccountID);
		userAccount.setGroups(groups);
		return updateSAPSCIAccount(userAccount, request);
	}
	public void validateEmailDependency(String email,boolean isVendorAdmin) throws AccountExistsException {

		InternalUser fairPriceUser = fairPriceUserGroupBean.findByUserEmail(email);
		if(fairPriceUser!=null){
			throw new AccountExistsException("Internal user account already activated '".concat(email)+"'");
		}
		if(!isVendorAdmin){
			List<VendorAccount> vendorAccount = vendorAccountBean.findByVendorAdminEmail(email);
			if(vendorAccount!=null&&!vendorAccount.isEmpty()){
				throw new AccountExistsException("Vendor Account has been activated  with email '".concat(email)+"'");
			}
		}
		if(!isVendorAdmin){
			VendorUser vendorUser = vendorUserBean.findByVendorUserEmail(email);
			if(vendorUser!=null){
				throw new AccountExistsException("Vendor User has been activated  with email '".concat(email)+"'");
			}
		}
	}
	@Override
	public Result createVendorAdministratorAccount(VendorAccountAdministrator vendorAccountAdministrator) {
		try {
			VendorAccountAdministrator administrator = vendorAdministratorBean.add(vendorAccountAdministrator);
			return new Result(ResultType.SUCCESS,administrator);
		} catch (AccountManagementException e) {
			return new Result(ResultType.ERROR,e.getMessage());
		}
	}

}
