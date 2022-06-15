package com.ntuc.vendorservice.foundationcontext.catalog.web;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.AuthenticationResult;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.entity.UserProfile;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountNotExistsException;
import com.sap.cloud.security.adapter.xs.XSUserInfoAdapter;
import com.sap.cloud.security.token.SecurityContext;
import com.sap.cloud.security.token.XsuaaToken;
import com.sap.xsa.security.container.XSUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * Base controller
 */
public abstract class BaseEntryController extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseEntryController.class);

	/**
	 * 
	 */


	private static final long serialVersionUID = 1L;
	/***
	 *
	 * @return
	 */
	protected String resolveUserEmail(HttpServletRequest request) {
		final XSUserInfo xsUserInfoAdapter = getXsUserInfoAdapter(request);
		return xsUserInfoAdapter.getEmail();
	}

	protected XSUserInfo getXsUserInfoAdapter(HttpServletRequest request) {
		XSUserInfo xsUserInfo=null;
		try {
			final String authorization = request.getHeader("Authorization");
			final JWT bearer = JWTParser.parse(authorization.replace("Bearer", "").trim());
			xsUserInfo = new XSUserInfoAdapter(new XsuaaToken(bearer.getParsedString()));
		}catch (ParseException e) {
			e.printStackTrace();
		}
		return xsUserInfo;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	/**
	 * Dispatch the request to the home of the requesting account
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void dispatchToHomePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	/**
	 * Deny access by redirecting back to home
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void dispatchToDefaultLandingPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getRemoteUser() != null) {
		    try {
				SecurityContext.clear();
		    } catch (Exception exc) {
		    	LOGGER.error("Logout failed.", exc);
		    }
		   
	   }
		final String defualtLandingPage = "/index.jsp";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(defualtLandingPage);
		dispatcher.forward(request, response);
	}
	/**
	 * Process the get or post request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException;
	/**
	 * Verify the account for registration, verification or subsequent accesses
	 * @param request
	 * @return
	 */
	protected abstract AuthenticationResult verifyAccountAccess(HttpServletRequest request);
	/**
	 * Activate the account if such an account is registered and not activated
	 * @param request
	 * @return
	 */
	protected abstract ResultType validateNewlyCreatedAccount(HttpServletRequest request);
	/**
	 * Get User profile
	 * @return
	 * @throws AccountNotExistsException 
	 */
    protected abstract UserProfile  getUserProfile(HttpServletRequest request) throws AccountNotExistsException;
	

     

}
