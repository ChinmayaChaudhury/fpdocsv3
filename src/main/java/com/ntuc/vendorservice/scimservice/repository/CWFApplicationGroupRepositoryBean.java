package com.ntuc.vendorservice.scimservice.repository;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Session Bean implementation class CWFApplicationGroupManagementBean
 */
@Stateless
@LocalBean
public class CWFApplicationGroupRepositoryBean implements GenericRepositoryBean<CWFApplicationGroup> {
	@PersistenceContext
	private EntityManager em;
	/**
	 * List all the available VendorUserGroup
	 * 
	 * @return
	 */
	public List<CWFApplicationGroup> findAll() {
		return em.createNamedQuery("CWFApplicationGroup.findAll", CWFApplicationGroup.class).getResultList();
	}
	public CWFApplicationGroup findByID(String id) {
		try{
			return em.createNamedQuery("CWFApplicationGroup.findByID", CWFApplicationGroup.class).setParameter("id", id).getSingleResult();
		}catch(javax.persistence.NoResultException e){
			return null;
		}
	} 
	@Override
	public CWFApplicationGroup add(CWFApplicationGroup e) throws AccountManagementException {
		em.persist(e);
		em.flush();
		return e;
	}
	@Override
	public CWFApplicationGroup update(CWFApplicationGroup e) throws AccountManagementException {
		CWFApplicationGroup merge = em.merge(e);
		em.flush();
		return merge;
	}
	/**
	 * @param applicationGroup
	 */
	public Result delete(CWFApplicationGroup applicationGroup) {
		try {
			CWFApplicationGroup cwfApplicationGroup = em.merge(applicationGroup); 
			em.remove(cwfApplicationGroup);
			em.flush();
			return new Result(ResultType.SUCCESS, findAll());
		} catch (IllegalArgumentException e) {
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}
	 
 

}
