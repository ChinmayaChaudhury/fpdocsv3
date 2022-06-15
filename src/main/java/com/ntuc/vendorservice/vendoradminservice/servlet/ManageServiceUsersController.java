package com.ntuc.vendorservice.vendoradminservice.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.mail.MethodNotSupportedException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import com.ntuc.vendorservice.scimservice.models.SCIMUserGroup;
import com.ntuc.vendorservice.vendoraccountservice.repository.VendorUserRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.vendoradminservice.exceptions.*;
import com.ntuc.vendorservice.vendoradminservice.repository.FPUserToVendorSubscriptionRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.FairPriceUserGroupRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.services.ManageAccountServiceBean;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URLEncodedUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ntuc.vendorservice.vendoradminservice.constants.ManageInternalUsersUrl;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.scimservice.exceptions.AccountUpdateSCIException;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithObjectSet;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoradminservice.entity.FPUserToVendorSubscription;
/*import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.User;*/

/**
 * Servlet implementation class UserManagementController
 */
@WebServlet(name = "ManageUsersController", urlPatterns = { "/fpadmin/fuser/*"})
public class ManageServiceUsersController extends BaseController {
	private static final long serialVersionUID = 1L;
	@EJB
	protected FairPriceUserGroupRepositoryBean fairPriceUserGroupBean;
	@EJB
	protected FPUserToVendorSubscriptionRepositoryBean subscriptionBean;
	@EJB
	protected VendorUserRepositoryBean vendorUserBean;
	@EJB
	protected ManageAccountServiceBean manageAccountServiceBean;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		ManageInternalUsersUrl manageInternalUsersUrl = ManageInternalUsersUrl.fromValue(request.getRequestURI());
		switch (manageInternalUsersUrl) {
		case INTERNAL_USER_VENDOR_REL: 
			ResultWithObjectSet resultWithObjectSet=new ResultWithObjectSet();
			URI uri;
			try{
				uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
				List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding()); 
				String action = ""; 
				Iterator<NameValuePair> nameValueIter = parse.iterator();
				while (nameValueIter.hasNext()) {
					NameValuePair nameValuePair = nameValueIter.next();
					if (nameValuePair.getName().equals("action")) {
						action = nameValuePair.getValue();
					}
				}
				if(StringUtils.isEmpty(action)){
					throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST, "Action Parameter not found url format"); 
				}
				JsonObject parsedJson = getParsedJson(getPostedJsonData(request));
				UserVendorRelAction vendorRelAction = UserVendorRelAction.valueOf(action);  
				if(vendorRelAction== UserVendorRelAction.SUBSCRIPTIONS){
					List<FPUserToVendorSubscription> vendorSubscriptions = subscriptionBean.findByFairPriceUserID(parsedJson.get("fairPriceUserID").getAsLong());
					resultWithObjectSet.setResult(vendorSubscriptions);
					resultWithObjectSet.setResultType(ResultType.SUCCESS);
				} else if(vendorRelAction== UserVendorRelAction.REMOVE){
						FPUserToVendorSubscription vendorSubscription = new Gson().fromJson(parsedJson, FPUserToVendorSubscription.class);
						resultWithObjectSet = subscriptionBean.delete(vendorSubscription); 
				}
				else if(vendorRelAction== UserVendorRelAction.ADD){
						boolean hasUser= parsedJson.has("user");
						boolean hasVendors= parsedJson.has("vendors");
						if(!hasUser||!hasVendors){
							throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST, "Integrity failed"); 
						}  
						JsonObject jsonObject = parsedJson.get("user").getAsJsonObject();
						InternalUser fairPriceUser = new Gson().fromJson(jsonObject, InternalUser.class);
						List<VendorAccount> vendorAccounts=new ArrayList<VendorAccount>();
						JsonArray jsonArray = parsedJson.get("vendors").getAsJsonArray();
						Iterator<JsonElement> iterator = jsonArray.iterator(); 
						while(iterator.hasNext()){
							JsonElement jsonElement = iterator.next();	
							vendorAccounts.add(new Gson().fromJson(jsonElement, VendorAccount.class));
						} 
						boolean hasUpdateException=false;
						List<FPUserToVendorSubscription> success= new ArrayList<FPUserToVendorSubscription>();
						List<FPUserToVendorSubscription> failed= new ArrayList<FPUserToVendorSubscription>(); 
						for(VendorAccount vendorAccount:vendorAccounts){
							FPUserToVendorSubscription vendorSubscription=new FPUserToVendorSubscription();
							vendorSubscription.setVendorID(vendorAccount);
							vendorSubscription.setFairPriceUserID(fairPriceUser);
							try{
								FPUserToVendorSubscription subscription = subscriptionBean.add(vendorSubscription); 
								success.add(subscription);
							}catch(AccountManagementException e ){
								hasUpdateException=true;
								failed.add(vendorSubscription);
							} 
						}  
						success=subscriptionBean.findByFairPriceUserID(fairPriceUser.getFairPriceUserID());
						if(hasUpdateException){
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							resultWithObjectSet.addValue("failed", failed); 
							resultWithObjectSet.setResult(success); 
							resultWithObjectSet.setResultType(ResultType.PARTIAL);
						}else{ 
							resultWithObjectSet.setResult(success);
							resultWithObjectSet.setResultType(ResultType.SUCCESS);
						} 
				}
				response.getWriter().print(new Gson().toJson(resultWithObjectSet));
			 } catch (Exception e ) {  
				 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				 response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
			 } 
				  
			break;
 
		case INTERNAL_USERS_QUERY:
			Result result = new Result();
				try {
					uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
					List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding()); 
					String email = ""; 
					Iterator<NameValuePair> nameValueIter = parse.iterator();
					while (nameValueIter.hasNext()) {
						NameValuePair nameValuePair = nameValueIter.next();
						if (nameValuePair.getName().equals("email")) {
							email = nameValuePair.getValue();
						}
					}
					if (!StringUtils.isEmpty(email)) {
						manageAccountServiceBean.validateEmailDependency(email,false); 
						String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(email).append("%22").toString();
						result = manageAccountServiceBean.parseSCIMUserQuery(queryString, request,FPSCIUserAccount.class);
					} else {
						result.setResultType(ResultType.SUCCESS);
						result.setResult(fairPriceUserGroupBean.findAllInternalUser()); 
					}
					response.getWriter().print(new Gson().toJson(result)); 
				} catch (URISyntaxException e ) { 
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				} catch (AccountExistsException e) { 
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					response.getWriter().print(new Gson().toJson(new Result(ResultType.EXISTS,e.getMessage())));
				}
			break;
		case INTERNAL_USER_DELETE:
			try { 
				
				String postedJsonData = getPostedJsonData(request); 
				InternalUser fairPriceUser = new Gson().fromJson(postedJsonData, InternalUser.class);
				validateVendorRelationship(fairPriceUser); 
				Set<SCIMGroup>  scimGroups=new HashSet<SCIMGroup>();
				String authGroup =request.getSession().getServletContext().getInitParameter(SCIMUserGroup.InternalDocumentsEndUser.name());
				scimGroups.add( new SCIMGroup(authGroup));
				result=manageAccountServiceBean.removeFromGroupSAPSCIAccount(fairPriceUser.getSciAccountID(),scimGroups, request);
				if(result.getResultType()==ResultType.SUCCESS){
						result=manageAccountServiceBean.deleteAccount(fairPriceUser);
				}else{
				  if ( result.getResult() instanceof KeyVal) {
					  Integer stausCode = Integer.valueOf(((KeyVal) result.getResult()).getKey());
					  if(stausCode==HttpServletResponse.SC_NOT_FOUND){
						  result=manageAccountServiceBean.deleteAccount(fairPriceUser);
					  }
				  }else{
						response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				  }
				} 
			}catch(RequestDataException| AccountManagementException e){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			}  
			response.getWriter().print(new Gson().toJson(result));
			break;
		case INTERNAL_USER_CREATE:  
			String postedJsonData;
			try { 
				postedJsonData = getPostedJsonData(request); 
				FPSCIUserAccount userAccount=new Gson().fromJson(postedJsonData, FPSCIUserAccount.class); 
				result=manageAccountServiceBean.initializeUserForSCIAccountNDOCShare(getXsUserInfoAdapter(request),userAccount, SCIMUserGroup.InternalDocumentsEndUser, request, getBaseUrl(request),false);
				 
			} catch (RequestDataException | AccountDetailsException | MethodNotSupportedException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			} catch (AccountExistsException|AccountNotExistsException e) {  
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				result=new Result(ResultType.EXISTS,e.getMessage());  
			}
			catch ( AccountManagementException
					| AccountUpdateSCIException  e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result=new Result(ResultType.ERROR,e.getMessage());
			} //catch (SCIServiceException e) {
				//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				//result=new Result(ResultType.ERROR,e.getMessage());
			//}
			response.getWriter().print(new Gson().toJson(result));
			break;
		case INTERNAL_USER_UPDATE:
			try{
			  postedJsonData = getPostedJsonData(request); 
			 InternalUser fairPriceUser = new Gson().fromJson(postedJsonData, InternalUser.class);
			 Result updateResult = manageAccountServiceBean.updateInternalUser(fairPriceUser); 
			 if(updateResult.getResultType()==ResultType.SUCCESS){
				 updateResult.setResult(fairPriceUserGroupBean.findAllInternalUser());  
			 }else{
				 response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
			 } 
			 response.getWriter().print(new Gson().toJson(updateResult));
			} catch (RequestDataException e) {
				handleDataErrorRequests(response);
			}
			break;
		case UNKNOWN: 
		default:
			handleUnknownRequests(response);
			break;

		}

	}
	private void validateVendorRelationship(InternalUser fairPriceUser) throws AccountManagementException {
		List<FPUserToVendorSubscription> subscriptions = subscriptionBean.findByFairPriceUserID(fairPriceUser.getFairPriceUserID());
		if(!subscriptions.isEmpty()){
			throw new AccountManagementException("Kindly drop vendor relations in the account, click on edit, select 'vendors' tab and drop all the entries from the list'");
		}
	}
	public enum UserVendorRelAction{
		ADD,REMOVE, SUBSCRIPTIONS
	}
	
	protected VendorUser getVendorUser(HttpServletRequest request)  {
	  /*  User userAttributes = getUserAttributes(request.getUserPrincipal());
		String email = userAttributes.getAttribute("email");*/
		return  vendorUserBean.findByVendorUserEmail(resolveUserEmail(request));
	}

}
