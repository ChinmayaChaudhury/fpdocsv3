package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.foundationcontext.repository.PagingList;
import com.ntuc.vendorservice.scimservice.entity.ApplicationGroupsLookup;
import org.apache.commons.lang.StringUtils;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAuthorizationGroups;

/**
 * Session Bean implementation class VendorUserGroupBean
 */
@Stateless
@LocalBean
public class VendorAccountRepositoryBean implements GenericRepositoryBean<VendorAccount> {
	@PersistenceContext
	EntityManager em;
	/**
	 * List all the available VendorAccount
	 * 
	 * @return
	 */
	public List<VendorAccount> findAll() {
		TypedQuery<VendorAccount> typedQuery = em.createNamedQuery("DSVendorAccount.findAll", VendorAccount.class);
		return typedQuery.getResultList();
	}
	
	public Map<String,Object> pageVendorDocuments(int pageNumb) {
		TypedQuery<VendorAccount> typedQuery = em.createNamedQuery("DSVendorAccount.findAll", VendorAccount.class);
		PagingList<VendorAccount> accounts = new PagingList<VendorAccount>(typedQuery,3);
		HashMap<String, Object> hashMap=new HashMap<String, Object>(); 
		hashMap.put(PagingList.CURRENT_PAGE, pageNumb); 
		hashMap.put(PagingList.COLLECTION, accounts.getPage(pageNumb));
		hashMap.put(PagingList.TOTAL_PAGES, accounts.getPages().length);
		return hashMap;
	}
	/**
	 * 
	 * @param vendorName
	 * @return
	 */
	public List<VendorAccount>  findByVendorName(String vendorName) {
		return em.createQuery("Select q from VendorAccount q where q.vendorName LIKE '%"+vendorName+"%'", VendorAccount.class)
				 .getResultList();
	}
    /**
     * Find VendorAccount by email address
     * @param vendorEmailAddress
     * @return
     */
	public List<VendorAccount> findByVendorAdminEmail(String vendorEmailAddress) {
			return em.createNamedQuery("DSVendorAccount.findByVendorAdminEmail", VendorAccount.class)
					.setParameter("vendorAdminEmail", vendorEmailAddress).getResultList(); 

	}
	/**
	 * @param type
	 * @return ApplicationGroupsLookup
	 */
	public List<ApplicationGroupsLookup> findGroupsByType(String type) { 
			return em.createNamedQuery("ApplicationGroupsLookup.findGroupsByType", ApplicationGroupsLookup.class)
					.setParameter("type", type).getResultList(); 
	}
	
	/**
	 * @param groupName
	 * @return
	 */
	public String getGroupDesc(String groupName){
		ApplicationGroupsLookup grpLk = em.createNamedQuery("ApplicationGroupsLookup.getGroupDesc", ApplicationGroupsLookup.class)
				.setParameter("groupName", groupName.trim()).getSingleResult(); 
		return grpLk.getGroupDesc();
	}
	
	/**
	 * Method to retrieve all Assignments pertaining to All Vendor Administrators
	 * @return
	 */
	public List<VendorAuthorizationGroups> getAdminGroupAssignments(Long vendorAdminId,String authGroup) { 
		return em.createNamedQuery("VendorAuthorizationGroups.getAdminGroupAssignments", VendorAuthorizationGroups.class)
				.setParameter("vendorAdminID", vendorAdminId).setParameter("adminGrpName", authGroup).getResultList();
	}
	

	/**
	 *
	 * @param id
	 * @param vendorCode
	 * @param groupName
	 * @param isVendorAdmin
	 * @return
	 */
	public VendorAuthorizationGroups findGroupsByUserNVendorCode(Long id, String vendorCode,String groupName,boolean isVendorAdmin) { 
		String queryToExec = (isVendorAdmin) ? "getAssignmentsOnVendorAdmin" : "getAssignmentsOnVendorUser";
		Query qry = em.createNamedQuery("VendorAuthorizationGroups."+queryToExec,VendorAuthorizationGroups.class);
		if(isVendorAdmin){
			qry.setParameter("vendorAdminID",id);
		}else{
			qry.setParameter("vendorUserID",id);
		}
		qry.setParameter("vendorCode", vendorCode);
		qry.setParameter("groupName", groupName);
		try {
			return (VendorAuthorizationGroups) qry.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	/**
	 * @param type
	 * @return 
	 */
	public List<VendorAuthorizationGroups> getDistinctVendors(Long vendorAdminID) { 
			return em.createNamedQuery("VendorAuthorizationGroups.getDistinctVendors", VendorAuthorizationGroups.class)
					.setParameter("vendorAdminID", vendorAdminID).getResultList(); 
	}
	/**
	 * 
	 * @param vendorID
	 * @return
	 */
	public VendorAccount findByID (Long vendorID) {
		try {
			return em.createNamedQuery("DSVendorAccount.findByID", VendorAccount.class)
					.setParameter("vendorID", vendorID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}

	}
	/**
	 * 
	 * @param sciAccountID
	 * @return
	 */
	public VendorAccount findBySciId (String sciAccountID) {
		try {
			return em.createNamedQuery("DSVendorAccount.findBySciAccountID", VendorAccount.class)
					.setParameter("sciAccountID", sciAccountID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}

	}
	/**
	 * 
	 * @param vendorCode
	 * @return
	 */
	public VendorAccount findByVendorCode (String vendorCode) {
		try {
			return em.createNamedQuery("DSVendorAccount.findByVendorCode", VendorAccount.class)
					.setParameter("vendorCode", vendorCode).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}

	}
	public boolean validateVendorAccount(VendorAccount vendorAccount) {
		VendorAccount account=em.merge(vendorAccount);
		em.flush();
		return account.isAccountValidated(); 
	}

	/**
	 * Create new vendor account
	 * 
	 * @param vendorAccount
	 */
	public VendorAccount add(VendorAccount vendorAccount) throws AccountManagementException {
		try{
			String email=StringUtils.lowerCase(vendorAccount.getVendorAdminEmail());
			vendorAccount.setVendorAdminEmail(email);
			em.persist(vendorAccount);
			em.flush();
			return vendorAccount;
		}catch(Exception e){
			throw new AccountManagementException(e.getMessage());
		}
	}
	/**
	 * @param vendAuthGroup
	 * @throws AccountManagementException
	 */
	public void addAuth(VendorAuthorizationGroups vendAuthGroup) throws AccountManagementException {
		try{
			if(vendAuthGroup.getId()>0){
				em.merge(vendAuthGroup);
			}else{
				em.persist(vendAuthGroup);
			}
			em.flush();
		}catch (Exception e) {
			DatabaseExceptionsHelper.isUniqueConstraintViolation(e, vendAuthGroup);
			throw new AccountManagementException(e.getMessage());
		}
	}
	/**
	 *
	 * @param vendorId
	 * @param isVendorAdmin
	 */
	public void deleteAuth(Long vendorId,boolean isVendorAdmin){
		String tempQry = (isVendorAdmin) ? "deleteAuthorizationForVendorAdmin" : "deleteAuthorizationForVendorUser";
		Query pStmt = em.createNamedQuery("VendorAuthorizationGroups."+tempQry, VendorAuthorizationGroups.class);
		pStmt.setParameter((isVendorAdmin) ? "vendorAdminID" : "vendorUserID", Long.valueOf(vendorId));
		pStmt.executeUpdate();
	}

	/**
	 * Delete vendor account
	 * 
	 * @param vendorAccount
	 */
	public Result delete(VendorAccount vendorAccount)  {
		try{ 
			VendorAccount account = em.merge(vendorAccount);
			em.remove(account);
			em.flush();
			return new Result(ResultType.SUCCESS, findAll());
		}catch(IllegalArgumentException e){
			return new Result(ResultType.ERROR, e.getMessage());
		} 
	}
	/**
	 * 
	 * @param vendorAccount
	 * @return
	 */
	public VendorAccount update(VendorAccount vendorAccount) throws AccountManagementException {
		try{
			String email=StringUtils.lowerCase(vendorAccount.getVendorAdminEmail());
			vendorAccount.setVendorAdminEmail(email);
		 VendorAccount account=em.merge(vendorAccount);
		 em.flush();
		 return account;
		}catch(Exception e){  
			DatabaseExceptionsHelper.isUniqueConstraintViolation(e,vendorAccount); 
			throw new AccountManagementException(e.getMessage());
		} 
	}
	

}
