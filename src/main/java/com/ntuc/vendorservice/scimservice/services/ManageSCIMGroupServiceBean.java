package com.ntuc.vendorservice.scimservice.services;
 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFCompositeAuthGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFGroupToCompositeMapping;
import com.ntuc.vendorservice.scimservice.entity.CWFVendorUserGroupMapping;
import com.ntuc.vendorservice.scimservice.models.SCIMUser;
import com.ntuc.vendorservice.scimservice.repository.CWFApplicationGroupRepositoryBean;
import com.ntuc.vendorservice.scimservice.repository.CWFCompositeAuthGroupRepositoryBean;
import com.ntuc.vendorservice.scimservice.repository.CWFGroupToAppMappingRepositoryBean;
import com.ntuc.vendorservice.scimservice.repository.CWFVendorUserGroupMappingRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.services.ManageAccountServiceBean;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import org.apache.http.client.HttpResponseException;

import com.ntuc.vendorservice.foundationcontext.adapter.BTPDestinationServiceAdapter;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.scimservice.models.SCIMGroup;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;

@Stateless 
@LocalBean
public class ManageSCIMGroupServiceBean implements ManageSCIMGroupService {
	@EJB
	protected CWFCompositeAuthGroupRepositoryBean compositeAuthGroupBean;
	@EJB
	protected CWFGroupToAppMappingRepositoryBean groupToAppMappingBean;
	@EJB
	protected CWFApplicationGroupRepositoryBean applicationGroupBean;
	@EJB
	protected ManageAccountServiceBean manageAccountServiceBean;
	@EJB
    protected CWFVendorUserGroupMappingRepositoryBean vendorUserGroupMappingBean;

	protected BTPDestinationServiceAdapter destinationAdapter;

	@PostConstruct
	protected void initializeDestinationAdapter(){
		this.destinationAdapter =new BTPDestinationServiceAdapter();
	}
	@Override
	public CWFCompositeAuthGroup defineCWFCompositeAuthGroup(CWFCompositeAuthGroup compositeAuthGroup) throws AccountManagementException {
		return compositeAuthGroupBean.add(compositeAuthGroup);
	}
	@Override
	public List<CWFApplicationGroup> mapGroupToCompositeGroup(List<CWFApplicationGroup> applicationGroups, CWFCompositeAuthGroup compositeAuthGroup) throws AccountManagementException {
		Iterator<CWFApplicationGroup> iterator = applicationGroups.iterator();
		List<CWFGroupToCompositeMapping> appMappings = groupToAppMappingBean.findByCompositeGroup(compositeAuthGroup.getId());
		while(iterator.hasNext()){
			CWFApplicationGroup applicationGroup = iterator.next();
			if( applicationGroupBean.findByID(applicationGroup.getId())==null){
				applicationGroupBean.add(applicationGroup);
			}  
			if(!groupExistsInComposite(appMappings, applicationGroup)){
				CWFGroupToCompositeMapping cwfGroupToAppMapping=new CWFGroupToCompositeMapping();
				cwfGroupToAppMapping.setCompositeAuthGroup(compositeAuthGroup);
				cwfGroupToAppMapping.setApplicationGroup(applicationGroup);
				groupToAppMappingBean.add(cwfGroupToAppMapping);
			}
		}
		return groupToAppMappingBean.findGroupsByComposition(compositeAuthGroup.getId());
	}
	
	@Override
	public Result provisionUserToCompositeGroups(CWFCompositeAuthGroup compositeAuthGroup, List<SCIMUser> scimUsers, HttpServletRequest request) {
		List<CWFApplicationGroup> groups=new ArrayList<CWFApplicationGroup>(); 
		groups.addAll(groupToAppMappingBean.findGroupsByComposition(compositeAuthGroup.getId())); 
		List<Object> objects = provisionUsersToAuthGroup(scimUsers, request, groups);
		return new Result(ResultType.SUCCESS,objects); 
	}
	public List<Object> provisionUsersToAuthGroup(List<SCIMUser> scimUsers, HttpServletRequest request, List<CWFApplicationGroup> groups) {
		List<SCIMGroup> scimGroups=new ArrayList<SCIMGroup>();
		for(CWFApplicationGroup applicationGroup:groups){
			scimGroups.add(new SCIMGroup(applicationGroup.getValue()));
		}
		List<Object> objects=new ArrayList<Object>();
		for(SCIMUser scimUser:scimUsers){
			Set<SCIMGroup> updatedSCIMGroups = scimUser.getGroups();
			updatedSCIMGroups.addAll(scimGroups);
			SCIMUser updatedSCIMUser = new SCIMUser();
			updatedSCIMUser.setId(scimUser.getId());
			updatedSCIMUser.setGroups(updatedSCIMGroups);
			objects.add(manageAccountServiceBean.updateSAPSCIAccount(updatedSCIMUser, request).getResult());
		}
		return objects;
	}
	
	@Override
	public Set<SCIMGroup> queryAllGroups(HttpServletRequest request) throws AccountManagementException {
		try {
			return SCIGroupManagementServiceImpl.getInstance().requestSCIMGroups(destinationAdapter.getSCIMDestinationHttpClient(request));
		} catch (HttpResponseException | NamingException | DestinationAccessException e) {
			throw new AccountManagementException(e.getMessage());
		}
	}
	
	private boolean groupExistsInComposite(List<CWFGroupToCompositeMapping> appMappings, CWFApplicationGroup applicationGroup) {
		Iterator<CWFGroupToCompositeMapping> mappingIter = appMappings.iterator();
		while(mappingIter.hasNext()){
			CWFGroupToCompositeMapping mapping = mappingIter.next();
			CWFApplicationGroup group = mapping.getApplicationGroup();
			if(group.equals(applicationGroup)){
				return true;
			}
		}
		return false;
	}
	@Override
	public List<CWFApplicationGroup> removeGroupToCompositeGroup(List<CWFApplicationGroup> applicationGroups, CWFCompositeAuthGroup compositeAuthGroup) throws AccountManagementException {
		List<CWFGroupToCompositeMapping> compositeMappings = groupToAppMappingBean.findByCompositeGroup(compositeAuthGroup.getId());
		Iterator<CWFGroupToCompositeMapping> iterator = compositeMappings.iterator();
		while(iterator.hasNext()){
			CWFGroupToCompositeMapping compositeMapping = iterator.next(); 
			if(applicationGroups.contains(compositeMapping.getApplicationGroup())){ 
				groupToAppMappingBean.delete(compositeMapping);
			}
		}
		return groupToAppMappingBean.findGroupsByComposition(compositeAuthGroup.getId());
	}
	@Override
	public List<CWFApplicationGroup> provisionVendorUser(VendorUser vendorUser, List<CWFApplicationGroup> applicationGroups, HttpServletRequest request) throws AccountManagementException {
		Set<SCIMGroup> scimGroups=new HashSet<SCIMGroup>();
		for(CWFApplicationGroup applicationGroup:applicationGroups){
			scimGroups.add(new SCIMGroup(applicationGroup.getValue()));
		}
		Result provisionSCIMUserToGroups = manageAccountServiceBean.provisionSCIMUserToGroups(vendorUser.getSciAccountID(), scimGroups, request);
		if(provisionSCIMUserToGroups.getResultType()!=ResultType.SUCCESS){
			throw new AccountManagementException(provisionSCIMUserToGroups.getResult().toString());
		} 
		
		List<CWFApplicationGroup> groups = vendorUserGroupMappingBean.findGroupsByComposition(vendorUser.getVendorUserID());
		for(CWFApplicationGroup applicationGroup:applicationGroups){
			if(!groups.contains(applicationGroup)){
			CWFVendorUserGroupMapping userGroupMapping=new CWFVendorUserGroupMapping();
			userGroupMapping.setVendorUser(vendorUser);
			userGroupMapping.setApplicationGroup(applicationGroup);
			vendorUserGroupMappingBean.add(userGroupMapping);
			}
		} 
		
		return vendorUserGroupMappingBean.findGroupsByComposition(vendorUser.getVendorUserID());
	}
	public List<CWFApplicationGroup> removeGroupProvisioningOnVendorUser(VendorUser vendorUser, List<CWFApplicationGroup> applicationGroups, HttpServletRequest request) throws AccountManagementException {
		Set<SCIMGroup> scimGroups=new HashSet<SCIMGroup>();
		for(CWFApplicationGroup applicationGroup:applicationGroups){
			scimGroups.add(new SCIMGroup(applicationGroup.getValue()));
		}
		Result result=manageAccountServiceBean.removeFromGroupSAPSCIAccount(vendorUser.getSciAccountID(), scimGroups, request);
		if(result.getResultType()!=ResultType.SUCCESS){
			throw new AccountManagementException(result.getResult().toString());
		} 
		
		List<CWFVendorUserGroupMapping> groupMapping = vendorUserGroupMappingBean.findGroupsByVendorUserID(vendorUser.getVendorUserID());
		for(CWFVendorUserGroupMapping mapping:groupMapping){ 
			if(applicationGroups.contains(mapping.getApplicationGroup())){
				vendorUserGroupMappingBean.delete(mapping);
			}
		} 
		return vendorUserGroupMappingBean.findGroupsByComposition(vendorUser.getVendorUserID());
	}
	
}
