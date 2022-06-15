package com.ntuc.vendorservice.vendoraccountservice.servlet;

import com.google.gson.Gson;
import com.ntuc.vendorservice.foundationcontext.catalog.constants.RootURIPath;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAccountRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAdministratorRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorToAdminMappingRepositoryBean;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseEntryController;
import com.ntuc.vendorservice.vendoradminservice.entity.UserProfile;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorToAdminMapping;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountNotExistsException;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.AuthenticationResult;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.scimservice.proxy.SCIIdentityProxy;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
// .PersistenceException;
//import com.sap.security.um.user.UnsupportedUserAttributeException;
//import com.sap.security.um.user.User;
//import com.sap.cloud.security.xsuaa.http.XSUserInfoAdapter;
//import org.omg.CORBA.UnknownUserException;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = { "/vadmin" ,"/vadmin/profile"})
public class VendorPageDispatcherController extends BaseEntryController {
	private static final long serialVersionUID = 1L;
	@EJB
	protected VendorAccountRepositoryBean vendorAccountBean;
	@EJB
	protected VendorToAdminMappingRepositoryBean vendorToAdminMappingBean;
	@EJB
	protected VendorAdministratorRepositoryBean vendorAdministratorBean;
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String requestURI = request.getRequestURI(); 
		RootURIPath fromValue = RootURIPath.fromValue(requestURI);
		switch (fromValue) {
			case VENDORS_ADMIN:
			AuthenticationResult verifyAccountAccess = verifyAccountAccess(request);
			switch (verifyAccountAccess) {
				case ACCOUNT_PENDINGVALIDATION:  
				case ACCOUNT_VALIDATED: 
					dispatchToHomePage(request, response); 
					break;
				case ACCOUNT_NOT_FOUND:
				case PERSISTENCE_ERROR: 
				case ACCOUNT_INCTIVATED:
				default:
					dispatchToDefaultLandingPage(request, response);
					break;
			}
			break;
			case VENDORS_ADMIN_PROFILE:
				response.setContentType("application/json");
				Result result=new Result(); 
				try {
					result.setResultType(ResultType.SUCCESS);
					UserProfile userProfile = getUserProfile(request);
					result.setResult(userProfile);
					response.getWriter().print(new Gson().toJson(result));
				} catch (AccountNotExistsException e) {
					result.setResultType(ResultType.ERROR);
					result.setResult(e.getMessage());
				}
				break;
		default:
			dispatchToDefaultLandingPage(request, response);
			break;
		}  
	}

	protected void dispatchToHomePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String adminPage = "/vadmin.jsp";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(adminPage);
		dispatcher.forward(request, response);
	}

	@Override
	protected AuthenticationResult verifyAccountAccess(HttpServletRequest request) {
		try {
			//User userAttributes = getUserAttributes(request.getUserPrincipal());
			//String vendorAdminEmailAddress = userAttributes.getAttribute("email");
			List<VendorAccount> vendorAccounts = vendorAccountBean.findByVendorAdminEmail(resolveUserEmail(request));
			if (vendorAccounts == null||vendorAccounts.isEmpty()) {
				return AuthenticationResult.ACCOUNT_NOT_FOUND;
			} 
			String loggedUserId = SCIIdentityProxy.getLoggedUserId(getXsUserInfoAdapter(request));
			HashMap<SystemAccountStatus, List<VendorAccount>> result = getValidationMapping(loggedUserId,vendorAccounts);
			boolean hasActiveVendorCode=result.containsKey(SystemAccountStatus.ACTIVE);
			boolean hasInActiveVendorCode=result.containsKey(SystemAccountStatus.INACTIVE);
			boolean hasNewVendorCode=result.containsKey(SystemAccountStatus.NEW);
			if(hasNewVendorCode){
				validateNewlyCreatedAccount(loggedUserId, result);
			}
			AuthenticationResult  authenticationResult=AuthenticationResult.ACCOUNT_PENDINGVALIDATION;
			if((!hasInActiveVendorCode)&&(!hasNewVendorCode)){
				authenticationResult= AuthenticationResult.ACCOUNT_VALIDATED;
			}
			else if(hasInActiveVendorCode&&((!hasActiveVendorCode)&&(!hasNewVendorCode))){
				authenticationResult= AuthenticationResult.ACCOUNT_INCTIVATED;
			} 
			else if(hasNewVendorCode&&((!hasActiveVendorCode)&&(!hasInActiveVendorCode))){
				authenticationResult=AuthenticationResult.ACCOUNT_PENDINGVALIDATION;
			} 
			return authenticationResult;
		} catch (Exception e) {
			return AuthenticationResult.PERSISTENCE_ERROR;
		}
	}

	private HashMap<SystemAccountStatus, List<VendorAccount>> getValidationMapping(String loggedUserId, List<VendorAccount> vendorAccounts) {
		Iterator<VendorAccount> iterator = vendorAccounts.iterator();
		HashMap<SystemAccountStatus,List<VendorAccount>> result=new HashMap<SystemAccountStatus,List<VendorAccount>>();
		while(iterator.hasNext()){
			VendorAccount vendorAccount = iterator.next();
			if(vendorAccount.getSystemAccountStatus()==SystemAccountStatus.INACTIVE){
				if(!result.containsKey(SystemAccountStatus.INACTIVE)){
					result.put(SystemAccountStatus.INACTIVE,new ArrayList<VendorAccount>());
				} 
				result.get(SystemAccountStatus.INACTIVE).add(vendorAccount);
			}
			if(vendorAccount.getSystemAccountStatus()==SystemAccountStatus.ACTIVE){
				boolean containsKey = result.containsKey(SystemAccountStatus.ACTIVE);
				if(!containsKey){
					result.put(SystemAccountStatus.ACTIVE,new ArrayList<VendorAccount>());
				} 
				result.get(SystemAccountStatus.ACTIVE).add(vendorAccount);
			}
			if(vendorAccount.getSystemAccountStatus()==SystemAccountStatus.NEW){
				if(!result.containsKey(SystemAccountStatus.NEW)){
					result.put(SystemAccountStatus.NEW,new ArrayList<VendorAccount>());
				} 
				result.get(SystemAccountStatus.NEW).add(vendorAccount);
			}

		}
		return result;
	}

	@Override
	protected ResultType validateNewlyCreatedAccount(HttpServletRequest request) { 
		return ResultType.ERROR;
	} 
	private ResultType validateNewlyCreatedAccount(String loggedUserId,HashMap<SystemAccountStatus, List<VendorAccount>> validationMapping) {
		List<VendorAccount> list = validationMapping.get(SystemAccountStatus.NEW);
		for(VendorAccount vendorAccount:list){
			vendorAccount.setActivationDate(new Date());
			vendorAccount.setVendorActivatedDate(new Date());
			vendorAccount.setAccountValidated(true); 
			vendorAccount.setActivatedBy(loggedUserId);
			vendorAccount.setSystemAccountStatus(SystemAccountStatus.ACTIVE);
			vendorAccountBean.validateVendorAccount(vendorAccount);
		}
		return ResultType.SUCCESS;
	}
//migration update
	protected UserProfile getUserProfile(HttpServletRequest request) throws UnsupportedOperationException, AccountNotExistsException {
		/*User userAttributes = getUserAttributes(request.getUserPrincipal());
		String vendorAdminEmailAddress = userAttributes.getAttribute("email");
        */
		final VendorAccountAdministrator administrator = vendorAdministratorBean.findByVendorUserEmail(resolveUserEmail(request));
		final List<VendorToAdminMapping> adminMappings = vendorToAdminMappingBean.findByVendorAdministrator(administrator.getVendorAdminID());
		final UserProfile loggedUserProfile = SCIIdentityProxy.getLoggedUserProfile(getXsUserInfoAdapter(request), administrator);
		for(VendorToAdminMapping adminMapping:adminMappings){
			loggedUserProfile.getAccount().add(adminMapping.getVendorAccount());
		}
		return loggedUserProfile;
	}

	
	 
}
