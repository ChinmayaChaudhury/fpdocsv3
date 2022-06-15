package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.ResultWithObjectSet;
import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.FPUserToVendorSubscription;

@Stateless
@LocalBean
public class FPUserToVendorSubscriptionRepositoryBean implements GenericRepositoryBean<FPUserToVendorSubscription> {
	@PersistenceContext
	EntityManager em;
	@Override
	public List<FPUserToVendorSubscription> findAll() { 
		return em.createNamedQuery("FPUserToVendorSubscription.findAll", FPUserToVendorSubscription.class).getResultList();
	}
	 
	public List<FPUserToVendorSubscription> findByFairPriceUserID(long fairPriceUserID) {
		InternalUser fairPriceUser=new InternalUser();
		fairPriceUser.setFairPriceUserID(fairPriceUserID);
		return em.createNamedQuery("FPUserToVendorSubscription.findByFairPriceUserID", FPUserToVendorSubscription.class)
				.setParameter("fairPriceUserID", fairPriceUser).getResultList();
	} 
	
	public List<FPUserToVendorSubscription> findByVendorID(long vendorID) { 
		VendorAccount vendorAccount=new VendorAccount();
		vendorAccount.setVendorID(vendorID);
		return em.createNamedQuery("FPUserToVendorSubscription.findByVendorID", FPUserToVendorSubscription.class)
				.setParameter("vendorID", vendorAccount).getResultList();
	} 
	public List<InternalUser> findByFairPriceUserByVendorID(long vendorID) {
		VendorAccount vendorAccount=new VendorAccount();
		vendorAccount.setVendorID(vendorID);
		return em.createNamedQuery("FPUserToVendorSubscription.findByFairPriceUserByVendorID", InternalUser.class)
				.setParameter("vendorID", vendorAccount).getResultList();
	} 
	@Override
	public FPUserToVendorSubscription add(FPUserToVendorSubscription userToVendorSubscription) throws AccountManagementException {
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
	public FPUserToVendorSubscription update(FPUserToVendorSubscription e) throws AccountManagementException {
		FPUserToVendorSubscription merge = em.merge(e); 
		em.flush();
		return merge;
	}
	/**
	 * 
	 * @param userToVendorSubscription
	 * @return
	 */
	public ResultWithObjectSet delete(FPUserToVendorSubscription userToVendorSubscription) {
		try{ 
			FPUserToVendorSubscription merge = em.merge(userToVendorSubscription);
			em.remove(merge);
			em.flush();
			return new ResultWithObjectSet(ResultType.SUCCESS, findByFairPriceUserID(userToVendorSubscription.getFairPriceUserID().getFairPriceUserID()));
		}catch(IllegalArgumentException e){
			return new ResultWithObjectSet(ResultType.ERROR, e.getMessage());
		} 
	} 

}
