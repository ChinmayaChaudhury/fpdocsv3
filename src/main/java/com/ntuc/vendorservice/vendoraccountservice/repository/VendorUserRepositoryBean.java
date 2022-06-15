package com.ntuc.vendorservice.vendoraccountservice.repository;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import org.apache.commons.lang.StringUtils;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAuthorizationGroups;

@Stateless
@LocalBean
public class VendorUserRepositoryBean implements GenericRepositoryBean<VendorUser> {
	@PersistenceContext
	EntityManager em;
	@Override
	public List<VendorUser> findAll() {
		return em.createNamedQuery("DSVendorUser.findAll", VendorUser.class).getResultList();
	}

	@Override
	public VendorUser add(VendorUser vendorUser) throws AccountManagementException {
		if(findByVendorUserEmail(vendorUser.getEmailAddress())!=null){
			throw new AccountManagementException("Email is already in use");
		}
		if(findAllByVendorID(vendorUser.getVendorID()).size()==20){
			throw new AccountManagementException("20 user accounts are allowed");
		}
		updateEmail(vendorUser);
		em.persist(vendorUser);
		em.flush();
		return vendorUser;
	}

	private void updateEmail(VendorUser vendorUser) {
		String email=StringUtils.lowerCase(vendorUser.getEmailAddress());
		vendorUser.setVendorUserEmail(email);
	}

	@Override
	public VendorUser update(VendorUser vendorUser) throws AccountManagementException {
		VendorAccount vendorAccount=new VendorAccount();
		vendorAccount.setVendorID(vendorUser.getVendorID());
		VendorUser user = findByVendorUserIDAndVendorID(vendorAccount, vendorUser.getVendorUserID());
		if(!vendorUser.getEmailAddress().equals(user.getEmailAddress())){
			if(findByVendorUserEmail(vendorUser.getEmailAddress())!=null){
				throw new AccountManagementException("Email is already in use");
			}
		} 
		try{ 
			updateEmail(vendorUser);
			VendorUser merge = em.merge(vendorUser);
			em.merge(merge); 
			return merge;
		}catch(IllegalArgumentException e){
			DatabaseExceptionsHelper.isUniqueConstraintViolation(e, vendorUser);
			throw new AccountManagementException(e.getMessage());
		}
	}
	
	public List<VendorUser> findAllByVendorIDAndVendorUserGroup(long vendorID, long vendorGroupID) {
		VendorAccount vendorAccount =new VendorAccount();
		vendorAccount.setVendorID(vendorID); 
		return em.createNamedQuery("DSVendorUser.findAllByVendorIDAndVendorUserGroup", VendorUser.class)
				.setParameter("vendorAccount", vendorAccount).setParameter("vendorGroupID", vendorGroupID).getResultList();
	}

	public List<VendorUser> findAllByVendorAccount(VendorAccount vendorAccount) {
		List<VendorUser> resultList = em.createNamedQuery("DSVendorUser.findAllByVendorID", VendorUser.class).setParameter("vendorAccount", vendorAccount)
				.getResultList();
		for(VendorUser vendorUser:resultList){
			vendorUser.setVendorCode(vendorAccount.getVendorCode());
		}
		return resultList;
	}
	
	/**
	 * @param type
	 * @return 
	 */
	public List<VendorAuthorizationGroups> getAssignedGroupsToVendor(Long vendorUserID) { 
			return em.createNamedQuery("VendorAuthorizationGroups.manageAccessOnVendorUser", VendorAuthorizationGroups.class)
					.setParameter("vendorUserID", vendorUserID).getResultList();
	}
	
	/**
	 * @param type
	 * @return 
	 */
	public List<String> getAssignedGroupsToVendorAdmin(Long vendorAdminID) { 
		List<String> tempAssignment = new ArrayList<String>();
		List<VendorAuthorizationGroups> adminAssignments =  em.createNamedQuery("VendorAuthorizationGroups.manageAccessOnVendorAdmin", VendorAuthorizationGroups.class)
					.setParameter("vendorAdminID", vendorAdminID).getResultList();
		for (VendorAuthorizationGroups authGrp : adminAssignments) {
			tempAssignment.add(authGrp.getGroupName());
		}
		return tempAssignment;
	}
	
	public List<VendorUser> findAllByVendorID(Long vendorID) {
		VendorAccount vendorAccount =new VendorAccount();
		vendorAccount.setVendorID(vendorID); 
		List<VendorUser> resultList = em.createNamedQuery("DSVendorUser.findAllByVendorID", VendorUser.class).setParameter("vendorAccount", vendorAccount)
				.getResultList(); 
		return resultList;
	}
	/**
	 * 
	 * @param sciAccountID
	 * @return
	 */
	public VendorUser findByVendorSCIUserID(String sciAccountID) {
		try {
			return em.createNamedQuery("DSVendorUser.findBySciAccountID", VendorUser.class)
					.setParameter("sciAccountID", sciAccountID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	/**
	 * 
	 * @param vendorUserID
	 * @return
	 */
	public VendorUser findByUserID(Long vendorUserID) {
		try {
			return em.createNamedQuery("DSVendorUser.findByVendorUserID", VendorUser.class)
					.setParameter("vendorUserID", vendorUserID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public VendorUser findByVendorUserEmail(String vendorUserEmail) {
		try {
			return em.createNamedQuery("DSVendorUser.findByVendorUserEmail", VendorUser.class)
					.setParameter("vendorUserEmail", vendorUserEmail).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	public VendorUser findByVendorUserEmailAndVendorID(long vendorID, String vendorUserEmail) {
		try {
			VendorAccount vendorAccount =new VendorAccount();
			vendorAccount.setVendorID(vendorID); 
			return em.createNamedQuery("DSVendorUser.findByVendorUserEmailAndVendorID", VendorUser.class)
					.setParameter("vendorUserEmail", vendorUserEmail)
					.setParameter("vendorAccount", vendorAccount).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	/**
	 * 
	 * @param vendorID
	 * @param vendorUserID
	 * @return
	 */
	public VendorUser findByVendorUserIDAndVendorID(VendorAccount vendorAccount, long vendorUserID) {
		try { 
			return em.createNamedQuery("DSVendorUser.findByVendorUserIDAndVendorID", VendorUser.class)
					.setParameter("vendorUserID", vendorUserID)
					.setParameter("vendorAccount", vendorAccount).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	

	public boolean validateVendorUserAccount(VendorUser vendorUser) {
		VendorUser merge = em.merge(vendorUser);
		em.flush();
		return merge.isAccountValidated();
	}
	/**
	 * 
	 * @param vendorUser
	 * @return
	 */
	public Result delete(VendorUser vendorUser) {
		try{ 
			VendorUser merge = em.merge(vendorUser);
			em.remove(merge);
			em.flush();
			return new Result(ResultType.SUCCESS, findAllByVendorID(vendorUser.getVendorID()));
		}catch(IllegalArgumentException e){
			return new Result(ResultType.ERROR, e.getMessage());
		} 
	} 
	

}
