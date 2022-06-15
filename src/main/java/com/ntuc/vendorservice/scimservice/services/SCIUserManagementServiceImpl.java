package com.ntuc.vendorservice.scimservice.services;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;
import com.ntuc.vendorservice.foundationcontext.catalog.model.VendorAccountModel;
import com.ntuc.vendorservice.foundationcontext.utils.IOUtils;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.scimservice.utils.SCIUtils;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Implement the SCIM and SCI Local users
 * @author I305675
 *
 */
public class SCIUserManagementServiceImpl implements SCIUserManagementService {

	
	@Override
	public FPSCIUserAccount createUserAccount(FPSCIUserAccount fpsciUserAccount, HttpDestination httpDestination) throws HttpResponseException {
		final HttpPost httpPost = new HttpPost();
		try {
			final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(SCIUtils.getSCIUserRegistrationRequest(fpsciUserAccount));
			httpPost.setHeader(HTTP.CONTENT_TYPE, SCI_CONTENT_TYPE); 
			httpPost.setEntity(entity); 
			URI destURI = httpDestination.getUri();
			URI uri = new URI(destURI.getScheme(), destURI.getHost(), destURI.getPath() + USER_REGISTRATION_END_POINT, null);
			httpPost.setURI(uri);

			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpPost); 
			String responseData = IOUtils.getInputFromStream(httpResponse.getEntity().getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_CREATED) { 
				return  fpsciUserAccount; 
			} else {
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;				
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
		} catch (DestinationAccessException | IOException | URISyntaxException e) {
			e.printStackTrace();
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}   
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		} 
	}

	@Override
	public String createUserAccountWithCallBackActivationUrl(FPSCIUserAccount fpsciUserAccount, HttpDestination httpDestination) throws HttpResponseException {
		HttpPost httpPost = new HttpPost();
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(SCIUtils.getSCIUserRegistrationRequest(fpsciUserAccount)); 
			httpPost.setHeader(HTTP.CONTENT_TYPE, SCI_CONTENT_TYPE); 
			httpPost.setEntity(entity); 
			URI destURI = httpDestination.getUri();
			URI uri = new URI(destURI.getScheme(), destURI.getHost(), destURI.getPath() + USER_REGISTRATION_END_POINT, null);
			httpPost.setURI(uri);
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpPost); 
			String responseData = IOUtils.getInputFromStream(httpResponse.getEntity().getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_CREATED) {
				JsonObject jsonObject =  JsonParser.parseString(responseData).getAsJsonObject();
				return  jsonObject.get("activationLink").getAsString(); 
			} else {
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;				
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
		} catch (DestinationAccessException | IOException | URISyntaxException e) {
			e.printStackTrace();
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}   
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		} 
	}
	
	
	@Override
	public SCIMUser createUserAccount(SCIMUser scimUser, HttpDestination httpDestination) throws HttpResponseException {
		try {
			HttpPost httpPost = new HttpPost();
			StringEntity entity = new StringEntity(new Gson().toJson(scimUser));
			httpPost.setHeader(HTTP.CONTENT_TYPE, SCIM_CONTENT_TYPE);
			httpPost.setEntity(entity); 
			URI destURI = httpDestination.getUri();
			String servicePath = new StringBuffer().append(destURI.getPath()).append(String.format(USER_SCIM_END_POINT, "")).toString();
			URI uri = new URI(destURI.getScheme(), destURI.getHost(), servicePath, null);
			httpPost.setURI(uri);
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity responseEntity = httpResponse.getEntity(); 
			String responseData = IOUtils.getInputFromStream(responseEntity.getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_CREATED) {
				return  new Gson().fromJson(responseData, SCIMUser.class);
			}  else { 
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;				
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
	  } catch (DestinationAccessException | IOException | URISyntaxException e) {
		e.printStackTrace();
		if (e instanceof IOException) {
			if (e instanceof HttpResponseException) {
				throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
			}else{
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} else if (e instanceof DestinationAccessException) {
			throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} else if (e instanceof NamingException) {
			throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} 
		throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
				"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
	  }
	}

	@Override
	public SCIMUser updateUserAccount(SCIMUser scimUser, HttpDestination httpDestination) throws HttpResponseException{
		try {
			HttpPut httpPut = new HttpPut();
			StringEntity entity = new StringEntity(new Gson().toJson(scimUser));
			httpPut.setHeader(HTTP.CONTENT_TYPE, SCIM_CONTENT_TYPE);
			httpPut.setEntity(entity); 
			URI destURI = httpDestination.getUri();
			String servicePath = new StringBuffer().append(destURI.getPath()).append(String.format(USER_SCIM_END_POINT, scimUser.getUserID())).toString();
			URI uri = new URI(destURI.getScheme(), destURI.getHost(), servicePath, null);
			httpPut.setURI(uri);
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpPut);
			HttpEntity responseEntity = httpResponse.getEntity(); 
			String responseData = IOUtils.getInputFromStream(responseEntity.getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK) {
				return  new Gson().fromJson(responseData, SCIMUser.class);
			}  else { 
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;				
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
	  } catch (DestinationAccessException | IOException | URISyntaxException e) {
		e.printStackTrace();
		if (e instanceof IOException) {
			if (e instanceof HttpResponseException) {
				throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
			}else{
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} else if (e instanceof DestinationAccessException) {
			throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} else if (e instanceof NamingException) {
			throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} 
		throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
				"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
	  }
	}

	@Override
	public FPSCIUserAccount getUserProfile(String profileID, HttpDestination httpDestination) throws HttpResponseException { 
		HttpGet httpGet = new HttpGet(String.format(USER_UME_GET,profileID));
		try {    
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpGet); 
			FPSCIProfileXmlSaxParserTransformer fpsciProfileXmlSaxParserTransformer =new FPSCIProfileXmlSaxParserTransformer();
			InputStream content = httpResponse.getEntity().getContent();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK ) {  
				List<FPSCIUserAccount> responseData= fpsciProfileXmlSaxParserTransformer.parseXmlResponse(content);
				return responseData.get(0);
			} else { 
				String responseData = IOUtils.getInputFromStream(content); 
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;
				throw new HttpResponseException(statusCode, reasonPhrase );
			}
		} catch ( DestinationAccessException | IOException e) {
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} else if (e instanceof NamingException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		} 
	}

	@Override
	public List<SCIMUser> parseUserQuery(String queryString, HttpDestination httpDestination) throws HttpResponseException { 
		HttpGet httpGet = new HttpGet(String.format(USER_SCIM_SEARCH,queryString));
		try {    
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpGet); 
			String responseData = IOUtils.getInputFromStream(httpResponse.getEntity().getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK ) {  
				JsonObject parse =  JsonParser.parseString(responseData).getAsJsonObject();
				Iterator<JsonElement> iterator = parse.get("Resources").getAsJsonArray().iterator();
				List<SCIMUser> scimUsers=new ArrayList<SCIMUser>();
				while(iterator.hasNext()){
					scimUsers.add(new Gson().fromJson(iterator.next(), SCIMUser.class));
				} 
				return scimUsers;
			} else {  
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;
				throw new HttpResponseException(statusCode, reasonPhrase ); 
			}
		} catch (DestinationAccessException | IOException  e) {
			e.printStackTrace();
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}  
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		}  
	}

	@Override
	public List<KeyVal> getAttribute(String attributeName, HttpDestination httpDestination)
			throws HttpResponseException {
		HttpGet httpGet = new HttpGet(attributeName);
		try {    
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpGet); 
			String responseData = IOUtils.getInputFromStream(httpResponse.getEntity().getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK ) {
				 JsonElement parse =  JsonParser.parseString(responseData);
				 JsonObject asJsonObject = parse.getAsJsonObject();
				 List<KeyVal> keyVals=new ArrayList<KeyVal>();
				 Set<Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
				 for(Entry<String, JsonElement> entry:entrySet){
					 String value = entry.getValue().getAsString();
					 keyVals.add(new KeyVal(value, value));
				 } 
				return keyVals;
			} else { 
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
		} catch (DestinationAccessException | IOException  e) {
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}  
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		} 
	}
	@Override
	public SCIMUser getUserDetail(String profileID, HttpDestination httpDestination) throws HttpResponseException {
		HttpGet httpGet = new HttpGet(String.format(USER_SCIM_GET,profileID));
		try {    
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpGet); 
			String responseData = IOUtils.getInputFromStream(httpResponse.getEntity().getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK ) {  
				return  new Gson().fromJson(responseData, SCIMUser.class);
			} else { 
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
		} catch (DestinationAccessException | IOException  e) {
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}  
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		} 
	}
	@Override
	public ResultType parseDeleteAccount(String userID, HttpDestination httpDestination) throws HttpResponseException {
		HttpDelete httpDelete = new HttpDelete(String.format(USER_SCIM_GET,userID));
		try {    
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpDelete);  
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_NO_CONTENT ) {  
				return  ResultType.SUCCESS;
			} else { 
				String responseData = IOUtils.getInputFromStream(httpResponse.getEntity().getContent());
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
		} catch (DestinationAccessException | IOException  e) {
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}  
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		} 
	}
	
	@Override
	public VendorAccountModel getVendorCodeFromSap(String vendorCode, HttpDestination httpDestination) throws HttpResponseException {
		HttpGet httpGet = new HttpGet(String.format(SAP_GW_GET,vendorCode));
		try {    
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpGet); 
			String responseData = IOUtils.getInputFromStream(httpResponse.getEntity().getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK ) {
				return  new Gson().fromJson(responseData, VendorAccountModel.class);
			} else { 
				String reasonPhrase = StringUtils.isEmpty(responseData)?httpResponse.getStatusLine().getReasonPhrase():responseData;
				throw new HttpResponseException(statusCode, reasonPhrase);
			}
		} catch (DestinationAccessException | IOException  e) {
			if (e instanceof IOException) {
				if (e instanceof HttpResponseException) {
					throw new HttpResponseException(((HttpResponseException) e).getStatusCode(), e.getMessage());
				}else{
					throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else if (e instanceof DestinationAccessException) {
				throw new HttpResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			throw new HttpResponseException(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Error Encountered, status-" + HttpServletResponse.SC_EXPECTATION_FAILED);
		} 
	}

	public static SCIUserManagementService getInstance() { 
		return new SCIUserManagementServiceImpl();
	} 
	protected SCIUserManagementServiceImpl(){
		super();
	}

	

	

	

	 
}
