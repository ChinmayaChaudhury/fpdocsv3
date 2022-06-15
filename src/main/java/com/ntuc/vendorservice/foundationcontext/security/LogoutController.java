package com.ntuc.vendorservice.foundationcontext.security;

import java.io.IOException;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.cloud.security.token.SecurityContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import com.sap.security.auth.login.LoginContextFactory;


@WebServlet(name="LogoutController",
urlPatterns={"/logout"})
public class LogoutController extends HttpServlet {
	private static final Log LOGGER = LogFactory.getLog(LogoutController.class);
	private static final long serialVersionUID = 1;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  if (request.getRemoteUser() != null) {
		  try {
		     /* LoginContext loginContext = LoginContextFactory.createLoginContext();
		      loginContext.logout();*/
			  SecurityContext.clear();
		  }catch (Exception exc) {
		      LOGGER.error("Logout failed.", exc);
		  }
	   }
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}