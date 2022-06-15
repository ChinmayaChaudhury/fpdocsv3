package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import org.apache.commons.lang.StringUtils;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.FairPriceUserGroup;

/**
 * Session Bean implementation class FairPriceUserGroupBean
 */
@Stateless
@LocalBean
public class FairPriceUserGroupRepositoryBean {
	@PersistenceContext
	private EntityManager em;
	/**
	 * List all the available quota allocations
	 * 
	 * @return
	 */
	public List<FairPriceUserGroup> findAllFairPriceUserGroups() {
		return em.createNamedQuery("FairPriceUserGroup.findAll", FairPriceUserGroup.class).getResultList();
	}

	/**
	 * Create a new Quota for the specified vendor categories
	 * 
	 * @param fairPriceUserGroup
	 */
	public void addFairPriceUserGroup(FairPriceUserGroup fairPriceUserGroup) {
		em.persist(fairPriceUserGroup);
		em.flush();
	}

	/**
	 * find all the internal user groups
	 * 
	 * @return
	 */
	public List<InternalUser> findAllInternalUser() {
		return em.createNamedQuery("FairPriceUser.findAll", InternalUser.class).getResultList();
	}

	/**
	 * find all the internal user groups
	 * 
	 * @return
	 */
	public InternalUser addFairPriceUser(InternalUser fairPriceUser) throws AccountManagementException {
		if(findByUserEmail(fairPriceUser.getEmailAddress())!=null){
			throw new AccountManagementException("Email is already in use");
		}
		updateEmail(fairPriceUser);
		em.persist(fairPriceUser);
		em.flush();
		return fairPriceUser;
	}

	private void updateEmail(InternalUser fairPriceUser) {
		String fairPriceEmail = StringUtils.lowerCase(fairPriceUser.getFairPriceEmail());
		fairPriceUser.setFairPriceEmail(fairPriceEmail);
	}

	public InternalUser findByUserEmail(String fairPriceEmail) {
		try {
			return em.createNamedQuery("FairPriceUser.findByUserEmail", InternalUser.class)
					.setParameter("fairPriceEmail", fairPriceEmail).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	public InternalUser findBySCIAccountID(String sciAccountID) {
		try {
			return em.createNamedQuery("FairPriceUser.findBySCIAccountID", InternalUser.class)
					.setParameter("sciAccountID", sciAccountID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	public InternalUser findByID(Long fairPriceUserID) {
		try {
			return em.createNamedQuery("FairPriceUser.findByID", InternalUser.class)
					.setParameter("fairPriceUserID", fairPriceUserID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public boolean validateUserAccount(InternalUser fairPriceUser) {
		InternalUser merge = em.merge(fairPriceUser);
		em.merge(merge); 
		return merge.isAccountValidated();
	}

	public int updateGroup(FairPriceUserGroup fairPriceUserGroup) {
		try {
			Query query = em.createQuery(
					"UPDATE FairPriceUserGroup v SET "
					+ "v.fairPriceUserGroupName =:fairPriceUserGroupName , "
					+ "v.fairPriceUserGroupDesc =:fairPriceUserGroupDesc  WHERE v.fairPriceGroupID=:fairPriceGroupID");
			query.setParameter("fairPriceUserGroupName", fairPriceUserGroup.getFairPriceUserGroupName());
			query.setParameter("fairPriceUserGroupDesc", fairPriceUserGroup.getFairPriceUserGroupDesc()); 
			query.setParameter("fairPriceGroupID", fairPriceUserGroup.getFairPriceGroupID()); 
			return query.executeUpdate();
		} catch (javax.persistence.NoResultException e) {
			return 0;
		}
		
	}

	public InternalUser updateFairPriceUser(InternalUser fairPriceUser) throws AccountManagementException {
			InternalUser account = findByID(fairPriceUser.getFairPriceUserID());
			if(!fairPriceUser.getEmailAddress().equals(account.getEmailAddress())){
				if(findByUserEmail(fairPriceUser.getEmailAddress())!=null){
					throw new AccountManagementException("Email is already in use");
				}
			} 
			try{ 
				InternalUser merge = em.merge(fairPriceUser);
				em.merge(merge); 
				return merge;
			}catch(IllegalArgumentException e){
				DatabaseExceptionsHelper.isUniqueConstraintViolation(e, fairPriceUser);
				throw new AccountManagementException(e.getMessage());
			}
	}

	public Result delete(InternalUser fairPriceUser) {
		try{ 
			InternalUser merge = em.merge(fairPriceUser);
			em.remove(merge);
			em.flush();
			return new Result(ResultType.SUCCESS, findAllInternalUser());
		}catch(IllegalArgumentException e){
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}
	public Result update(InternalUser fairPriceUser) {
		try{ 
			updateEmail(fairPriceUser);
			InternalUser merge = em.merge(fairPriceUser);
			em.merge(merge); 
			return new Result(ResultType.SUCCESS, findAllInternalUser());
		}catch(IllegalArgumentException e){
			return new Result(ResultType.ERROR, e.getMessage());
		}
	} 
}
