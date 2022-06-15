package com.ntuc.vendorservice.scimservice.repository;


import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithObjectSet;
import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFCompositeAuthGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFGroupToCompositeMapping;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Stateless
@LocalBean
public class CWFGroupToAppMappingRepositoryBean implements GenericRepositoryBean<CWFGroupToCompositeMapping> {
	@PersistenceContext
	private EntityManager em;
	@Override
	public List<CWFGroupToCompositeMapping> findAll() { 
		return em.createNamedQuery("CWFGroupToCompositeMapping.findAll", CWFGroupToCompositeMapping.class).getResultList();
	}
	 
	public List<CWFGroupToCompositeMapping> findByCompositeGroup(Long compositeGroupID) {
		CWFCompositeAuthGroup compositeAuthGroup=new CWFCompositeAuthGroup();
		compositeAuthGroup.setId(compositeGroupID);
		return em.createNamedQuery("CWFGroupToCompositeMapping.findByApplication", CWFGroupToCompositeMapping.class)
				.setParameter("compositeAuthGroup", compositeAuthGroup).getResultList();
	} 
	
	public CWFGroupToCompositeMapping findByApplicationGroup(String groupID) { 
		try{
			CWFApplicationGroup applicationGroup=new CWFApplicationGroup();
			applicationGroup.setId(groupID);
		    return em.createNamedQuery("CWFGroupToCompositeMapping.findByApplicationGroup", CWFGroupToCompositeMapping.class)
				.setParameter("applicationGroup", applicationGroup).getSingleResult();
		}catch(javax.persistence.NoResultException e){
			return null;
		}
	} 
	public List<CWFApplicationGroup> findGroupsByComposition(Long compositeGroupID) {
		CWFCompositeAuthGroup compositeAuthGroup=new CWFCompositeAuthGroup();
		compositeAuthGroup.setId(compositeGroupID);
		return em.createNamedQuery("CWFGroupToCompositeMapping.findGroupsByComposition", CWFApplicationGroup.class)
				.setParameter("compositeAuthGroup", compositeAuthGroup).getResultList();
	}
	
	
	@Override
	public CWFGroupToCompositeMapping add(CWFGroupToCompositeMapping cwfGroupToAppMapping) throws AccountManagementException {
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
	public CWFGroupToCompositeMapping update(CWFGroupToCompositeMapping e) throws AccountManagementException {
		CWFGroupToCompositeMapping merge = em.merge(e); 
		em.flush();
		return merge;
	}
	/**
	 * 
	 * @param compositeMapping
	 * @return
	 */
	public ResultWithObjectSet delete(CWFGroupToCompositeMapping compositeMapping) {
		try{ 
			CWFGroupToCompositeMapping merge = em.merge(compositeMapping);
			em.remove(merge);
			em.flush();
			return new ResultWithObjectSet(ResultType.SUCCESS, "Deletion Successful");
		}catch(IllegalArgumentException e){
			return new ResultWithObjectSet(ResultType.ERROR, e.getMessage());
		} 
	} 

}
