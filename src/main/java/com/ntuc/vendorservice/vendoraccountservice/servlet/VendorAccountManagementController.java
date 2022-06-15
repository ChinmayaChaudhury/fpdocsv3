package com.ntuc.vendorservice.vendoraccountservice.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithKeySet;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithObjectSet;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.scimservice.exceptions.AccountUpdateSCIException;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.scimservice.models.SCIMUserGroup;
import com.ntuc.vendorservice.scimservice.services.ManageSCIMGroupServiceBean;
import com.ntuc.vendorservice.scimservice.utils.SCIUtils;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoraccountservice.repository.VendorUserRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.constants.ManageVendorAccountUrl;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorUserGroup;
import com.ntuc.vendorservice.vendoradminservice.exceptions.*;
import com.ntuc.vendorservice.vendoradminservice.models.VendorCategory;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAccountRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAdministratorRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorUserGroupBean;
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
 *
 * @author I305675
 *
 */
@WebServlet(urlPatterns={"/vadmin/vendorAccount/*"})
public class VendorAccountManagementController extends BaseController {
	private static final long serialVersionUID = 1L;
	@EJB
	protected VendorAccountRepositoryBean vendorAccountBean;
	@EJB
	protected VendorUserGroupBean vendorUserGroupBean;
	@EJB
	protected VendorAdministratorRepositoryBean administratorBean;
	@EJB
	protected VendorUserRepositoryBean vendorUserBean;
	@EJB
	protected ManageAccountServiceBean manageAccountServiceBean;
	@EJB
	protected ManageSCIMGroupServiceBean manageSCIMGroupServiceBean;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		List<VendorUser> vendorUsers= new ArrayList<>();
		Result result;
		switch (ManageVendorAccountUrl.fromValue(httpServletRequest.getRequestURI())){
			case VENDORACCOUNT_QUERY:
				ResultWithKeySet resultWithKeySet = new ResultWithKeySet();
				List<VendorAccount>vendorAccounts = getVendorAccount(httpServletRequest);
				if(vendorAccounts!=null){
					resultWithKeySet.setResult(vendorAccounts);
					resultWithKeySet.setResultType(ResultType.SUCCESS);
					resultWithKeySet.addKeyValSet("vendorCategory",  VendorCategory.toKeyValFormat());
				}else{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resultWithKeySet.setResult("Account not found");
					resultWithKeySet.setResultType(ResultType.ERROR);
				}
				response.getWriter().print(new Gson().toJson(resultWithKeySet));
				break;
			case VENDORACCOUNT_GROUPS:
				List<VendorUserGroup> vendorUserGroups = vendorUserGroupBean.findAll();
				if (vendorUserGroups == null) {
					vendorUserGroups = new ArrayList<VendorUserGroup>();
				}
				resultWithKeySet = new ResultWithKeySet(ResultType.SUCCESS, vendorUserGroups);
				response.getWriter().print(new Gson().toJson(resultWithKeySet));
				break;
			case VENDORACCOUNT_UPDATE:
				try{
					String postedJsonData  = getPostedJsonData(httpServletRequest);
					VendorAccount vendorAccount = new Gson().fromJson(postedJsonData, VendorAccount.class);
					resultWithKeySet = new ResultWithKeySet();
					try { 	resultWithKeySet.setResultType(ResultType.SUCCESS);
						resultWithKeySet.setResult(vendorAccountBean.update(vendorAccount));
						resultWithKeySet.addKeyValSet("vendorCategory",  VendorCategory.toKeyValFormat());
					} catch (AccountManagementException e) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						resultWithKeySet.setResultType(ResultType.ERROR);
						resultWithKeySet.setResult(e.getMessage());
					}
					response.getWriter().print(new Gson().toJson(resultWithKeySet));
				}catch(RequestDataException e){
					handleDataErrorRequests(response);
				}
				break;

			case VENDORUSER_DELETE:
				try {
					String postedJsonData = getPostedJsonData(httpServletRequest);
					VendorUser vendorUser = new Gson().fromJson(postedJsonData, VendorUser.class);
					Set<SCIMGroup>  scimGroups=new HashSet<SCIMGroup>();
					String vendorUserGroup =httpServletRequest.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsEndUser.name());
					scimGroups.add(new SCIMGroup(vendorUserGroup));
					result=manageAccountServiceBean.removeFromGroupSAPSCIAccount(vendorUser.getSciAccountID(),scimGroups, httpServletRequest);
					if(result.getResultType()==ResultType.SUCCESS){
						result=manageAccountServiceBean.deleteAccount(vendorUser);
					}else{
						if (result.getResult() instanceof KeyVal) {
							Integer statusCode = Integer.valueOf(((KeyVal) result.getResult()).getKey());
							if(statusCode==HttpServletResponse.SC_NOT_FOUND){
								result=manageAccountServiceBean.deleteAccount(vendorUser);
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
			case VENDORUSER_CREATE:
				try {
					String postedJsonData = getPostedJsonData(httpServletRequest);
					FPSCIUserAccount fpsciUserAccount=new Gson().fromJson(postedJsonData, FPSCIUserAccount.class);
					fpsciUserAccount.setSendEmail(Boolean.FALSE);
					final String baseUrl = getBaseUrl(httpServletRequest);
					result = manageAccountServiceBean.initializeUserForSCIAccountNDOCShare(getXsUserInfoAdapter(httpServletRequest), fpsciUserAccount, SCIMUserGroup.VendorDocumentsEndUser, httpServletRequest, baseUrl,false);
				} catch (RequestDataException | AccountDetailsException | MethodNotSupportedException e) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					result=new Result(ResultType.ERROR,e.getMessage());
				} catch (AccountExistsException | AccountNotExistsException e) {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					result=new Result(ResultType.EXISTS,e.getMessage());
				} catch ( AccountManagementException | AccountUpdateSCIException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					result=new Result(ResultType.ERROR,e.getMessage());
				} /*catch (SCIServiceException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					result=new Result(ResultType.ERROR,e.getMessage());
				}*/
				response.getWriter().print(new Gson().toJson(result));
				break;
			case VENDORUSER_UPDATE:
				try{
					String postedJsonData  = getPostedJsonData(httpServletRequest);
					FPSCIUserAccount userAccount=new Gson().fromJson(postedJsonData, FPSCIUserAccount.class);
					result = manageAccountServiceBean.updateVendorUserAccount(userAccount,SCIMUserGroup.VendorDocumentsEndUser,httpServletRequest,getBaseUrl(httpServletRequest));
					response.getWriter().print(new Gson().toJson(result));
				}catch(RequestDataException e){
					handleDataErrorRequests(response);
				}
				break;
			case VENDORUSERS_QUERY:
				vendorAccounts=getVendorAccount(httpServletRequest);
				if(httpServletRequest.getQueryString()!=null){
					URI uri;
					try {
						uri = new URI(httpServletRequest.getRequestURL().append("?").append(httpServletRequest.getQueryString()).toString());
						List<NameValuePair> parse = URLEncodedUtils.parse(uri, httpServletRequest.getCharacterEncoding());
						String vendorGroupID = "";
						String vendorUserID = "";
						String emailId = "";
						Iterator<NameValuePair> nameValueIter = parse.iterator();
						while (nameValueIter.hasNext()) {
							NameValuePair nameValuePair = nameValueIter.next();
							if (nameValuePair.getName().equals("vendorGroupID")) {
								vendorGroupID = nameValuePair.getValue();
							}
							if(nameValuePair.getName().equals("vendorUserID")){
								vendorUserID = nameValuePair.getValue();
							}
							if(nameValuePair.getName().equals("emailId")){
								emailId  = nameValuePair.getValue();
							}
						}
						//XXX Revisit the source for optimization
						if (!StringUtils.isEmpty(vendorGroupID)) {
							vendorUsers =new ArrayList<VendorUser>();
							for(VendorAccount vendorAccount:vendorAccounts ){
								long vendorID = vendorAccount.getVendorID();
								List<VendorUser> dsVendorUsers = vendorUserBean.findAllByVendorIDAndVendorUserGroup(vendorID, Long.valueOf(vendorGroupID));
								for(VendorUser vendorUser:dsVendorUsers ){
									vendorUser.setVendorUserGroupID(vendorUser.getVendorUserGroups());
									vendorUsers.add(vendorUser);
								}
							}
							response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS,vendorUsers)));
						}
						else if (!StringUtils.isEmpty(vendorUserID)) {
							VendorUser vendorUser =  vendorUserBean.findByUserID(Long.valueOf(vendorUserID));

							//Retrieve All the Group Assignment against the Vendor User
							ResultWithObjectSet userResponse = new ResultWithObjectSet();
							userResponse.addValue("authorizations", vendorUserBean.getAssignedGroupsToVendor(Long.valueOf(vendorUserID)));

							//SCI Details Retrieval
							manageAccountServiceBean.validateEmailDependency(vendorUser.getEmailAddress(),true);
							String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(vendorUser.getEmailAddress()).append("%22").toString();
							Result sciResponse = manageAccountServiceBean.parseSCIMUserQuery(queryString, httpServletRequest,FPSCIUserAccount.class);
							userResponse.addValue("sciDetails", sciResponse.getResult());
							response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS,userResponse)));
						}
						else if (!StringUtils.isEmpty(emailId)) {
							manageAccountServiceBean.validateEmailDependency(emailId,true);
							String queryString=new StringBuilder().append("filter").append("=").append("emails").append("%20eq%20").append("%22").append(emailId).append("%22").toString();
							Result parseSCIMUserQuery = manageAccountServiceBean.parseSCIMUserQuery(queryString, httpServletRequest, SCIMUser.class);
							List<SCIMUser> scimUsers = (List<SCIMUser>) parseSCIMUserQuery.getResult();
							boolean isActive = true;
							for (SCIMUser scimUser : scimUsers) {
								isActive = scimUser.getActive();
							}
							response.getWriter().print(new Gson().toJson(new Result((!scimUsers.isEmpty() && isActive) ? ResultType.EXISTS : parseSCIMUserQuery.getResultType(),scimUsers)));
						}

					} catch (AccountExistsException e) {
						response.setStatus(HttpServletResponse.SC_CONFLICT);
						response.getWriter().print(new Gson().toJson(new Result(ResultType.EXISTS,e.getMessage())));
					}catch (URISyntaxException e ) {
						response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
					}
				}else{
					//XXX Revisit the source for optimization
					try {
						vendorUsers = new ArrayList<>();
						for(VendorAccount vendorAccount:vendorAccounts ){
							List<VendorUser> findAllByVendorID = vendorUserBean.findAllByVendorAccount(vendorAccount);
							for(VendorUser vendorUser:findAllByVendorID ){
								vendorUser.setVendorUserGroupID(vendorUser.getVendorUserGroups());
								vendorUsers.add(vendorUser);
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS,vendorUsers)));
				}
				break;
			case VENDORUSER_ASSIGN_GROUP:
				try {
					JsonObject jsonObject =  JsonParser.parseString(getPostedJsonData(httpServletRequest)).getAsJsonObject();
					JsonElement collection = jsonObject.get("collection");
					JsonElement vendorUser=jsonObject.get("vendorUser");
					List<CWFApplicationGroup> applicationGroups= SCIUtils.getCWFApplicationGroups(collection);
					VendorUser dsVendorUser = new Gson().fromJson(vendorUser, VendorUser.class);
					result = new Result(ResultType.SUCCESS,manageSCIMGroupServiceBean.provisionVendorUser(dsVendorUser, applicationGroups, httpServletRequest));
				} catch (RequestDataException | AccountManagementException e) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					result=new Result(ResultType.ERROR,e.getMessage());
				}
				response.getWriter().print(new Gson().toJson(result));

				break;

			case VENDORUSER_DROP_GROUP:
				try {
					JsonObject jsonObject =  JsonParser.parseString(getPostedJsonData(httpServletRequest)).getAsJsonObject();
					JsonElement collection = jsonObject.get("collection");
					JsonElement vendorUser=jsonObject.get("vendorUser");
					List<CWFApplicationGroup> applicationGroups=SCIUtils.getCWFApplicationGroups(collection);
					VendorUser dsVendorUser = new Gson().fromJson(vendorUser, VendorUser.class);
					result = new Result(ResultType.SUCCESS,manageSCIMGroupServiceBean.removeGroupProvisioningOnVendorUser(dsVendorUser, applicationGroups, httpServletRequest));
				} catch (RequestDataException | AccountManagementException e) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					result=new Result(ResultType.ERROR,e.getMessage());
				}
				response.getWriter().print(new Gson().toJson(result));
				break;
			case VENDORUSER_ROLES:
				ResultWithObjectSet permissionResponse = new ResultWithObjectSet();
				permissionResponse.setResultType(ResultType.SUCCESS);
				Long vendorAdminId = getVendorAdminAccount(httpServletRequest);
				String authGroup =httpServletRequest.getSession().getServletContext().getInitParameter(SCIMUserGroup.VendorDocumentsAdministrator.name());
				permissionResponse.addValue("groups",  vendorAccountBean.getAdminGroupAssignments(vendorAdminId,authGroup));

				if(vendorAdminId!=null){
					permissionResponse.addValue("vendorList",  vendorAccountBean.getDistinctVendors(vendorAdminId));
				}

				response.getWriter().print(new Gson().toJson(permissionResponse));
				break;
			case UNKNOWN:
			default:
				handleUnknownRequests(response);
				break;

		}
	}

	private List<VendorUser> loadVendorUsers(HttpServletRequest request) {
		List<VendorUser> vendorUsers;
		List<VendorAccount> vendorAccounts;
		vendorAccounts=getVendorAccount(request);
		vendorUsers =new ArrayList<VendorUser>();
		for(VendorAccount vendorAccount:vendorAccounts ){
			List<VendorUser> findAllByVendorID = vendorUserBean.findAllByVendorAccount(vendorAccount);
			for(VendorUser updatedVendorUser:findAllByVendorID ){
				updatedVendorUser.setVendorUserGroupID(updatedVendorUser.getVendorUserGroups());
				vendorUsers.add(updatedVendorUser);
			}
		}
		return vendorUsers;
	}

	protected List<VendorAccount>getVendorAccount(HttpServletRequest request) {
		try {
	        /*UserProvider userProvider = UserManagementAccessor.getUserProvider();
	        User userAttributes = userProvider.getUser(request.getUserPrincipal().getName());
			String vendorAdminEmailAddress = userAttributes.getAttribute("email");*/

			List<VendorAccount> vendorAccounts = vendorAccountBean.findByVendorAdminEmail(resolveUserEmail(request));
			return vendorAccounts;
		} catch (Exception e) {
			return null;
		}
	}

	protected Long getVendorAdminAccount(HttpServletRequest request) {
		try {
			/*UserProvider userProvider = UserManagementAccessor.getUserProvider();
	        User userAttributes = userProvider.getUser(request.getUserPrincipal().getName());
			String vendorAdminEmailAddress = userAttributes.getAttribute("email");*/

			VendorAccountAdministrator vendAdmin = administratorBean.findByVendorUserEmail(resolveUserEmail(request));
			return vendAdmin.getVendorAdminID();
		} catch (  AccountNotExistsException e) {
			return null;
		}
	}

}
