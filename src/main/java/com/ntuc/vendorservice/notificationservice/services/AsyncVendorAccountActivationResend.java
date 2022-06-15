package com.ntuc.vendorservice.notificationservice.services;

import java.util.Iterator;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import com.ntuc.vendorservice.notificationservice.entity.ActivationNotificationRequest;
import com.ntuc.vendorservice.vendoradminservice.repository.VendorToAdminMappingRepositoryBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.services.EmailerService;
import com.ntuc.vendorservice.notificationservice.repository.ActivationNotificationRequestRepositoryBean;
import com.ntuc.vendorservice.emailingservice.services.MessagingBean;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorToAdminMapping;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;

@Stateless
@LocalBean
public class AsyncVendorAccountActivationResend {
	private static final Log LOGGER = LogFactory.getLog(AsyncVendorAccountActivationResend.class);
	@EJB
	protected MessagingBean messagingBean;
	@EJB
	protected VendorToAdminMappingRepositoryBean vendorToAdminMappingRepositoryBean;
	@EJB
	protected ActivationNotificationRequestRepositoryBean activationNotificationRequestBean;

	@Asynchronous
	public void dispatchVendorAccountActivationNotifications(final Long vendorID){
		VendorToAdminMapping findByVendorAccount = vendorToAdminMappingRepositoryBean.findByVendorAccount(vendorID);
		VendorAccountAdministrator vendorAccountAdministrator = (findByVendorAccount == null) ? null : findByVendorAccount.getVendorAdministrator();
		VendorAccount vendorAccount = findByVendorAccount.getVendorAccount();
		List<ActivationNotificationRequest> notificationRequests = activationNotificationRequestBean.findEmailNotificationByVendorAccount(vendorAccount,EmailNotificationDirection.FPADMIN_2_VENDORADMIN);
		Iterator<ActivationNotificationRequest> iterator = notificationRequests.iterator();
		while (iterator.hasNext()) {
			try{
			   ActivationNotificationRequest activationNotificationRequest = iterator.next();
			   MailSenderUtil util = new MailSenderUtil(activationNotificationRequest); 
				String message = util.getEmailActivationMessage(vendorAccountAdministrator,null);
				activationNotificationRequest.setMessage(message);
				activationNotificationRequest.setFromEmail(EmailerService.CWF_MAIL_FROM);
				messagingBean.sendWithAttachment(activationNotificationRequest); 
			}
			catch (MessagingException exc) {
				exc.printStackTrace(); 
				LOGGER.error(exc.getMessage(), exc);  
			} catch (Exception exc) {
				exc.printStackTrace();
				LOGGER.error(exc.getMessage(), exc);  
			}
		}
	}
	
}
