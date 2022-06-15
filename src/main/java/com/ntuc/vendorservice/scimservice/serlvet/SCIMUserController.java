package com.ntuc.vendorservice.scimservice.serlvet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.vendoradminservice.exceptions.SCIServiceException;
import com.ntuc.vendorservice.vendoradminservice.services.ManageAccountServiceBean;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.google.gson.Gson;
import com.ntuc.vendorservice.scimservice.constants.SCIUserUrls;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;

/**
 * 
 * @author I305675
 *
 */
@WebServlet(urlPatterns={"/fpadmin/epuser/*","/vadmin/epuser/*","/fpadmin/account/*","/scim/attr","/vadmin/account/status"})
public class SCIMUserController extends BaseController {
	private static final long serialVersionUID = 1L;
	@EJB
	private ManageAccountServiceBean manageAccountServiceBean;
    /**
     * Default constructor. 
     */
    public SCIMUserController() {
       super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 SCIUserUrls sciUserUrls = SCIUserUrls.fromValue(request.getRequestURI());
		 response.setContentType("application/json");
		 Result result = new Result();
		 switch(sciUserUrls){ 
		  case FPADMIN_SCIM_USER_STATUS:
		  case VADMIN_SCIM_USER_STATUS:
			  response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS,SystemAccountStatus.toKeyValFormat())));
			  break;
		  case SCIM_ATTRIBUTES:
			  URI uri;
				try {
					uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
					List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding()); 
					String attributeName = ""; 
					Iterator<NameValuePair> nameValueIter = parse.iterator();
					while (nameValueIter.hasNext()) {
						NameValuePair nameValuePair = nameValueIter.next();
						if (nameValuePair.getName().equals("attributeName")) {
							attributeName = nameValuePair.getValue();
						}
					}
					if (!StringUtils.isEmpty(attributeName)) {
						result = manageAccountServiceBean.getAttributeKeyVal(attributeName, request);
					} else {
						result.setResultType(ResultType.ERROR);
						result.setResult("attributeName query value not found");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
					response.getWriter().print(new Gson().toJson(result)); 
				} catch (URISyntaxException e ) { 
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				} 
			 break;
		  case SCI_ACCOUNT_DETAILS:  
				try {
					uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
					List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding()); 
					String userID = ""; 
					Iterator<NameValuePair> nameValueIter = parse.iterator();
					while (nameValueIter.hasNext()) {
						NameValuePair nameValuePair = nameValueIter.next();
						if (nameValuePair.getName().equals("userID")) {
							userID = nameValuePair.getValue();
						}
					}
					if (!StringUtils.isEmpty(userID)) {
						result = manageAccountServiceBean.getUMESAPSCIAccountDetails(userID, request);
					} else {
						result.setResultType(ResultType.ERROR);
						result.setResult("userID query value not found");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
					response.getWriter().print(new Gson().toJson(result)); 
				} catch (URISyntaxException | SCIServiceException e ) {
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				}
			 break;
		  case SCI_ACCOUNT_SEARCH:
			  try {
					uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
					List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding());
					String userID = ""; 
					String groupName=""; 
					String expanded="";
					Iterator<NameValuePair> nameValueIter = parse.iterator();
					while (nameValueIter.hasNext()) {
						NameValuePair nameValuePair = nameValueIter.next();
						if (nameValuePair.getName().equals("userID")) {
							userID = nameValuePair.getValue();
						}
						if (nameValuePair.getName().equals("groupName")) {
							groupName = nameValuePair.getValue();
						}
						if (nameValuePair.getName().equals("expanded")) {
							expanded = nameValuePair.getValue();
						} 
					}
					if (!StringUtils.isEmpty(userID)) {
						result = manageAccountServiceBean.getSAPSCIAccountDetails(userID, request);
					}else if (!StringUtils.isEmpty(groupName)) {
						if(!StringUtils.isEmpty(expanded)){
							result = manageAccountServiceBean.getSAPSCIExpandedGroupUsers(groupName, request); 
						}else{
						result = manageAccountServiceBean.getSAPSCIGroupUsers(groupName, request); 
						}
					}
					else {
						result.setResultType(ResultType.ERROR);
						result.setResult("userID/groupName query value not found");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
					response.getWriter().print(new Gson().toJson(result)); 
				} catch (URISyntaxException e ) { 
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				} 
			  break;
		  case SCIM_USER_SEARCH:   
				result = manageAccountServiceBean.parseSCIMUserQuery(request.getQueryString(), request, SCIMUser.class);
				response.getWriter().print(new Gson().toJson(result));
			  break;
		  case SCIM_USER_DELETE:
			  try {
					uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
					List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding());
					String userID = ""; 
					Iterator<NameValuePair> nameValueIter = parse.iterator();
					while (nameValueIter.hasNext()) {
						NameValuePair nameValuePair = nameValueIter.next();
						if (nameValuePair.getName().equals("userID")) {
							userID = nameValuePair.getValue();
						}
					}
					if (!StringUtils.isEmpty(userID)) {
						result = manageAccountServiceBean.deleteSAPSCIAccount(userID, request);
					} else {
						result.setResultType(ResultType.ERROR);
						result.setResult("userID query value not found");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
					response.getWriter().print(new Gson().toJson(result)); 
				} catch (URISyntaxException e ) { 
					response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
				} 
			   break;
		  case UNKNOWN:
				handleUnknownRequests(response);
				break;
		   default:
			   super.methodNotImplemented(response);
			   break;
		 }
		 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	@Override
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		SCIUserUrls epUserUrl = SCIUserUrls.fromValue(request.getRequestURI());
		String postedJsonData;
		try {
			postedJsonData = getPostedJsonData(request); 
			switch(epUserUrl){ 
			case VENDOR_ACCOUNT_CREATE: 
			case VENDOR_USER_CREATE:  
				FPSCIUserAccount userAccount = new Gson().fromJson(postedJsonData, FPSCIUserAccount.class); 
				Result result = manageAccountServiceBean.createSAPSCIAccount(userAccount, request);
			    String serviceUrl=getBaseUrl(request);
				if (!userAccount.isSendEmail()) {
					serviceUrl = String.valueOf(result.getResult());
				}
				if (epUserUrl == SCIUserUrls.VENDOR_ACCOUNT_CREATE) {
					VendorAccount vendorAccount = new VendorAccount();
					// vendorAccount.set
					manageAccountServiceBean.createAccount(vendorAccount, serviceUrl);
				}
				if (epUserUrl == SCIUserUrls.VENDOR_USER_CREATE) {
					VendorUser vendorUser = new VendorUser();
					manageAccountServiceBean.createAccount(vendorUser, serviceUrl);
				}
				response.getWriter().print(new Gson().toJson(result));
				break;
			case SCI_ACCOUNT_UPDATE:
				SCIMUser scimUser = new Gson().fromJson(postedJsonData, SCIMUser.class); 
				result = manageAccountServiceBean.updateSAPSCIAccount(scimUser, request); 
				response.getWriter().print(new Gson().toJson(result));
				break;
			case SCIM_USER_CREATE:
				scimUser = new Gson().fromJson(postedJsonData, SCIMUser.class); 
				 result = manageAccountServiceBean.createSAPSCIAccount(scimUser, request);
				 response.getWriter().print(new Gson().toJson(result));
				break;
			case SCI_ACCOUNT_CREATE: 
				userAccount = new Gson().fromJson(postedJsonData, FPSCIUserAccount.class); 
				if(userAccount.isSendEmail()){
					result = manageAccountServiceBean.createSAPSCIAccount(userAccount, request); 
				}else{
					result = manageAccountServiceBean.createSAPSCIAccountWithActivationCallbackUrl(userAccount, request); 
				} 
				response.getWriter().print(new Gson().toJson(result));
				break;   
			case VENDOR_USER_UPDATE:
			case VENDOR_ACCOUNT_UPDATE:
				 scimUser = new Gson().fromJson(postedJsonData, SCIMUser.class); 
				 result = manageAccountServiceBean.updateSAPSCIAccount(scimUser, request);
				 response.getWriter().print(new Gson().toJson(result));
				break; 
			case UNKNOWN:
				handleUnknownRequests(response);
				break;
			default:
			     super.methodNotImplemented(response);
			   break;
			}
		} catch (RequestDataException e) { 
			handleDataErrorRequests(response);
		}
	 
	}
	
	 
}
