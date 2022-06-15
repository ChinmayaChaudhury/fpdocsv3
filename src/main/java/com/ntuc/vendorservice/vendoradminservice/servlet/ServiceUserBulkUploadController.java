package com.ntuc.vendorservice.vendoradminservice.servlet;

import com.google.gson.Gson;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import com.ntuc.vendorservice.foundationcontext.utils.FileUtils;
import com.ntuc.vendorservice.scimservice.fileservice.enums.BulkFileType;
import com.ntuc.vendorservice.scimservice.fileservice.enums.BulkUploadType;
import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileContentException;
import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileFormatException;
import com.ntuc.vendorservice.scimservice.fileservice.service.BulkUploadFactory;
import com.ntuc.vendorservice.scimservice.fileservice.service.BulkUploadParser;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMUserGroup;
import com.ntuc.vendorservice.vendoradminservice.constants.BulkUploadUrls;
import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.vendoradminservice.exceptions.*;
import com.ntuc.vendorservice.vendoradminservice.repository.FairPriceUserGroupRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorAccountRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.services.ManageAccountServiceBean;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.scimservice.exceptions.AccountUpdateSCIException;

import javax.mail.MethodNotSupportedException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class DocumentUploadContoller
 */
@WebServlet(urlPatterns = { "/fpadmin/bulk/upload/vendors", "/fpadmin/bulk/upload/internal" })
@MultipartConfig
public class ServiceUserBulkUploadController extends BaseController {
	private static final long serialVersionUID = 1L; 
	@EJB
	private ManageAccountServiceBean manageAccountServiceBean;
	@EJB
	private FairPriceUserGroupRepositoryBean fairPriceUserGroupBean;
	@EJB
	private VendorAccountRepositoryBean vendorAccountBean;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		final Part filePart = request.getPart("myFileUpload");
		final String fileName = FileUtils.getFileName(filePart);
		final String bulkFileType = fileName.substring(fileName.lastIndexOf(".")+1); 
		InputStream inputStream = null; 
		Result result = new Result();
		List<Result> results=new ArrayList<Result>();
		try {
			inputStream = filePart.getInputStream();  
			switch (BulkUploadUrls.fromValue(request.getRequestURI())) {
			case INTENALUSER_ACCOUNT_BULKUPLOAD:
				final String userGroupID = request.getParameter("fairPriceGroupID");
				BulkUploadParser bulkUploadParser = BulkUploadFactory.getInstance(BulkFileType.fromValue(bulkFileType), BulkUploadType.INTERNAL_ACCOUNT_UPLOAD);
				List<FPSCIUserAccount> userAccounts = (List<FPSCIUserAccount>) bulkUploadParser.readAccountsFromFile(inputStream);
				
				boolean hasFailedAccount=false;
				for(FPSCIUserAccount userAccount:userAccounts){
					String baseUrl = getBaseUrl(request);
					userAccount.setFairpriceUserGroupID(Integer.valueOf(userGroupID)); 
					userAccount.setCreateBy(getLoggedInUserID(request));
					try{
						result=manageAccountServiceBean.initializeUserForSCIAccountNDOCShare(getXsUserInfoAdapter(request),userAccount, SCIMUserGroup.InternalDocumentsEndUser, request, baseUrl,true);
					} catch (AccountDetailsException | MethodNotSupportedException e) {
						result=new Result(ResultType.ERROR,e.getMessage());  
						hasFailedAccount=true;
					} catch (AccountExistsException | AccountNotExistsException e) {
						result=new Result(ResultType.EXISTS,e.getMessage());  
						hasFailedAccount=true;
					}
					catch ( AccountManagementException | AccountUpdateSCIException e) {
						result=new Result(ResultType.ERROR,e.getMessage());
						hasFailedAccount=true;
					} /*catch (SCIServiceException e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						result=new Result(ResultType.ERROR,e.getMessage());
					}*/
					String message = String.valueOf(result.getResult());
					result.setResult("Record with email:"+userAccount.getEmail().concat(" failed. Reason:- ").concat(message));
					results.add(result);
				}
				if(hasFailedAccount){
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					result = new Result(ResultType.PARTIAL,results);
				}else{
					List<InternalUser> findAllInternalUser = fairPriceUserGroupBean.findAllInternalUser();
					result = new Result(ResultType.SUCCESS,findAllInternalUser);
				} 
				break;
			
			case VENDOR_ACCOUNT_BULKUPLOAD:
				bulkUploadParser = BulkUploadFactory.getInstance(BulkFileType.fromValue(bulkFileType), BulkUploadType.VENDOR_ACCOUNT_UPLOAD);
				userAccounts = (List<FPSCIUserAccount>) bulkUploadParser.readAccountsFromFile(inputStream);
				
				hasFailedAccount=false;
				for(FPSCIUserAccount userAccount:userAccounts){
					String baseUrl = getBaseUrl(request); 
					try{
						result=manageAccountServiceBean.initializeUserForSCIAccountNDOCShare(getXsUserInfoAdapter(request),userAccount, SCIMUserGroup.VendorDocumentsAdministrator, request, baseUrl,true);
					} catch (AccountDetailsException | MethodNotSupportedException e) { 
						result=new Result(ResultType.ERROR,e.getMessage());  
						hasFailedAccount=true;
					} catch (AccountExistsException|AccountNotExistsException e) {   
						result=new Result(ResultType.EXISTS,e.getMessage());  
						hasFailedAccount=true;
					}
					catch ( AccountManagementException | AccountUpdateSCIException  e) {
						result=new Result(ResultType.ERROR,e.getMessage());
						hasFailedAccount=true;
					} /*catch (SCIServiceException e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						result=new Result(ResultType.ERROR,e.getMessage());
					}*/
					String message = String.valueOf(result.getResult());
					result.setResult("Record with email:"+userAccount.getEmail().concat(" failed. Reason:- ").concat(message));
					results.add(result);
				}
				if(hasFailedAccount){
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					result = new Result(ResultType.PARTIAL,results);
				}else{ 
					result = new Result(ResultType.SUCCESS,vendorAccountBean.findAll());
				} 
				break;
			default:
			case UNKNOWN:
				handleUnknownRequests(response);
				break; 
		
		   } 
		} catch (IOException e) {
			e.printStackTrace();
			results.add(new Result(ResultType.ERROR,e.getMessage()));  
			result = new Result(ResultType.PARTIAL,results); 
		} catch (BulkFileContentException | BulkFileFormatException e) {
			results.add(new Result(ResultType.ERROR,e.getMessage())); 
			result.setResult(results);
			result = new Result(ResultType.PARTIAL,results);
		}  finally { 
			if (inputStream != null) {
				inputStream.close();
			}
		}
		response.getWriter().print(new Gson().toJson(result));

	}
 
	 

}
