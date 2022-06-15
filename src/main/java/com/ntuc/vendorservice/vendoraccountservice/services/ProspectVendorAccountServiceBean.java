package com.ntuc.vendorservice.vendoraccountservice.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;

import com.ntuc.vendorservice.foundationcontext.catalog.constants.AccountFLPUrl;
import com.ntuc.vendorservice.notificationservice.entity.ActivationNotificationRequest;
import com.ntuc.vendorservice.notificationservice.services.AsyncAccountActivation;
import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFCompositeAuthGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFCompositeAuthType;
import com.ntuc.vendorservice.scimservice.repository.CWFCompositeAuthGroupRepositoryBean;
import com.ntuc.vendorservice.scimservice.repository.CWFGroupToAppMappingRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.ProspectVendorAccountRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAccountRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAdministratorRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorToAdminMappingRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.services.ManageAccountServiceBean;
import com.ntuc.vendorservice.notificationservice.services.AsyncProspectVendorAccountNotificationBean;
import org.apache.commons.lang.StringUtils;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.VendorAccountStatus;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountExistsException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountNotExistsException;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.scimservice.models.SCIMName;
import com.ntuc.vendorservice.scimservice.models.SCIMPhoneNumber;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorToAdminMapping;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;
import com.ntuc.vendorservice.scimservice.utils.SCIUtils;

@Stateless
@LocalBean
public class ProspectVendorAccountServiceBean implements ProspectVendorAccountService {
	@EJB
	protected ProspectVendorAccountRepositoryBean prospectVendorAccountBean;
	@EJB
	protected ManageAccountServiceBean manageAccountServiceBean;
	@EJB
	protected AsyncProspectVendorAccountNotificationBean asyncMailerEJB;
	@EJB
	protected CWFCompositeAuthGroupRepositoryBean compositeAuthGroupBean;
	@EJB
	protected CWFGroupToAppMappingRepositoryBean groupToAppMappingBean;
	@EJB
	protected VendorToAdminMappingRepositoryBean vendorToAdminMappingBean;
	@EJB
	protected VendorAdministratorRepositoryBean vendorAdministratorBean;
	@EJB
	protected AsyncAccountActivation asyncAccountActivation;
	@EJB
	protected VendorAccountRepositoryBean vendorAccountBean;
	
	@Override
	public Result createTempVendorAccount(ProspectVendorAccount vendorAccount,HttpServletRequest request) throws AccountExistsException, AccountManagementException {
		FPSCIUserAccount fpsciUserAccount= SCIUtils.getFPSCIUserAccount(vendorAccount); 
		CWFCompositeAuthGroup compositeAuthGroup = validateApplicationAuthorizationsGroup();
		
		String email = vendorAccount.getEmail();  
		try{
			String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(email).append("%22").toString();
			validateFields(request, queryString);
			Result createSAPSCIAccount = manageAccountServiceBean.createSAPSCIAccountWithActivationCallbackUrl(fpsciUserAccount, request);
			if(createSAPSCIAccount.getResultType()==ResultType.SUCCESS){
				vendorAccount.setActivationLink(String.valueOf(createSAPSCIAccount.getResult()));
				vendorAccount.setVendorAccountStatus(VendorAccountStatus.PROSPECT);	
				vendorAccount.setCreateDateTime(new Date());
				
				SCIMUser  scimUser=updateSCIUserResource(compositeAuthGroup,vendorAccount,request);
				vendorAccount.setSciUserID(scimUser.getId());
				vendorAccount.setNewSciUserID(vendorAccount.getLoginName());
				Result updateSAPSCIAccount = manageAccountServiceBean.updateSAPSCIAccount(scimUser, request);
				
				if(updateSAPSCIAccount.getResultType()!=ResultType.SUCCESS){
					Object result = updateSAPSCIAccount.getResult();
					String message="";
					if(result instanceof KeyVal){
						KeyVal keyVal = (KeyVal)result;
						message=keyVal.getKey()+":"+keyVal.getVal();
					}else{
						message=request.toString();
					}
					throw new AccountManagementException(message);
				}  
				asyncMailerEJB.sendNotification(vendorAccount, AccountFLPUrl.getUrl(request.getRequestURL().toString()));
				vendorAccount.setEmailSent(true); 
				ProspectVendorAccount prospectVendorAccount = prospectVendorAccountBean.add(vendorAccount);  
				return new Result(ResultType.SUCCESS, prospectVendorAccount);
			}
			throw new AccountManagementException(createSAPSCIAccount.getResult().toString());
		}catch(AccountExistsException e){
			throw new AccountManagementException("Email '"+email+"' already exists, kindly use a different email to proceed");
		}  
	}

	@SuppressWarnings("unchecked")
	private void validateFields(HttpServletRequest request,  String queryString) throws AccountExistsException { 
		 Result parseSCIMUserQuery = manageAccountServiceBean.parseSCIMUserQuery(queryString, request, SCIMUser.class);
		 if(parseSCIMUserQuery.getResultType()==ResultType.SUCCESS){ 
			if(!((List<SCIMUser>) parseSCIMUserQuery.getResult()).isEmpty()){
				throw new AccountExistsException();
			} 
		}
	}

	private CWFCompositeAuthGroup validateApplicationAuthorizationsGroup() throws AccountManagementException {
		final List<CWFCompositeAuthGroup> prospectVendorAuthGroups = compositeAuthGroupBean.findByApplicationType(CWFCompositeAuthType.ProspectVendor);
		if(prospectVendorAuthGroups.isEmpty()){
			throw new AccountManagementException("Kindly request administrator to maintain and activate 'Prospect Vendor' authorization group");
		}
		CWFCompositeAuthGroup compositeAuthGroup = prospectVendorAuthGroups.get(0);
		if(!compositeAuthGroup.isActivated()){
			throw new AccountManagementException("Kindly request administrator to activate '"+compositeAuthGroup.getName()+"' authorization group");
		}
		return compositeAuthGroup;
	}
	/**
	 *
	 * @param compositeAuthGroup
	 * @param vendorAccount
	 * @param request
	 * @return
	 * @throws AccountManagementException
	 */
	private SCIMUser updateSCIUserResource(CWFCompositeAuthGroup compositeAuthGroup,ProspectVendorAccount vendorAccount, HttpServletRequest request) throws AccountManagementException {
		String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(vendorAccount.getEmail()).append("%22").toString();
		Result result=manageAccountServiceBean.parseSCIMUserQuery(queryString, request, SCIMUser.class); 
		if(result.getResultType()==ResultType.SUCCESS){ 
			@SuppressWarnings("unchecked")
			List<SCIMUser> list = (List<SCIMUser>) result.getResult();
			SCIMUser requested=list.get(0);
			
			SCIMUser scimUser=new SCIMUser();
			scimUser.setId(requested.getId());
			
			String industry = vendorAccount.getIndustry();
			if(industry!=null&&!StringUtils.isEmpty(industry)){
				scimUser.setIndustryCrm(industry);
			}
			
			String department = vendorAccount.getDepartment();
			if(department!=null&&!StringUtils.isEmpty(department)){
				scimUser.setDepartment(department);
			}
			String fax = vendorAccount.getFax();
			if(fax!=null&&!StringUtils.isEmpty(fax)){
				SCIMPhoneNumber phoneNumber = new SCIMPhoneNumber();
				phoneNumber.setType(SCIMPhoneNumber.Type.fax.name());
				phoneNumber.setValue(fax);
				scimUser.getPhoneNumbers().add(phoneNumber); 
			}
			String mobile = vendorAccount.getMobile();
			if(mobile!=null&&!StringUtils.isEmpty(mobile)){
				SCIMPhoneNumber phoneNumber = new SCIMPhoneNumber();
				phoneNumber.setType(SCIMPhoneNumber.Type.mobile.name());
				phoneNumber.setValue(mobile);
				scimUser.getPhoneNumbers().add(phoneNumber); 
			}
			scimUser.setCompany(vendorAccount.getCompanyName());
			scimUser.setUserName(vendorAccount.getLoginName());
			SCIMName name = new SCIMName(); 
			name.setHonorificPrefix(vendorAccount.getSalutation());
			scimUser.setName(name);
			SCIMPhoneNumber phoneNumber = new SCIMPhoneNumber();
			phoneNumber.setType(SCIMPhoneNumber.Type.work.name());
			phoneNumber.setValue(vendorAccount.getTelephone());
			scimUser.getPhoneNumbers().add(phoneNumber); 
			List<CWFApplicationGroup> applicationGroups = groupToAppMappingBean.findGroupsByComposition(compositeAuthGroup.getId());
			for(CWFApplicationGroup applicationGroup:applicationGroups){
				scimUser.getGroups().add(new SCIMGroup(applicationGroup.getValue())); 	
			} 
			return scimUser;
		}
		throw new AccountManagementException(result.getResult().toString());
	}

	@Override
	public Result updateTempVendorAccount(ProspectVendorAccount vendorAccount,HttpServletRequest request)throws AccountManagementException, AccountNotExistsException {
		ProspectVendorAccount prospectVendorAccount = getVendorBySCIUserID(vendorAccount.getSciUserID());
		if(prospectVendorAccount==null){
			throw new AccountNotExistsException("Account not exists");
		}
		String email = vendorAccount.getEmail();
		if(prospectVendorAccount.getEmail()!=email){
			throw new AccountManagementException("Account details {email: "+email+"} cannot be updated, details inconsistent");
		} 
		ProspectVendorAccount update = prospectVendorAccountBean.update(vendorAccount);
		return new Result(ResultType.SUCCESS,update);
		 
	}
	

	@Override
	public Result activateTempVendorAccount(FPSCIUserAccount fpSciAccount,HttpServletRequest request,String baseUrl) throws AccountManagementException, AccountNotExistsException,AccountExistsException {
		if(fpSciAccount.getUserID()==null){
			throw new AccountNotExistsException("SCIUserID field has not been provided");
		}
		fpSciAccount.setNewLoginName("admin@"+fpSciAccount.getVendorCode());
		String loginName = fpSciAccount.getNewLoginName();
		String vendorCode = fpSciAccount.getVendorCode();
		if(vendorCode==null||StringUtils.isEmpty(vendorCode)){
			throw new AccountManagementException("Vendor code not provided");
		}else{
			VendorAccount existingAccount = vendorAccountBean.findByVendorCode(fpSciAccount.getVendorCode());
			if(existingAccount != null){
				throw new AccountManagementException("Admin with Vendor Code " + fpSciAccount.getVendorCode() + " already exists in the system");
			}
		}
		
		if(loginName==null||StringUtils.isEmpty(loginName)){
			throw new AccountManagementException("Vendor loginName not provided");
		}
		
		ProspectVendorAccount vendorBySCIUserID = getVendorBySCIUserID(fpSciAccount.getUserID());
		vendorBySCIUserID.setVendorAccountStatus(VendorAccountStatus.ACTIVATED);
		vendorBySCIUserID.setNewSciUserID(vendorBySCIUserID.getLoginName());
		vendorBySCIUserID.setVendorCode(vendorCode);
		vendorBySCIUserID.setLoginName(fpSciAccount.getNewLoginName());
		ProspectVendorAccount update = prospectVendorAccountBean.update(vendorBySCIUserID);
		Result activateProspectVendorAccount = manageAccountServiceBean.activateProspectVendorAccount(vendorBySCIUserID.getSciUserID(),loginName, vendorCode, request);
		if(activateProspectVendorAccount.getResultType()==ResultType.SUCCESS){
			activateAsVendorAdministrator(update,baseUrl);
			return new Result(ResultType.SUCCESS,update);
		}else{
			vendorBySCIUserID.setVendorAccountStatus(VendorAccountStatus.PROSPECT);
			prospectVendorAccountBean.update(vendorBySCIUserID);
		}
		throw new AccountManagementException(String.valueOf(activateProspectVendorAccount.getResult()));
		
	}

	private void activateAsVendorAdministrator(ProspectVendorAccount prospectVendorAccount,String baseUrl) throws AccountExistsException, AccountManagementException {
		manageAccountServiceBean.validateEmailDependency(prospectVendorAccount.getEmail(),true);
		VendorAccount vendorAccount=SCIUtils.getDSVendorAccount(prospectVendorAccount);
		VendorAccountAdministrator vendorAccountAdministrator =SCIUtils.getDSVendorAdministrator(prospectVendorAccount);
		String targetUrl = MailSenderUtil.getAccountTargetUrl(baseUrl);
		
		Result createAccount = manageAccountServiceBean.createAccount(vendorAccount,targetUrl);
		if(createAccount.getResultType()!=ResultType.SUCCESS){
			throw new AccountManagementException(String.valueOf(createAccount.getResult()));
		}
		try {
			vendorAccountAdministrator = vendorAdministratorBean.findByVendorUserEmail(prospectVendorAccount.getEmail());
		}  
		catch (AccountNotExistsException e1) {
			try {
				vendorAccountAdministrator = vendorAdministratorBean.add(vendorAccountAdministrator);
			} catch (AccountManagementException e) {
				e.printStackTrace();
			} 
		}
		final VendorToAdminMapping userToVendorSubscription=new VendorToAdminMapping();
		userToVendorSubscription.setVendorAdministrator(vendorAccountAdministrator);
		userToVendorSubscription.setVendorAccount(vendorAccount);
		try {
			vendorToAdminMappingBean.add(userToVendorSubscription);
			
			//Attain Mapping once the Temp Vendor Account is Transformed to Admin Account
			manageAccountServiceBean.maintainVendorAuthorization(vendorAccountAdministrator, vendorAccount,null);
		
			//Send Notification in case when the Prospect Vendor is Activated as Administrator
			ActivationNotificationRequest createActivationRequestNotification = manageAccountServiceBean.createActivationRequestNotification(targetUrl, new Result(), prospectVendorAccount);
			asyncAccountActivation.dispatchAccountActivationNotifications(createActivationRequestNotification,prospectVendorAccount,null);
		} catch (AccountManagementException e) {
			e.printStackTrace();
		}  
	}

	@Override
	public Result deactivateTempVendorAccount(String sciUserID, HttpServletRequest request) throws  AccountNotExistsException, AccountManagementException {
		ProspectVendorAccount prospectVendorAccount = getVendorBySCIUserID(sciUserID);
		ProspectVendorAccount vendorAccount  = null;
		if(prospectVendorAccount!=null){
			prospectVendorAccount.setVendorAccountStatus(VendorAccountStatus.REJECTED);
			vendorAccount = prospectVendorAccountBean.update(prospectVendorAccount);
		}
		CWFCompositeAuthGroup compositeAuthGroup = validateApplicationAuthorizationsGroup();
		Set<SCIMGroup> scimGroups=new HashSet<SCIMGroup>();
		List<CWFApplicationGroup> applicationGroups = groupToAppMappingBean.findGroupsByComposition(compositeAuthGroup.getId());
		for(CWFApplicationGroup applicationGroup:applicationGroups){
			scimGroups.add(new SCIMGroup(applicationGroup.getValue())); 	
		} 
		manageAccountServiceBean.deactivateProspectVendorAccount(sciUserID, scimGroups, request);
		return new Result(ResultType.SUCCESS,vendorAccount);
	}
	/**
	 * @param sciUserID
	 * @return
	 * @throws AccountNotExistsException
	 */
	private ProspectVendorAccount getVendorBySCIUserID(String sciUserID) throws AccountNotExistsException { 
		if(sciUserID==null){
			throw new AccountNotExistsException("SCIUserID field has not been provided");
		}
		ProspectVendorAccount prospectVendorAccount = prospectVendorAccountBean.findBySciUserID(sciUserID);
		return prospectVendorAccount;
	}
}
