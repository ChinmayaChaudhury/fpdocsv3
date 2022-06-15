package com.ntuc.vendorservice.scimservice.services;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import com.ntuc.vendorservice.foundationcontext.utils.IOUtils;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.scimservice.utils.SCIUtils;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser; 
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

/**
 * 
 * @author I305675
 *
 */
public class SCIGroupManagementServiceImpl implements SCIGroupManagementService{
	
	/**
	 * 
	 * @return {@link SCIGroupManagementService}
	 */
	public static SCIGroupManagementServiceImpl getInstance() { 
		return new SCIGroupManagementServiceImpl();
	} 
	/**
	 * 
	 */
	protected SCIGroupManagementServiceImpl(){
		super();
	}

	
	@Override
	public Set<SCIMGroup> requestSCIMGroups(HttpDestination httpDestination) throws HttpResponseException {
		HttpGet httpGet = new HttpGet(GROUP_SCIM_GET_ALL);
		try {    
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpGet);  
			InputStream content = httpResponse.getEntity().getContent();
			String responseData = IOUtils.getInputFromStream(content);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK ) {   
				return SCIUtils.parseSCIMGroups(responseData);
			} else {   
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
	public List<FPSCIUserAccount> getUsersInGroup(String groupName, HttpDestination httpDestination) throws HttpResponseException {
		try {
			HttpPost httpPost = new HttpPost();
			JsonObject json=new JsonObject();
			json.addProperty("group_name", groupName); 
			json.addProperty("start", 0); 
			json.addProperty("size", 100); 
			StringEntity entity = new StringEntity(new Gson().toJson(json));
			httpPost.setHeader(HTTP.CONTENT_TYPE, JSON_CONTENT_TYPE);
			httpPost.setEntity(entity); 
			URI destURI = httpDestination.getUri();
			String servicePath = new StringBuffer().append(destURI.getPath()).append(String.format(GROUP_SCIM_GET_SPECIFIC, "")).toString();
			URI uri = new URI(destURI.getScheme(), destURI.getHost(), servicePath, null);
			httpPost.setURI(uri);
			HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity responseEntity = httpResponse.getEntity(); 
			String responseData = IOUtils.getInputFromStream(responseEntity.getContent());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == SC_OK) {
				    JsonObject documentData = JsonParser.parseString(responseData).getAsJsonObject();
					JsonElement jsonElement = documentData.get("users");
					if(StringUtils.isEmpty(responseData)||jsonElement==null){
						throw new HttpResponseException(SC_NO_CONTENT, "No users found");
					}
					Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
					List<FPSCIUserAccount> scimUsers=new ArrayList<FPSCIUserAccount>();
					while(iterator.hasNext()){
						FPSCIUserAccount account = SCIUtils.getFPSCIUserAccount(iterator);
						scimUsers.add(account);
				} 
					
					
					//Deepak Call all rest all data from API service Call
					int dataSize = scimUsers.size();
					if(dataSize == 100){
						HttpPost httpPost2 = new HttpPost();
						JsonObject json2=new JsonObject();
						json2.addProperty("group_name", groupName); 
						json2.addProperty("start", 100); 
						json2.addProperty("size", 99); 
						StringEntity entity2 = new StringEntity(new Gson().toJson(json2));
						httpPost2.setHeader(HTTP.CONTENT_TYPE, JSON_CONTENT_TYPE);
						httpPost2.setEntity(entity2); 
						URI destURI2 = httpDestination.getUri();
						String servicePath2 = new StringBuffer().append(destURI2.getPath()).append(String.format(GROUP_SCIM_GET_SPECIFIC, "")).toString();
						URI uri2 = new URI(destURI2.getScheme(), destURI2.getHost(), servicePath2, null);
						httpPost2.setURI(uri2);
						HttpClient httpClient2 = HttpClientAccessor.getHttpClient(httpDestination);
						HttpResponse httpResponse2 = httpClient2.execute(httpPost2);
						HttpEntity responseEntity2 = httpResponse2.getEntity(); 
						String responseData2 = IOUtils.getInputFromStream(responseEntity2.getContent());
						int statusCode2 = httpResponse2.getStatusLine().getStatusCode();
						if (statusCode2 == SC_OK) {  
								JsonObject parse2 =  JsonParser.parseString(responseData2).getAsJsonObject();
								JsonElement jsonElement2 = parse2.get("users");
								if(StringUtils.isEmpty(responseData2)||jsonElement2==null){
									throw new HttpResponseException(SC_NO_CONTENT, "No users found");
								}
								Iterator<JsonElement> iterator2 = jsonElement2.getAsJsonArray().iterator();
								while(iterator2.hasNext()){
									FPSCIUserAccount account2 = SCIUtils.getFPSCIUserAccount(iterator2);
									scimUsers.add(account2);
							} 
								
							return scimUsers; 
						}  else { 
							String reasonPhrase = StringUtils.isEmpty(responseData2)?httpResponse2.getStatusLine().getReasonPhrase():responseData2;				
							throw new HttpResponseException(statusCode2, reasonPhrase);
						}
					}
					
					//***************************END***************************************************
					
					
				return scimUsers; 
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

}
