package com.ntuc.vendorservice.scimservice.serlvet;

import com.google.gson.Gson;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.VendorAccountStatus;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.vendoraccountservice.services.ProspectVendorAccountServiceBean;
import com.ntuc.vendorservice.vendoradminservice.constants.ProspectAccountUrl;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountExistsException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountNotExistsException;
import com.ntuc.vendorservice.vendoradminservice.repository.ProspectVendorAccountRepositoryBean;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "CWFAPIController", urlPatterns = { "/prospect/*" })
public class CWFProspectVendorController extends BaseController {
	private static final long serialVersionUID = 1;
	@EJB
	protected ProspectVendorAccountServiceBean prospectVendorAccountServiceBean;
	@EJB
	protected ProspectVendorAccountRepositoryBean prospectVendorAccountBean;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		response.setContentType("application/json");
		ProspectAccountUrl accountUrl = ProspectAccountUrl.fromValue(request.getRequestURI());
		switch (accountUrl) {
		case ACCOUNT_QUERY:
			Result result =new Result();
			try {
				URI uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
				List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding());
				String accountID = ""; 
				Iterator<NameValuePair> nameValueIter = parse.iterator();
				while (nameValueIter.hasNext()) {
					NameValuePair nameValuePair = nameValueIter.next();
					if (nameValuePair.getName().equals("accountID")) {
						accountID = nameValuePair.getValue();
					}
				}
				if (!StringUtils.isEmpty(accountID)) {
					result.setResult(prospectVendorAccountBean.findByAccountID(Long.getLong(accountID)));
				} else { 
					result.setResult(prospectVendorAccountBean.findByStatus(VendorAccountStatus.PROSPECT));
				}
				result.setResultType(ResultType.SUCCESS);
				response.getWriter().print(new Gson().toJson(result)); 
			} catch (URISyntaxException e ) { 
				response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
			} 
			break; 
		case ACCOUNT_DEACTIVATE:
			result =new Result();
			try {
				URI uri = new URI(request.getRequestURL().append("?").append(request.getQueryString()).toString());
				List<NameValuePair> parse = URLEncodedUtils.parse(uri, request.getCharacterEncoding());
				String sciUserID = ""; 
				Iterator<NameValuePair> nameValueIter = parse.iterator();
				while (nameValueIter.hasNext()) {
					NameValuePair nameValuePair = nameValueIter.next();
					if (nameValuePair.getName().equals("sciUserID")) {
						sciUserID = nameValuePair.getValue();
					}
				}
				if (!StringUtils.isEmpty(sciUserID)) {
					result=prospectVendorAccountServiceBean.deactivateTempVendorAccount(sciUserID,request);
				} else { 
					  throw new RequestDataException("Parameter 'sciUserID' has not been passed");
				}
				result.setResultType(ResultType.SUCCESS);
				response.getWriter().print(new Gson().toJson(result)); 
			} catch (URISyntaxException | RequestDataException | AccountNotExistsException | AccountManagementException e ) {
				response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
			} 
			break;
		default:
			methodNotImplemented(response);
			break;
		}
	} 
	@Override
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		ProspectAccountUrl accountUrl = ProspectAccountUrl.fromValue(request.getRequestURI());
		Result result=new Result();
		switch (accountUrl) {
		case ACCOUNT_CREATE: 
			try {  
				ProspectVendorAccount vendorAccount=new Gson().fromJson(getPostedJsonData(request), ProspectVendorAccount.class);  
				result = prospectVendorAccountServiceBean.createTempVendorAccount(vendorAccount,request);
			} catch (RequestDataException | AccountExistsException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			} catch ( AccountManagementException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result=new Result(ResultType.ERROR,e.getMessage());
			} 
			response.getWriter().print(new Gson().toJson(result));
			break;
		case ACCOUNT_UPDATE:
			try {  
				ProspectVendorAccount vendorAccount=new Gson().fromJson(getPostedJsonData(request), ProspectVendorAccount.class);  
				result = prospectVendorAccountServiceBean.updateTempVendorAccount(vendorAccount,request);
			} catch (RequestDataException | AccountNotExistsException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			} catch ( AccountManagementException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result=new Result(ResultType.ERROR,e.getMessage());
			} 
			response.getWriter().print(new Gson().toJson(result));
			break;
		case ACCOUNT_ACTIVATE:
			try {  
				FPSCIUserAccount fpSciAccount=new Gson().fromJson(getPostedJsonData(request), FPSCIUserAccount.class);
				result = prospectVendorAccountServiceBean.activateTempVendorAccount(fpSciAccount,request,getBaseUrl(request));
			} catch (RequestDataException | AccountNotExistsException|AccountExistsException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result=new Result(ResultType.ERROR,e.getMessage());  
			} catch ( AccountManagementException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result=new Result(ResultType.ERROR,e.getMessage());
			} 
			response.getWriter().print(new Gson().toJson(result));
			break; 
		 
		default:
			methodNotImplemented(response);
			break;
		}
		
	}
}