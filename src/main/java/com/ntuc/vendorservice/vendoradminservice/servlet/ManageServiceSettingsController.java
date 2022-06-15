package com.ntuc.vendorservice.vendoradminservice.servlet;

import com.google.gson.Gson;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import com.ntuc.vendorservice.vendoradminservice.repository.FairPriceUserGroupRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorUserGroupBean;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.FairPriceUserGroup;
import com.ntuc.vendorservice.vendoradminservice.models.UserGroupType;
import com.ntuc.vendorservice.vendoradminservice.constants.SettingsUrl;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorUserGroup;
import com.ntuc.vendorservice.vendoradminservice.models.VendorCategory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servlet implementation class ManageSettingsController
 */
@WebServlet(name = "ManageSettingsController", urlPatterns = {"/fpadmin/settings/*" })
public class ManageServiceSettingsController extends BaseController {
	private static final long serialVersionUID = 1L;
	@EJB
	private VendorUserGroupBean vendorUserGroupBean;
	@EJB
	private FairPriceUserGroupRepositoryBean fairPriceUserGroupRepositoryBean;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		   handleRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handleRequest(request, response);
	}

	protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		SettingsUrl settingsUrl = SettingsUrl.fromValue(request.getRequestURI());
		List<FairPriceUserGroup> fairPriceUserGroups;

		switch (settingsUrl) {

		case VENDOR_CATEGORY:
			Result keyValResult = new Result(ResultType.SUCCESS, VendorCategory.toKeyValFormat());
			response.getWriter().print(new Gson().toJson(keyValResult));
			break;
		case USER_GROUPS_TYPES:
			keyValResult = new Result(ResultType.SUCCESS, UserGroupType.toKeyValFormat());
			response.getWriter().print(new Gson().toJson(keyValResult));
			break;
		case FPUSER_GROUPS_CREATE:

			try{
			String postedJsonData = getPostedJsonData(request);
			FairPriceUserGroup fairPriceUserGroup = new Gson().fromJson(postedJsonData, FairPriceUserGroup.class);
			Date createDateTime = new Date();
			fairPriceUserGroup.setCreateDateTime(createDateTime);
			fairPriceUserGroup.setActivatedBy(getLoggedInUserID(request));
			fairPriceUserGroup.setCreatedBy(getLoggedInUserID(request));
			fairPriceUserGroup.setActivationDate(createDateTime);
			fairPriceUserGroupRepositoryBean.addFairPriceUserGroup(fairPriceUserGroup);
			fairPriceUserGroups = fairPriceUserGroupRepositoryBean.findAllFairPriceUserGroups();
			if (fairPriceUserGroups == null) {
				fairPriceUserGroups = new ArrayList<>();
			}
			Result result = new Result(ResultType.SUCCESS, fairPriceUserGroups);
			response.getWriter().print(new Gson().toJson(result));
		} catch (RequestDataException e) {
			handleDataErrorRequests(response);
		}
			break;
		case FPUSER_GROUPS_UPDATE:
			try{
			String postedJsonData = getPostedJsonData(request);
			FairPriceUserGroup fairPriceUserGroup = new Gson().fromJson(postedJsonData, FairPriceUserGroup.class);
			int updateGroup = fairPriceUserGroupRepositoryBean.updateGroup(fairPriceUserGroup);
			Result result = new Result(ResultType.ERROR, "Error Exists!");
			if(updateGroup>0){
				fairPriceUserGroups = fairPriceUserGroupRepositoryBean.findAllFairPriceUserGroups();
				if (fairPriceUserGroups == null) {
					fairPriceUserGroups = new ArrayList<FairPriceUserGroup>();
				}
				result = new Result(ResultType.SUCCESS, fairPriceUserGroups);
			}
			response.getWriter().print(new Gson().toJson(result));
			} catch (RequestDataException e) {
				handleDataErrorRequests(response);
			}
			break;
		case FPUSER_GROUPS_QUERY: 
			fairPriceUserGroups = fairPriceUserGroupRepositoryBean.findAllFairPriceUserGroups();
			if (fairPriceUserGroups == null) {
				fairPriceUserGroups = new ArrayList<FairPriceUserGroup>();
			}
			Result result = new Result(ResultType.SUCCESS, fairPriceUserGroups);
			response.getWriter().print(new Gson().toJson(result));
			break;
		case VUSER_GROUPS_QUERY:
			List<VendorUserGroup> vendorUserGroups = vendorUserGroupBean.findAll();
			if (vendorUserGroups == null) {
				vendorUserGroups = new ArrayList<VendorUserGroup>();
			}
			keyValResult = new Result(ResultType.SUCCESS, vendorUserGroups);
			response.getWriter().print(new Gson().toJson(keyValResult));
			break;
		case VUSER_GROUPS_CREATE:
			try{
			String postedJsonData = getPostedJsonData(request);
			VendorUserGroup vendorUserGroup = new Gson().fromJson(postedJsonData, VendorUserGroup.class);
			Date createDateTime = new Date();
			vendorUserGroup.setCreateDateTime(createDateTime);
			vendorUserGroup.setActivatedBy(getLoggedInUserID(request));
			vendorUserGroup.setCreatedBy(getLoggedInUserID(request));
			vendorUserGroup.setActivationDate(createDateTime);
			vendorUserGroupBean.add(vendorUserGroup);
			vendorUserGroups = vendorUserGroupBean.findAll();
			if (vendorUserGroups == null) {
				vendorUserGroups = new ArrayList<VendorUserGroup>();
			}
			keyValResult = new Result(ResultType.SUCCESS, vendorUserGroups);
			response.getWriter().print(new Gson().toJson(keyValResult));
			} catch (RequestDataException e) {
				handleDataErrorRequests(response);
			}
			break;
		default:
			handleUnknownRequests(response);
			break;
		}

	}
}
