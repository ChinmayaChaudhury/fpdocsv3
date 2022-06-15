package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.ntuc.vendorservice.vendoraccountservice.repository.VendorUserRepositoryBean;
import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import org.apache.commons.lang.StringUtils;

import com.ntuc.vendorservice.vendoradminservice.models.RetrieveVendorAdminMessage;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountNotExistsException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAuthorizationGroups;

@Stateless
@LocalBean
public class VendorAdministratorRepositoryBean implements GenericRepositoryBean<VendorAccountAdministrator> {
	@PersistenceContext
	EntityManager em;
	
	@EJB
	private VendorUserRepositoryBean vendorUserBean;
	
	@Override
	public List<VendorAccountAdministrator> findAll() {
		return em.createNamedQuery("DSVendorAdministrator.findAll", VendorAccountAdministrator.class).getResultList();
	}
	
	/**
	 * @return DSVendorAdministrator list
	 * @throws AccountNotExistsException
	 */
	@SuppressWarnings("unchecked")
	public List<VendorAccount> retrieveVendorAdmins() throws AccountNotExistsException{
		try {
//			List<RetrieveVendorAdminMessage> adminResponse = new ArrayList<RetrieveVendorAdminMessage>();
			List<String> userNames = (List<String>) em.createQuery("SELECT a.userName FROM VendorAccountAdministrator a ").getResultList();
			 String tempName = null;
			 CriteriaBuilder cb = em.getCriteriaBuilder();
			 CriteriaQuery<VendorAccount> vendorCb = cb.createQuery(VendorAccount.class);
			 Root<VendorAccount> vendorRoot = vendorCb.from(VendorAccount.class);
			 In<Object> in = cb.in(vendorRoot.get("vendorCode"));
			
			 for (int i = 0; i < userNames.size(); i++) {
				 tempName = userNames.get(i);
				 if(tempName!=null && tempName.contains("@")){
					 tempName = tempName.split("@")[1].trim();
				 }
				 in = in.value(tempName);
			}
			vendorCb.select(vendorRoot).where(in);
//			assignDataForVendor(em.createQuery(vendorCb).getResultList(),adminResponse);
//			return adminResponse;
			return em.createQuery(vendorCb).getResultList();
		} catch (javax.persistence.NoResultException e) {
			throw new AccountNotExistsException();
		}
	}
	
	/**
	 * Frame a Retrieval Object for Vendor Administrator Application
	 * @param vendorList
	 * @return
	 */
	private List<RetrieveVendorAdminMessage> assignDataForVendor(List<VendorAccount> vendorList, List<RetrieveVendorAdminMessage> adminResponse){
		RetrieveVendorAdminMessage element = null;
		VendorAccountAdministrator dsAdmin = null;
		try {
			for (VendorAccount dsVendorAccount : vendorList) {
				element = new RetrieveVendorAdminMessage();
				element.setAccountValidated(dsVendorAccount.isAccountValidated());
				element.setAllocatedQuota(dsVendorAccount.getAllocatedQuota());
				element.setInheritCategoryQuota(dsVendorAccount.isInheritCategoryQuota());
				element.setMobile(dsVendorAccount.getMobile());
				element.setQuotaUtilization(dsVendorAccount.getQuotaUtilization());
				element.setSciAccountID(dsVendorAccount.getSciAccountID());
				element.setSystemAccountStatus(dsVendorAccount.getSystemAccountStatus());
				element.setValidateEmailDomain(dsVendorAccount.isValidateEmailDomain());
				element.setVendorActivatedDate(dsVendorAccount.getVendorActivatedDate());
				element.setVendorAdminEmail(dsVendorAccount.getVendorAdminEmail());
				element.setVendorCategory(dsVendorAccount.getVendorCategory());
				element.setVendorCode(dsVendorAccount.getVendorCode());
				element.setVendorName(dsVendorAccount.getVendorName());
				element.setVendorEmailDomain(dsVendorAccount.getVendorEmailDomain());
				element.setVendorID(dsVendorAccount.getVendorID());
				element.setWork(dsVendorAccount.getWork());
				
				dsAdmin = null;
				if(dsVendorAccount.getSciAccountID()!=null && dsVendorAccount.getSciAccountID().trim().length()>0){
					dsAdmin = findBySciAccountID(dsVendorAccount.getSciAccountID());
				}else if(dsVendorAccount.getVendorAdminEmail()!=null && dsVendorAccount.getVendorAdminEmail().trim().length()>0){
					dsAdmin = findByVendorUserEmail(dsVendorAccount.getVendorAdminEmail());
				}
				if(dsAdmin!=null && dsAdmin.getVendorAdminID() > 0 ){
					element.setSelectedGroups(vendorUserBean.getAssignedGroupsToVendorAdmin(dsAdmin.getVendorAdminID()));
					element.setCreatedBy(dsAdmin.getCreatedBy());
					element.setActivatedBy(dsAdmin.getActivatedBy());
					element.setCreateDateTime(dsAdmin.getCreateDateTime());
				}
				
				adminResponse.add(element);
			}
		} catch (AccountNotExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			adminResponse = null;
		}
		
		return adminResponse;
	}
	
	/**
	 * 
	 * @param sciAccountID
	 * @return
	 * @throws AccountNotExistsException
	 */
	public VendorAccountAdministrator findBySciAccountID(String sciAccountID) throws AccountNotExistsException {
		try{ 
			return em.createNamedQuery("DSVendorAdministrator.findBySciAccountID", VendorAccountAdministrator.class)
				.setParameter("sciAccountID", sciAccountID).getSingleResult();
		}catch (javax.persistence.NoResultException e) {
			throw new AccountNotExistsException();
		}
	}
	public VendorAccountAdministrator findByVendorAdminID(long vendorAdminID) {
		try{ 
			return em.createNamedQuery("DSVendorAdministrator.findByVendorAdminID", VendorAccountAdministrator.class)
				.setParameter("vendorAdminID", vendorAdminID).getSingleResult();
		}catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	public VendorAccountAdministrator findByVendorUserName(String userName) {
		try{ 
			return em.createNamedQuery("DSVendorAdministrator.findByVendorUserName", VendorAccountAdministrator.class)
				.setParameter("userName", userName).getSingleResult();
		}catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	/**
	 * 
	 * @param vendorAdminEmail
	 * @return
	 * @throws AccountNotExistsException
	 */
	public VendorAccountAdministrator findByVendorUserEmail(String vendorAdminEmail) throws AccountNotExistsException {
		try{
		return em.createNamedQuery("DSVendorAdministrator.findByVendorUserEmail", VendorAccountAdministrator.class)
				.setParameter("vendorUserEmail", vendorAdminEmail).getSingleResult();
		}catch (javax.persistence.NoResultException e) {
			throw new AccountNotExistsException();
		}
	}  
	@Override
	public VendorAccountAdministrator add(VendorAccountAdministrator vendorAccountAdministrator) throws AccountManagementException {
		try{
			updateEmail(vendorAccountAdministrator);
			em.persist(vendorAccountAdministrator);
			em.flush();
			return vendorAccountAdministrator;
		}catch (Exception e) {
			DatabaseExceptionsHelper.isUniqueConstraintViolation(e, vendorAccountAdministrator);
			throw new AccountManagementException(e.getMessage());
		}
	}

	public void addAuth(VendorAuthorizationGroups vendAuthGroup) throws AccountManagementException {
		try{
			em.persist(vendAuthGroup);
			em.flush();
		}catch (Exception e) {
			DatabaseExceptionsHelper.isUniqueConstraintViolation(e, vendAuthGroup);
			throw new AccountManagementException(e.getMessage());
		}
	}
	private void updateEmail(VendorAccountAdministrator vendorAccountAdministrator) {
		String email=StringUtils.lowerCase(vendorAccountAdministrator.getVendorUserEmail());
		vendorAccountAdministrator.setVendorUserEmail(email);
	}

	@Override 
	public VendorAccountAdministrator update(VendorAccountAdministrator vendorAccountAdministrator) throws AccountManagementException {
		try {
			updateEmail(vendorAccountAdministrator);
			VendorAccountAdministrator account = em.merge(vendorAccountAdministrator);
			em.flush();
			return account;
		} catch (Exception e) {
			DatabaseExceptionsHelper.isUniqueConstraintViolation(e, vendorAccountAdministrator);
			throw new AccountManagementException(e.getMessage());
		}
	}

	/**
	 * Delete vendor administrator account
	 * 
	 * @param vendorAccountAdministrator
	 */
	public Result delete(VendorAccountAdministrator vendorAccountAdministrator) {
		try {
			VendorAccountAdministrator vendorAdminAccount = em.merge(vendorAccountAdministrator);
			em.remove(vendorAdminAccount);
			em.flush();
			return new Result(ResultType.SUCCESS, findAll());
		} catch (IllegalArgumentException e) {
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}
	

}
