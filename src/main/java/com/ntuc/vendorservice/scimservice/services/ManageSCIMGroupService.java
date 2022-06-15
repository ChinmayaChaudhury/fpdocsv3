package com.ntuc.vendorservice.scimservice.services;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFCompositeAuthGroup;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;

/**
 * Manage Application SCIM Groups
 * @author I305675
 *
 */
public interface ManageSCIMGroupService {
	/**
	 * Define CWF Applications
	 * @param cwfApplication
	 * @return
	 * @throws AccountManagementException
	 */
	CWFCompositeAuthGroup defineCWFCompositeAuthGroup(CWFCompositeAuthGroup cwfApplication) throws AccountManagementException;
	
	
	List<CWFApplicationGroup> provisionVendorUser(VendorUser vendorUser, List<CWFApplicationGroup> applicationGroups, HttpServletRequest request) throws AccountManagementException;
	/**
	 * Remove groups from composite
	 *
	 * @param applicationGroups
	 * @param compositeAuthGroup
	 * @return
	 * @throws AccountManagementException
	 */
	List<CWFApplicationGroup> removeGroupToCompositeGroup(List<CWFApplicationGroup> applicationGroups, CWFCompositeAuthGroup compositeAuthGroup) throws AccountManagementException;
	/**
	 * Map Applications to the relevant role groups
	 *
	 * @param applicationGroups
	 * @param compositeAuthGroup
	 * @return
	 * @throws AccountManagementException
	 */
	List<CWFApplicationGroup> mapGroupToCompositeGroup(List<CWFApplicationGroup> applicationGroups, CWFCompositeAuthGroup compositeAuthGroup) throws AccountManagementException;

    /**
     * Provision user to applications
     * @param compositeAuthGroups
     * @param scimUser
     * @param request
     * @return
     */
	Result provisionUserToCompositeGroups(CWFCompositeAuthGroup compositeAuthGroup, List<SCIMUser> scimUsers, HttpServletRequest request);
	/**
	 * Query all groups
	 * @param request
	 * @return
	 * @throws AccountManagementException
	 */
	Set<SCIMGroup> queryAllGroups(HttpServletRequest request) throws AccountManagementException;

}
