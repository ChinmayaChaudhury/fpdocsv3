package com.ntuc.vendorservice.emailingservice.services;

import com.ntuc.vendorservice.notificationservice.entity.ActivationNotificationRequest;
import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailContentType;
import com.ntuc.vendorservice.emailingservice.models.EmailStatus;
import com.ntuc.vendorservice.emailingservice.models.EmailType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.NotificationRequest;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;

/**
 * 
 * @author I305675
 *
 */
public class NotificationRequestProxy {

	/**
	 * Get Email Notification Request  
	 * @param t
	 * @return
	 */
	public static <T extends NotificationRequest> ActivationNotificationRequest getActivationEmailRequest(T t, String targetUrl){
		ActivationNotificationRequest activationNotificationRequest = new ActivationNotificationRequest();
		if(t instanceof VendorUser){
			VendorUser vendorUser=(VendorUser)t;
			activationNotificationRequest.setRecipientEmail(vendorUser.getEmailAddress());
			activationNotificationRequest.setCreateDateTime(vendorUser.getCreateDateTime());
			activationNotificationRequest.setToUserID(vendorUser.getVendorUserID());
			activationNotificationRequest.setVendorID(vendorUser.getVendorID());
			activationNotificationRequest.setRecipientName(vendorUser.getDisplayName());
			activationNotificationRequest.setEmailNotificationDirection(EmailNotificationDirection.VADMIN_2_VUSER);
		}
		if(t instanceof InternalUser){
			InternalUser fairPriceUser=(InternalUser)t;
			activationNotificationRequest.setRecipientEmail(fairPriceUser.getEmailAddress());
			activationNotificationRequest.setCreateDateTime(fairPriceUser.getCreateDateTime());
			activationNotificationRequest.setToUserID(fairPriceUser.getFairPriceUserID());
			activationNotificationRequest.setVendorID(null);
			activationNotificationRequest.setRecipientName(fairPriceUser.getDisplayName());
			activationNotificationRequest.setEmailNotificationDirection(EmailNotificationDirection.FPADMIN_2_FPUSER);
		}
		if(t instanceof VendorAccount){
			VendorAccount vendorAccount=(VendorAccount)t;
			activationNotificationRequest.setRecipientEmail(vendorAccount.getVendorAdminEmail());
			activationNotificationRequest.setToUserID(vendorAccount.getVendorID());
			activationNotificationRequest.setCreateDateTime(vendorAccount.getCreateDateTime());
			activationNotificationRequest.setEmailNotificationDirection(EmailNotificationDirection.FPADMIN_2_VENDORADMIN);
			activationNotificationRequest.setVendorID(vendorAccount.getVendorID());
			activationNotificationRequest.setRecipientName(vendorAccount.getDisplayName());
			
		} 
		if(t instanceof ProspectVendorAccount){
			ProspectVendorAccount prospectAccount =(ProspectVendorAccount)t; 
			activationNotificationRequest.setRecipientEmail(prospectAccount.getEmail());
			activationNotificationRequest.setToUserID(prospectAccount.getAccountID());
			activationNotificationRequest.setCreateDateTime(prospectAccount.getCreateDateTime());
			activationNotificationRequest.setEmailNotificationDirection(EmailNotificationDirection.FPADMIN_2_VENDORADMIN);
			activationNotificationRequest.setVendorID(null);
			activationNotificationRequest.setRecipientName(prospectAccount.getDisplayName());
			
		} 
		activationNotificationRequest.setMessage("");
		activationNotificationRequest.setEmailType(EmailType.ACCOUNT_ACTIVATION);
		activationNotificationRequest.setEmailStatus(EmailStatus.PENDING);
		activationNotificationRequest.setTargetUrl(targetUrl);
		activationNotificationRequest.setEmailSubject(MailSenderUtil.CWF_ACCOUNT_ACTIVATION_SUBJECT);
		activationNotificationRequest.setContentType(EmailContentType.PLAIN);
		return activationNotificationRequest;
	}
	
	
}
