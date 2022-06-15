package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountNotExistsException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithObjectSet;
import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorToAdminMapping;

@Stateless
@LocalBean
public class VendorToAdminMappingRepositoryBean implements GenericRepositoryBean<VendorToAdminMapping> {
	@PersistenceContext
	private EntityManager em;
	@Override
	public List<VendorToAdminMapping> findAll() { 
		return em.createNamedQuery("VendorToAdminMapping.findAll", VendorToAdminMapping.class).getResultList();
	}
	 
	public List<VendorToAdminMapping> findByVendorAdministrator(long vendorAdminID) {
		VendorAccountAdministrator vendorAccountAdministrator =new VendorAccountAdministrator();
		vendorAccountAdministrator.setVendorAdminID(vendorAdminID);
		return em.createNamedQuery("VendorToAdminMapping.findByVendorAdministrator", VendorToAdminMapping.class)
				.setParameter("vendorAdministrator", vendorAccountAdministrator).getResultList();
	} 
	
	public VendorToAdminMapping findByVendorAccount(long vendorID) { 
		try{
		VendorAccount vendorAccount=new VendorAccount();
		vendorAccount.setVendorID(vendorID);
		return em.createNamedQuery("VendorToAdminMapping.findByVendorAccount", VendorToAdminMapping.class)
				.setParameter("vendorAccount", vendorAccount).getSingleResult();
		}catch(javax.persistence.NoResultException e){
			return null;
		}
	} 
	
	/**
	 * @return Long list
	 * @throws AccountNotExistsException
	 */
	public List<VendorAccount> findAllVendorAccounts(VendorAccountAdministrator vendorAdmin, Long vendorID) throws AccountNotExistsException{
		try {
			@SuppressWarnings("unchecked")
			List<VendorAccount> vendorAccountsList = (List<VendorAccount>) em.createQuery("SELECT a.vendorAccount FROM VendorToAdminMapping a where a.vendorAccountAdministrator.vendorAdminID = "+vendorAdmin.getVendorAdminID()).getResultList();
			return vendorAccountsList;
		} catch (javax.persistence.NoResultException e) {
			throw new AccountNotExistsException();
		}
	}
	
	@Override
	public VendorToAdminMapping add(VendorToAdminMapping userToVendorSubscription) throws AccountManagementException {
		try{
			em.persist(userToVendorSubscription);
			em.flush();
			return userToVendorSubscription;
		}catch(Exception e){  
			 DatabaseExceptionsHelper.isUniqueConstraintViolation(e);
			 throw new AccountManagementException(e.getMessage());
		} 
	}

	@Override
	public VendorToAdminMapping update(VendorToAdminMapping e) throws AccountManagementException {
		VendorToAdminMapping merge = em.merge(e); 
		em.flush();
		return merge;
	}
	/**
	 * 
	 * @param vendorToAdminMapping
	 * @return
	 */
	public ResultWithObjectSet delete(VendorToAdminMapping vendorToAdminMapping) {
		try{ 
			VendorToAdminMapping merge = em.merge(vendorToAdminMapping);
			em.remove(merge);
			em.flush();
			return new ResultWithObjectSet(ResultType.SUCCESS, "Deletion Successful");
		}catch(IllegalArgumentException e){
			return new ResultWithObjectSet(ResultType.ERROR, e.getMessage());
		} 
	} 

}
