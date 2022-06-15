package com.ntuc.vendorservice.scimservice.services;

import java.util.List;
import java.util.Set;

import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import org.apache.http.client.HttpResponseException;


/**
 * SCI Group Management Service
 * @author I305675
 *
 */
public interface SCIGroupManagementService extends BaseSCIManagementService {
	String GROUP_SCIM_GET_SPECIFIC="um/";
	String GROUP_SCIM_GET_ALL="groups";
 
	/**
	 * Request SCIM Groups
	 * @param httpDestination
	 * @return
	 * @throws HttpResponseException
	 */
	Set<SCIMGroup>requestSCIMGroups(HttpDestination httpDestination) throws HttpResponseException;
	
	
	/**
	 * Get users in group
	 * @param groupName
	 * @param httpDestination
	 * @return
	 */
	List<FPSCIUserAccount> getUsersInGroup(String groupName, HttpDestination httpDestination)throws HttpResponseException;

}
