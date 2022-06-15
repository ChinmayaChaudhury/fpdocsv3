package com.ntuc.vendorservice.vendoradminservice.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;
import com.ntuc.vendorservice.foundationcontext.catalog.constants.RootURIPath;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseEntryController;
import com.ntuc.vendorservice.scimservice.proxy.SCIIdentityProxy;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.AuthenticationResult;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.UserProfile;
import com.sap.cloud.security.adapter.xs.XSUserInfoAdapter;
import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.servlet.AbstractTokenAuthenticator;
import com.sap.cloud.security.servlet.XsuaaTokenAuthenticator;
import com.sap.cloud.security.token.SecurityContext;
import com.sap.cloud.security.token.Token;

/**
 * Servlet implementation class DocumentShareAdministrationController
 */
@WebServlet(urlPatterns={"/fpadmin","/fpadmin/profile"})
public class VendorAdministrationController extends BaseEntryController {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		final String requestURI = request.getRequestURI();
		RootURIPath fromValue = RootURIPath.fromValue(requestURI);
		switch (fromValue) {
			case FAIRPRICE_ADMIN:
				dispatchToHomePage(request, response);
				break;
			case FAIRPRICE_ADMIN_PROFILE:
				response.setContentType("application/json");
				Result result=new Result();
				result.setResultType(ResultType.SUCCESS);
				result.setResult(getUserProfile(request));
				response.getWriter().print(new Gson().toJson(result));
				break;
			default:
				dispatchToDefaultLandingPage(request, response);
				break;
		}

	}
	@Override
	protected void dispatchToHomePage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String adminPage = "/admin.jsp";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(adminPage);
		dispatcher.forward(request,response);

	}
	@Override
	protected AuthenticationResult verifyAccountAccess(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected ResultType validateNewlyCreatedAccount(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected UserProfile getUserProfile(HttpServletRequest request) {
		final XSUserInfoAdapter xsUserInfoAdapter = new XSUserInfoAdapter(SecurityContext.getAccessToken());
		return SCIIdentityProxy.getFPAdministratorProfile(xsUserInfoAdapter);
	}

}
