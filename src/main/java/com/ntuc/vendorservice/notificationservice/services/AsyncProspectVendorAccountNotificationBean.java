package com.ntuc.vendorservice.notificationservice.services;

import java.io.IOException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import com.ntuc.vendorservice.emailingservice.models.EmailContentType;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.notificationservice.models.GeneralNotificationRequest;
import com.ntuc.vendorservice.emailingservice.services.MessagingBean;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;

@Stateless
@LocalBean
public class AsyncProspectVendorAccountNotificationBean {
	@EJB
	protected MessagingBean messagingBean;
	@Asynchronous
	public void sendNotification(ProspectVendorAccount prospectVendorAccount,String accountFlpLink){ 
		try{
			GeneralNotificationRequest generalNotificationRequest=new GeneralNotificationRequest();
			generalNotificationRequest.setContentType(EmailContentType.HTML);
			generalNotificationRequest.setEmailSubject(MailSenderUtil.CWF_ACCOUNT_ACTIVATION_SUBJECT);
			generalNotificationRequest.setFromEmail(MessagingBean.CWF_MAIL_FROM);
			generalNotificationRequest.setRecipientEmail(prospectVendorAccount.getEmail());
			generalNotificationRequest.setRecipientName(prospectVendorAccount.getDisplayName());
			generalNotificationRequest.setEmailNotificationDirection(EmailNotificationDirection.ARTICLE_CREATION);
			MailSenderUtil mailSenderUtil=new MailSenderUtil(generalNotificationRequest); 
			generalNotificationRequest.setMessage(mailSenderUtil.getCWFAccountNotificationMessage(prospectVendorAccount,accountFlpLink));  
			messagingBean.sendWithAttachment(generalNotificationRequest);
			
		}catch(MessagingException | IOException e){
			e.printStackTrace(); 
		}
	}
}
