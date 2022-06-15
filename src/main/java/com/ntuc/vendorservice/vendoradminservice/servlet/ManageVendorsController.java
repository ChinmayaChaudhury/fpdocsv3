package com.ntuc.vendorservice.vendoradminservice.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithObjectSet;
import com.ntuc.vendorservice.foundationcontext.catalog.model.VendorAccountModel;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import com.ntuc.vendorservice.notificationservice.services.AsyncVendorAccountActivationResend;
import com.ntuc.vendorservice.scimservice.exceptions.AccountUpdateSCIException;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.scimservice.models.SCIMUserGroup;
import com.ntuc.vendorservice.scimservice.utils.SCIUtils;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoraccountservice.repository.VendorUserRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.constants.ManageVendorsUrl;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorToAdminMapping;
import com.ntuc.vendorservice.vendoradminservice.exceptions.*;
import com.ntuc.vendorservice.vendoradminservice.repository.*;
import com.ntuc.vendorservice.vendoradminservice.services.ManageAccountServiceBean;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.ejb.EJB;
import javax.mail.MethodNotSupportedException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Servlet implementation class ManageVendor.java
 */ 
@WebServlet(description = "Manage FairPrice Vendor Account. This is a fairprice administrator role",
		urlPatterns = {"/fpadmin/fvendors/*","/fpuser/fpvendors/q" })
public class ManageVendorsController extends BaseController {
	private static final long serialVersionUID = 1L;
	 
	@EJB
	private VendorAccountRepositoryBean vendorAccountBean;
	@EJB
	private ManageAccountServiceBean manageAccountServiceBean;
	@EJB
	private VendorAdministratorRepositoryBean vendorAdministratorBean;
	@EJB
	private VendorUserRepositoryBean vendorUserBean;
	@EJB
	private VendorToAdminMappingRepositoryBean adminMappingBean;
	@EJB
	private FPUserToVendorSubscriptionRepositoryBean vendorSubscriptionBean;
	@EJB
	private FairPriceUserGroupRepositoryBean fairPriceUserGroupBean;
	@EJB
	private VendorUserGroupBean vendorUserGroupBean;
	@EJB
	private AsyncVendorAccountActivationResend  asyncAccountActivation;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		ManageVendorsUrl manageVendorsUrl = ManageVendorsUrl.fromValue(request.getRequestURI());
		switch(manageVendorsUrl){ 
		case VENDORS_QUERY: 
			ResultWithObjectSet withObjectSet=new ResultWithObjectSet();
			 URI uri;
				try {
					uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
					List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding()); 
					String email = ""; 
					String vendorName="",userName="";
					String vendorCode = "";
					Iterator<NameValuePair> nameValueIter = parse.iterator();
					while (nameValueIter.hasNext()) {
						NameValuePair nameValuePair = nameValueIter.next();
						if (nameValuePair.getName().equals("email")) {
							email = nameValuePair.getValue();
						}
						if (nameValuePair.getName().equals("vendorName")) {
							vendorName = nameValuePair.getValue();
						}
						if (nameValuePair.getName().equals("userName")) {
							userName = nameValuePair.getValue();
						}
						if (nameValuePair.getName().equals("vendorCode")) {
							vendorCode = nameValuePair.getValue();
						}
					}
					if (!StringUtils.isEmpty(email)) {
						manageAccountServiceBean.validateEmailDependency(email,true);
						String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(email).append("%22").toString();
						Result parseSCIMUserQuery = manageAccountServiceBean.parseSCIMUserQuery(queryString, request,SCIMUser.class);
						List<SCIMUser> scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
						withObjectSet.setResultType((!scimUsers.isEmpty()) ? ResultType.EXISTS : parseSCIMUserQuery.getResultType());
					}
					else if(!StringUtils.isEmpty(vendorName)){
						withObjectSet.setResultType(ResultType.SUCCESS);
						withObjectSet.setResult(vendorAccountBean.findByVendorName(vendorName));  
					}else if(!StringUtils.isEmpty(userName)){
						withObjectSet.setResultType((vendorAdministratorBean.findByVendorUserName(userName)!=null) ? ResultType.EXISTS : ResultType.SUCCESS);
					}else if(!StringUtils.isEmpty(vendorCode)){
						withObjectSet.setResultType((vendorAccountBean.findByVendorCode(vendorCode)!=null) ? ResultType.EXISTS : ResultType.SUCCESS);
					}else {
						withObjectSet.setResultType(ResultType.SUCCESS); 
						
						//Retrieve Vendor Admin details
						withObjectSet.setResult(vendorAdministratorBean.retrieveVendorAdmins());
						
						//Retrieve Groups Lookup for an Administrator
						withObjectSet.addValue("groupsLookup",vendorAccountBean.findGroupsByType("ADMIN"));
					}
					response.getWriter().print(new Gson().toJson(withObjectSet)); 
				} catch (URISyntaxException e ) { 
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				} catch (AccountExistsException e) {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					response.getWriter().print(new Gson().toJson(new Result(ResultType.EXISTS,e.getMessage())));
				}catch (AccountNotExistsException e) {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					response.getWriter().print(new Gson().toJson(new Result(ResultType.EXISTS,e.getMessage())));
				}
			break; 
		case VENDORS_SEND_EMAIL:
			ResultWithObjectSet result=new ResultWithObjectSet(); 
			try{
				String vendorID = getVendorID(request); 
				if(!StringUtils.isEmpty(vendorID)){ 
					asyncAccountActivation.dispatchVendorAccountActivationNotifications(Long.valueOf(vendorID));
					result.setResultType(ResultType.SUCCESS);
					result.setResult("Email sent successfully");
				}else{
					throw new URISyntaxException(ResultType.ERROR.name(),"Url format is wrong");
				}
				response.getWriter().print(new Gson().toJson(result)); 
			}catch(URISyntaxException e){
				response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
			}
			break;
		case VENDOR_ATTRIBUTE:
				result=new ResultWithObjectSet(); 
		     	try {
					String vendorID = getVendorID(request); 
					if(!StringUtils.isEmpty(vendorID)){
						result.setResultType(ResultType.SUCCESS);
						VendorToAdminMapping findByVendorAccount = adminMappingBean.findByVendorAccount(Long.valueOf(vendorID));
						VendorAccount vendorAccount;
						VendorAccountAdministrator vendorAdministrator = null;
						if(findByVendorAccount==null){
							vendorAccount=vendorAccountBean.findByID(Long.valueOf(vendorID));
							try {
								vendorAdministrator = vendorAdministratorBean.findByVendorUserEmail(vendorAccount.getVendorAdminEmail());
							} catch (AccountNotExistsException e) { 
								e.printStackTrace();
							}
							
						}else{
							vendorAccount= findByVendorAccount.getVendorAccount();
							vendorAdministrator = findByVendorAccount.getVendorAdministrator();
						}  
						result.setResult(vendorAdministrator); 
						
						//Retrieve list of Vendor Accounts associated with Vendor Admin
						List<VendorAccount> vendorAccounts = adminMappingBean.findAllVendorAccounts(vendorAdministrator, Long.valueOf(vendorID));
						VendorAccountModel vAccount = null;
						List<VendorAccountModel> accountList = new ArrayList<VendorAccountModel>();
						for (VendorAccount dsVendAccount : vendorAccounts) {
							if(dsVendAccount!=null){
								vAccount = new VendorAccountModel();
								vAccount.setVendorCode(dsVendAccount.getVendorCode());
								vAccount.setVendorName(dsVendAccount.getVendorName());
								vAccount.setVendorID(String.valueOf(dsVendAccount.getVendorID()));
								accountList.add(vAccount);
							}
						}
						result.addValue("vendorAccounts",accountList);
						
						if(vendorAdministrator!=null && vendorAdministrator.getVendorAdminID() > 0){
							//Retrieve Selected Groups
							result.addValue("selectedGroups", vendorUserBean.getAssignedGroupsToVendorAdmin(vendorAdministrator.getVendorAdminID()));
						}
						//Get Vendor Admin User details from SCI
						String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(vendorAdministrator.getVendorUserEmail()).append("%22").toString();
						Result sciResponse = manageAccountServiceBean.parseSCIMUserQuery(queryString, request,FPSCIUserAccount.class);
						if(sciResponse.getResultType()==ResultType.SUCCESS){
							result.addValue("sciDetails",sciResponse.getResult());
						}
						
						//QuotaAllocation quotaAllocation = quotaAllocationBean.findByVendorCategory(vendorAccount.getVendorCategory());
						//double vendorQuotaUtilization = directoryServiceBean.getVendorQuotaUtilization(vendorAccount,quotaAllocation);
						//result.addValue("qu", vendorQuotaUtilization);
					}
					else {
						throw new URISyntaxException(ResultType.ERROR.name(),"Url format is wrong");
					}
					response.getWriter().print(new Gson().toJson(result)); 
				} catch (URISyntaxException e ) { 
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				}  catch (AccountNotExistsException e) {
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				}
			
			break;
		/*case FPUSER_VENDORS_QUERY:
			List<VendorAccount> vendorAccounts= new ArrayList<>();
			try {
				long fairPriceUserID = getFairPriceUser(request).getFairPriceUserID();
				List<FPUserToVendorSubscription> vendorSubscriptions = vendorSubscriptionBean.findByFairPriceUserID(fairPriceUserID);
				for(FPUserToVendorSubscription subscription:vendorSubscriptions){
					vendorAccounts.add(subscription.getVendorID());
				}
				result = new ResultWithObjectSet(ResultType.SUCCESS, vendorAccounts);
			} catch (PersistenceException | UnsupportedUserAttributeException e) {
				result=new ResultWithObjectSet(ResultType.SUCCESS,vendorAccounts); 
			} 
			response.getWriter().print(new Gson().toJson(result));
			break; */
		case UNKNOWN:
			handleUnknownRequests(response);
			break;
		default:
			methodNotImplemented(response);
			break;
		
		}
	}

	private String getVendorID(HttpServletRequest request) throws URISyntaxException {
		URI uri;
		uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
		List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding());  
		String vendorID="";
		Iterator<NameValuePair> nameValueIter = parse.iterator();
		while (nameValueIter.hasNext()) {
			NameValuePair nameValuePair = nameValueIter.next(); 
			if (nameValuePair.getName().equals("vendorID")) {
				vendorID = nameValuePair.getValue();
			}
		}
		return vendorID;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException { 
		handleRequest(request, response);
		
	}

	@Override
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		ManageVendorsUrl manageVendorsUrl = ManageVendorsUrl.fromValue(request.getRequestURI());
		Result result=new Result();
		String postedJsonData = null;
		switch (manageVendorsUrl) {
		case ALLOW_VENDORADMIN_DOCUMENTSHARE:
			try { 
				postedJsonData = getPostedJsonData(request); 
				JsonObject jsonObject = new JsonParser().parse(postedJsonData).getAsJsonObject();
				long vendorID = jsonObject.get("vendorID").getAsLong();
				boolean allowDocumentShare = jsonObject.get("allowDocumentShare").getAsBoolean(); 
				VendorToAdminMapping findByVendorAccount = adminMappingBean.findByVendorAccount(vendorID);
				VendorAccountAdministrator vendorAdministrator = findByVendorAccount.getVendorAdministrator();
				vendorAdministrator.setAllowDocumentShare(allowDocumentShare);
				vendorAdministratorBean.update(vendorAdministrator);
				String endUserGroup = request.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsEndUser.name());
				String sciAccountID = vendorAdministrator.getSciAccountID();
				result = manageAccountServiceBean.searchSAPSCIAccountDetails(sciAccountID,request); 
				if(result.getResultType()==ResultType.SUCCESS){ 
					if(allowDocumentShare){ 
						SCIMUser scimUser=(SCIMUser) result.getResult();
						result = manageAccountServiceBean.addUserToSCIGroup(scimUser,SCIMUserGroup.VendorDocumentsEndUser, request);
						VendorUser vendorUser = SCIUtils.getVendorUser(vendorAdministrator, findByVendorAccount.getVendorAccount());
						vendorUser.getVendorUserGroups().addAll(vendorUserGroupBean.findAll());
						//XXX
						manageAccountServiceBean.createAccount(vendorUser, MailSenderUtil.getDocumentsTargetUrl(EmailNotificationDirection.VADMIN_2_VUSER, getBaseUrl(request)));
					} else{ 
						SCIMGroup scimGroup=new SCIMGroup(endUserGroup);
						Set<SCIMGroup>  scimGroups=new HashSet<SCIMGroup>();
						scimGroups.add(scimGroup);
						result = manageAccountServiceBean.removeFromGroupSAPSCIAccount(sciAccountID,scimGroups, request);
						VendorUser vendorUser = vendorUserBean.findByVendorUserEmail(vendorAdministrator.getEmailAddress());
						manageAccountServiceBean.deleteAccount(vendorUser);
					}
				}else{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
				}
			}catch(RequestDataException e){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			} catch (AccountManagementException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage()); 
			}  
			response.getWriter().print(new Gson().toJson(result));
			break;
		
		case VENDOR_DELETE:
			try { 
				postedJsonData = getPostedJsonData(request); 
				VendorAccount vendorAccount = new Gson().fromJson(postedJsonData, VendorAccount.class); 
				String vendorAdminGroup =request.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsAdministrator.name());
				SCIMGroup scimGroup = new SCIMGroup(vendorAdminGroup);
				result=dropAdministrationForVendorAccount(vendorAccount,scimGroup, request);  
				if(result.getResultType()==ResultType.SUCCESS){
					result=manageAccountServiceBean.deleteAccount(vendorAccount);
				}else{
				  if ( result.getResult() instanceof KeyVal) {
					  Integer statusCode = Integer.valueOf(((KeyVal) result.getResult()).getKey());
					  if(statusCode==HttpServletResponse.SC_NOT_FOUND){
						  result=manageAccountServiceBean.deleteAccount(vendorAccount);
					  }
				  }else{
						response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				  }
				}
			}catch(RequestDataException e){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			}  
			response.getWriter().print(new Gson().toJson(result)); 
			break;
		case VENDOR_CREATE: 
			try { 
				postedJsonData = getPostedJsonData(request); 
				FPSCIUserAccount userAccount=new Gson().fromJson(postedJsonData, FPSCIUserAccount.class);
				userAccount.setSendEmail(Boolean.FALSE);
				manageAccountServiceBean.validateEmailDependency(userAccount.getEmail(),true); 
				result=manageAccountServiceBean.initializeUserForSCIAccountNDOCShare(getXsUserInfoAdapter(request),userAccount, SCIMUserGroup.VendorDocumentsAdministrator, request, getBaseUrl(request),false);
			} catch (RequestDataException | AccountDetailsException | MethodNotSupportedException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			} catch (AccountExistsException|AccountNotExistsException e) {  
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				result=new Result(ResultType.EXISTS,e.getMessage());  
			}
			catch ( AccountManagementException | AccountUpdateSCIException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result=new Result(ResultType.ERROR,e.getMessage());
			} /*catch (SCIServiceException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result=new Result(ResultType.ERROR,e.getMessage());
			}*/
			response.getWriter().print(new Gson().toJson(result));
			break;
		case VENDOR_UPDATE:
			try {
			 postedJsonData = getPostedJsonData(request); 
			 FPSCIUserAccount userAccount=new Gson().fromJson(postedJsonData, FPSCIUserAccount.class); 
			 result = manageAccountServiceBean.updateVendorAdminAccount(getXsUserInfoAdapter(request),userAccount,SCIMUserGroup.VendorDocumentsAdministrator,request,getBaseUrl(request));
			} catch (RequestDataException e ) {
				handleDataErrorRequests(response);
			}
			response.getWriter().print(new Gson().toJson(result));
			break;
		default:
			handleUnknownRequests(response);
			break; 
		
		}
	} 
	/**
	 * If vendor manages more than one vendor code, keep the administration rights in HCP_SCI else drop the administration
	 * @param currentVendorAccount
	 * @param scimGroup
	 * @param request
	 * @return
	 */
	private Result dropAdministrationForVendorAccount(VendorAccount currentVendorAccount, SCIMGroup scimGroup, HttpServletRequest request) {
		Set<SCIMGroup>  scimGroups=new HashSet<SCIMGroup>();
		scimGroups.add(scimGroup);
		 
		Result result=new Result(); 
			/**
			 * Ignore exising vendor administrator managing more than one account
			 */
			List<VendorAccount> accounts = vendorAccountBean.findByVendorAdminEmail(currentVendorAccount.getVendorAdminEmail());
			VendorUser administratorUser = vendorUserBean.findByVendorUserEmail(currentVendorAccount.getVendorAdminEmail());
			List<VendorUser> vendorUsers = vendorUserBean.findAllByVendorID(currentVendorAccount.getVendorID());
			for(VendorUser vendorUser: vendorUsers){
				if(accounts.size()>1&&administratorUser!=null){
					if(vendorUser.getVendorUserID()== administratorUser.getVendorUserID()){
						continue;
					}
				}
				vendorUserBean.delete(vendorUser);	
				dropVendorUserAccount(vendorUser, request);
			}  
			if(accounts.size()==1){
				VendorToAdminMapping vendorToAdminMapping = adminMappingBean.findByVendorAccount(currentVendorAccount.getVendorID());
				if(vendorToAdminMapping!=null){
					VendorAccountAdministrator vendorAdministrator = vendorToAdminMapping.getVendorAdministrator();
					adminMappingBean.delete(vendorToAdminMapping);
					vendorAdministratorBean.delete(vendorAdministrator); 
				}
				else{
					try {
						VendorAccountAdministrator vendorAdministrator = vendorAdministratorBean.findByVendorUserEmail(currentVendorAccount.getVendorAdminEmail());
						vendorAdministratorBean.delete(vendorAdministrator);
					} catch (AccountNotExistsException e) { 
						e.printStackTrace();
					}
				}
				result=manageAccountServiceBean.removeFromGroupSAPSCIAccount(currentVendorAccount.getSciAccountID(),scimGroups, request); 
			}else{
				VendorToAdminMapping vendorToAdminMapping = adminMappingBean.findByVendorAccount(currentVendorAccount.getVendorID());
				adminMappingBean.delete(vendorToAdminMapping);
				result=new Result(ResultType.SUCCESS, "relationship dropped");
			}  
		 
		return result;
	} 
	
	private Result dropVendorUserAccount(VendorUser vendorUser,  HttpServletRequest request) {  
		Set<SCIMGroup>  scimGroups=new HashSet<SCIMGroup>();
		String vendorUserGroup =
		        request.getSession()
		               .getServletContext()
		               .getInitParameter(SCIMUserGroup.VendorDocumentsEndUser.name());
		scimGroups.add( new SCIMGroup(vendorUserGroup));
		return manageAccountServiceBean.removeFromGroupSAPSCIAccount(vendorUser.getSciAccountID(),scimGroups, request); 
	} 
	
	/*protected FairPriceUser getFairPriceUser(HttpServletRequest request) throws PersistenceException, UnsupportedUserAttributeException {
	    User userAttributes = getUserAttributes(request.getUserPrincipal());
		String email = userAttributes.getAttribute("email");
		return  fairPriceUserGroupBean.findByUserEmail(email); 
	}*/
	 
}
