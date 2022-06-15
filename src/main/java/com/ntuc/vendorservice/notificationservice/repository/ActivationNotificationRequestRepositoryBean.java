package com.ntuc.vendorservice.notificationservice.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.notificationservice.entity.ActivationNotificationRequest;
import org.apache.commons.lang.StringUtils;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailStatus;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;

/**
 * EmailNotificatioRequest Bean; Persist the email notifications  
 * @author I305675
 *
 */
@Stateless
@LocalBean
public class ActivationNotificationRequestRepositoryBean implements GenericRepositoryBean<ActivationNotificationRequest> {
	@PersistenceContext
	private EntityManager em;
	/**
	 * List all the available VendorAccount
	 * 
	 * @return
	 */
	public List<ActivationNotificationRequest> findAll() {
		return em.createNamedQuery("ActivationNotificationRequest.findAll", ActivationNotificationRequest.class).getResultList();
	}
    /**
     * Find Email notifications by status
     * @param emailStatus
     * @return
     */
	public List<ActivationNotificationRequest>  findPendingEmailNotificationByStatus(EmailStatus emailStatus) {
		try {
			return em.createNamedQuery("ActivationNotificationRequest.findPendingEmailNotificationByStatus", ActivationNotificationRequest.class)
					.setParameter("emailStatus", emailStatus).getResultList();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}

	} 
	public List<ActivationNotificationRequest>  findEmailNotificationByVendorAccount(VendorAccount vendorAccount, EmailNotificationDirection emailNotificationDirection) {
		try {
			return em.createNamedQuery("ActivationNotificationRequest.findEmailNotificationByVendorAccount", ActivationNotificationRequest.class)
					.setParameter("recipientEmail", vendorAccount.getVendorAdminEmail())
					.setParameter("vendorID", vendorAccount.getVendorID())
					.setParameter("emailNotificationDirection", emailNotificationDirection)
					.getResultList();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}

	} 
	/**
	 * Create an email notification
	 * @param activationNotificationRequest
	 */
	public ActivationNotificationRequest add(ActivationNotificationRequest activationNotificationRequest) {
		updateEmail(activationNotificationRequest);
		em.persist(activationNotificationRequest);
		em.flush();
		return activationNotificationRequest;
	} 
	 
	@Override
	public ActivationNotificationRequest update(ActivationNotificationRequest activationNotificationRequest) { 
		updateEmail(activationNotificationRequest);
		ActivationNotificationRequest merge = em.merge(activationNotificationRequest);  
   	     return merge;  	     
	}
	
	public void delete(ActivationNotificationRequest activationNotificationRequest) {
		try {
			ActivationNotificationRequest merge = em.merge(activationNotificationRequest); 
			em.remove(merge);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void updateNotificationStatus(EmailStatus emailStatus, 	List<ActivationNotificationRequest> pendingEmailNotifications) {
		try {  
	          for(ActivationNotificationRequest  activationNotificationRequest:pendingEmailNotifications){
	        	  updateEmail(activationNotificationRequest);
	        	  em.merge(activationNotificationRequest); 
	        	  em.flush();
	          }
			  
		} catch (Exception e) {
			e.printStackTrace(); 
		}  
	}
	private void updateEmail(ActivationNotificationRequest e) {
		if(e.getRecipientEmail()!=null){
			String email = StringUtils.lowerCase(e.getRecipientEmail());
			e.setRecipientEmail(email);
		}
		
	}

}
