package com.ntuc.vendorservice.foundationcontext.catalog.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.ntuc.vendorservice.emailingservice.services.EmailServiceBean;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.sap.cloud.security.adapter.xs.XSUserInfoAdapter;
import com.sap.cloud.security.token.AccessToken;
import com.sap.cloud.security.token.XsuaaToken;
import com.sap.xsa.security.container.XSUserInfo;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 
 * @author I305675
 *
 */
public abstract class BaseController extends HttpServlet{
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
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
		return xsUserInfoAdapter.getLogonName();
	}

	protected XSUserInfo getXsUserInfoAdapter(final HttpServletRequest request) {
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



	public  String getPostedJsonData(HttpServletRequest request) throws ServletException, IOException, RequestDataException{
			if(!request.getContentType().contains("application/json")){
				throw new RequestDataException();
			}
		
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = request.getReader();
	        while ((line = reader.readLine()) != null) {
	               builder.append(line);
	        } 
	        String jsonData = builder.toString();
	        if(jsonData.isEmpty()){
	        	throw new RequestDataException("Data not available");
	        }
			return jsonData; 
		
	} 
	protected  JsonObject getParsedJson(String postedJsonData) throws RequestDataException{
		try{
			return   JsonParser.parseString(postedJsonData).getAsJsonObject();
		}catch(  com.google.gson.JsonIOException|com.google.gson.JsonSyntaxException e){
			throw new RequestDataException(e.getMessage());
		}catch(IllegalStateException e){
			throw new RequestDataException(e.getMessage());
		}
	}
	public  String getPostedFormData(HttpServletRequest request) throws ServletException, IOException, RequestDataException{
		Enumeration<String> parameterNames = request.getParameterNames();
		JsonObject jsonObject=new JsonObject();
		while(parameterNames.hasMoreElements()){
			String nextParameter = parameterNames.nextElement();
			Object parameterValue = request.getParameter(nextParameter);
			jsonObject.add(nextParameter,new JsonPrimitive(String.valueOf(parameterValue)) ); 
		} 
        String jsonData = jsonObject.toString();
        if(jsonData.isEmpty()){
        	throw new RequestDataException("Data not available");
        }
		return jsonData; 
	} 
	public  List <NameValuePair> getPostedAttributes(HttpServletRequest request) throws ServletException, IOException, RequestDataException{
		Enumeration<String> parameterNames = request.getParameterNames();
		List <NameValuePair> attributes=new ArrayList<NameValuePair>();
		while(parameterNames.hasMoreElements()){
			String nextParameter = parameterNames.nextElement();
			Object parameterValue = request.getParameter(nextParameter);
			attributes.add(new BasicNameValuePair(nextParameter,String.valueOf(parameterValue))) ; 
			
		}  
        if(attributes.isEmpty()){
        	throw new RequestDataException("Data not available");
        }
//        System.out.println(attributes);
		return attributes; 
	} 
	
	
	protected String getBaseUrl(HttpServletRequest request) {
		int serverPort = request.getServerPort();
		String port=serverPort<=0?"":":"+String.valueOf(serverPort); 
		String systemScheme=request.getScheme();
		String serverName=request.getServerName();
		return systemScheme+"://"+serverName+port+"/fp.docs";
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
	protected void methodNotImplemented(HttpServletResponse response)throws ServletException, IOException {
		response.setContentType("application/json");
		Result keyValResult = new Result(ResultType.ERROR,"Method not implemented");
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		response.getWriter().print(new Gson().toJson(keyValResult));
	} 
	protected abstract void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException ;
	
	protected void handleUnknownRequests(HttpServletResponse response)throws ServletException, IOException {
		Result keyValResult = new Result(ResultType.ERROR, new KeyVal("Failed", "Unknown request"));
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().print(new Gson().toJson(keyValResult));
	}
	protected void handleDataErrorRequests(HttpServletResponse response)throws ServletException, IOException {
		Result keyValResult = new Result(ResultType.ERROR, new KeyVal("Failed", "Data cannot be empty"));
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().print(new Gson().toJson(keyValResult));
	}
	protected void handleWrongApplicationFormat(HttpServletResponse response)throws ServletException, IOException {
		Result keyValResult = new Result(ResultType.ERROR, new KeyVal("Failed", "Bad request, the format sent isnt correct"));
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().print(new Gson().toJson(keyValResult));
	}

	protected String getLoggedInUserID(final HttpServletRequest request) {
		///
		final Principal principal = request.getUserPrincipal();
		final XSUserInfo xsUserInfo = (XSUserInfo) principal;
		return xsUserInfo.getLogonName();
	}
	  
}
