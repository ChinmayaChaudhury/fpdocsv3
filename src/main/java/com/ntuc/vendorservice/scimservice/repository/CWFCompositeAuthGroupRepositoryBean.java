package com.ntuc.vendorservice.scimservice.repository;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.scimservice.entity.CWFCompositeAuthGroup;
import com.ntuc.vendorservice.scimservice.entity.CWFCompositeAuthType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Session Bean implementation class CWFApplicationManagementBean
 */
@Stateless
@LocalBean
public class CWFCompositeAuthGroupRepositoryBean implements GenericRepositoryBean<CWFCompositeAuthGroup> {
	@PersistenceContext
	private EntityManager em;
	/**
	 * List all the available VendorUserGroup
	 * 
	 * @return
	 */
	public List<CWFCompositeAuthGroup> findAll() {
		return em.createNamedQuery("CWFCompositeAuthGroup.findAll", CWFCompositeAuthGroup.class).getResultList();
	}
	public CWFCompositeAuthGroup findByID(Long id) {
		try{
			return em.createNamedQuery("CWFCompositeAuthGroup.findByID", CWFCompositeAuthGroup.class).setParameter("id", id).getSingleResult();
		}catch(javax.persistence.NoResultException e){
			return null;
		}
	}
	public List<CWFCompositeAuthGroup>findByApplicationType(CWFCompositeAuthType applicationType){
		try{
			return em.createNamedQuery("CWFCompositeAuthGroup.findByApplicationType", CWFCompositeAuthGroup.class).setParameter("applicationType", applicationType).getResultList();
		}catch(javax.persistence.NoResultException e){
			return null;
		}
	}
	@Override
	public CWFCompositeAuthGroup add(CWFCompositeAuthGroup e) throws AccountManagementException {
		CWFCompositeAuthType applicationType = e.getApplicationType();
		if(applicationType==CWFCompositeAuthType.ProspectVendor||applicationType==CWFCompositeAuthType.Vendor){
			List<CWFCompositeAuthGroup> compositeAuthGroups = findByApplicationType(applicationType);
			if(compositeAuthGroups!=null&&!compositeAuthGroups.isEmpty()){
				throw new AccountManagementException("Can only have one composite group of type '"+ applicationType+"'");
			}
		}
		em.persist(e);
		em.flush();
		return e;
		
	}
	@Override
	public CWFCompositeAuthGroup update(CWFCompositeAuthGroup e) throws AccountManagementException {
		CWFCompositeAuthType applicationType = e.getApplicationType();
		if(applicationType==CWFCompositeAuthType.ProspectVendor||applicationType==CWFCompositeAuthType.Vendor){
			List<CWFCompositeAuthGroup> compositeAuthGroups = findByApplicationType(applicationType);
			if(!compositeAuthGroups.isEmpty()){ 
				if(!compositeAuthGroups.contains(e)){
					throw new AccountManagementException("Can only have one composite group of type '"+ applicationType+"'");
				} 
			}
		}
		CWFCompositeAuthGroup merge = em.merge(e);
		em.flush();
		return merge;
	}
	/**
	 * @param cwfApplication
	 */
	public Result delete(CWFCompositeAuthGroup cwfApplication) {
		try {
			CWFCompositeAuthGroup application = em.merge(cwfApplication); 
			em.remove(application);
			em.flush();
			return new Result(ResultType.SUCCESS, findAll());
		} catch (IllegalArgumentException e) {
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}
	 
 

}
