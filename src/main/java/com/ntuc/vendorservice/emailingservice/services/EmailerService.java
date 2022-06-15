package com.ntuc.vendorservice.emailingservice.services;

import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author I305675
 * 
 */
public interface EmailerService {
	enum EmailSource {
		CWF, INTERNAL, OTHERS
	}
	Logger LOGGER = LoggerFactory.getLogger(EmailerService.class);
	String MAIL_FROM = "documentsharing@fairprice.com.sg";
	String OTHER_MAIL_FROM = "notification@fairprice.com.sg";
	String CWF_MAIL_FROM = "cwfnotification@fairprice.com.sg";

	/**
	 * Send email without attachments
	 *
	 * @param emailNotificationRequest
	 * @param emailSource
	 * @throws MessagingException
	 */
	void sendEmailWithoutAttachment(HttpServletRequest request, EmailNotificationRequest emailNotificationRequest, EmailSource emailSource) throws MessagingException;

	/**
	 * Send email without attachments
	 *
	 * @param emailNotificationRequest
	 * @param recipientEmails
	 * @param emailSource
	 */
	void sendEmailBCCWithoutAttachment(HttpServletRequest request, EmailNotificationRequest emailNotificationRequest, List<String> recipientEmails, EmailSource emailSource) throws MessagingException;
	/**
	 * Send email with attachment
	 * @param request
	 * @param emailNotificationRequest
	 * @param emailSource
	 * @throws MessagingException
	 */
}
