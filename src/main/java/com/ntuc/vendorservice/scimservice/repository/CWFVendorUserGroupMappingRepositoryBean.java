package com.ntuc.vendorservice.scimservice.repository;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithObjectSet;
import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFVendorUserGroupMapping;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
@LocalBean
public class CWFVendorUserGroupMappingRepositoryBean implements GenericRepositoryBean<CWFVendorUserGroupMapping> {
	@PersistenceContext
	private EntityManager em;
	@Override
	public List<CWFVendorUserGroupMapping> findAll() { 
		return em.createNamedQuery("CWFVendorUserGroupMapping.findAll", CWFVendorUserGroupMapping.class).getResultList();
	}
	 
	public CWFVendorUserGroupMapping findByApplicationGroup(String groupID) { 
		try{
			CWFApplicationGroup applicationGroup=new CWFApplicationGroup();
			applicationGroup.setId(groupID);
		    return em.createNamedQuery("CWFVendorUserGroupMapping.findByApplicationGroup", CWFVendorUserGroupMapping.class)
				.setParameter("applicationGroup", applicationGroup).getSingleResult();
		}catch(javax.persistence.NoResultException e){
			return null;
		}
	} 
	public List<CWFApplicationGroup> findGroupsByComposition(Long vendorUserID) {
		final VendorUser vendorUser=new VendorUser();
		vendorUser.setVendorUserID(vendorUserID);
		return em.createNamedQuery("CWFVendorUserGroupMapping.findGroupsByVendorUser", CWFApplicationGroup.class)
				.setParameter("vendorUser", vendorUser).getResultList();
	}
	public List<CWFVendorUserGroupMapping> findGroupsByVendorUserID(Long vendorUserID) {
		final VendorUser vendorUser=new VendorUser();
		vendorUser.setVendorUserID(vendorUserID);
		return em.createNamedQuery("CWFVendorUserGroupMapping.findGroupMappingByVendorUser", CWFVendorUserGroupMapping.class)
				.setParameter("vendorUser", vendorUser).getResultList();
	}
	
	
	@Override
	public CWFVendorUserGroupMapping add(CWFVendorUserGroupMapping cwfGroupToAppMapping) throws AccountManagementException {
		try{
			em.persist(cwfGroupToAppMapping);
			em.flush();
			return cwfGroupToAppMapping;
		}catch(Exception e){  
			 DatabaseExceptionsHelper.isUniqueConstraintViolation(e);
			 throw new AccountManagementException(e.getMessage());
		} 
	}

	@Override
	public CWFVendorUserGroupMapping update(CWFVendorUserGroupMapping e) throws AccountManagementException {
		CWFVendorUserGroupMapping merge = em.merge(e); 
		em.flush();
		return merge;
	}
	/**
	 * 
	 * @param compositeMapping
	 * @return
	 */
	public ResultWithObjectSet delete(CWFVendorUserGroupMapping compositeMapping) {
		try{ 
			CWFVendorUserGroupMapping merge = em.merge(compositeMapping);
			em.remove(merge);
			em.flush();
			return new ResultWithObjectSet(ResultType.SUCCESS, "Deletion Successful");
		}catch(IllegalArgumentException e){
			return new ResultWithObjectSet(ResultType.ERROR, e.getMessage());
		} 
	} 

}
