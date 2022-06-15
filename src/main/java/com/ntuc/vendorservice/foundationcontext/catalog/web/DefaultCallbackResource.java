package com.ntuc.vendorservice.foundationcontext.catalog.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

@WebServlet(urlPatterns = { "/oauth/callback"})
public class DefaultCallbackResource extends HttpServlet{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String URL_MAPPING = "/oauth/callback";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 final String authCode = request.getParameter("code"); 
		List <NameValuePair> attributes=new ArrayList<NameValuePair>(); 
		attributes.add(new BasicNameValuePair("grant_type", "authorization_code"));
		attributes.add(new BasicNameValuePair("code", authCode));
		attributes.add(new BasicNameValuePair("redirect_uri", "_REDIRECT_URI_MAINTAINED_IN_CLIENT_REGISTRATION_"));
		attributes.add(new BasicNameValuePair("client_id", "_CLIENT_ID_GENERATED_IN_CLIENT_REGISTRATION_"));
		attributes.add(new BasicNameValuePair("client_secret", "_CLIENT_SECRET_MAINTAINED_IN_CLIENT_REGISTRATION_"));
		UrlEncodedFormEntity formData=new UrlEncodedFormEntity(attributes);  
		response.setContentType("application/json"); 
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", getOAuthCodeCallbackHandlerUrl(request));
		response.getWriter().print(new Gson().toJson(formData)); 
	}
	/**
	   * Construct the OAuth code callback handler URL.
	   *
	   * @param req the HttpRequest object
	   * @return The constructed request's URL
	   */
	  public static String getOAuthCodeCallbackHandlerUrl(HttpServletRequest req) {
	    String scheme = req.getScheme() + "://";
	    String serverName = req.getServerName();
	    String serverPort = ((req.getServerPort() == 80) ||(req.getServerPort() == 443))? "" : ":" + req.getServerPort();
	    String contextPath = req.getContextPath();
	    String servletPath = URL_MAPPING;
	    String pathInfo = (req.getPathInfo() == null) ? "" : req.getPathInfo();
	    return scheme + serverName + serverPort + contextPath + servletPath + pathInfo;
	  }

}
