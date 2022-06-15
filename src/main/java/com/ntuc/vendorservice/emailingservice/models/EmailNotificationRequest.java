package com.ntuc.vendorservice.emailingservice.models;

import java.io.Serializable;
import java.util.Date;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.RecipientType;

/**
 * Email Notification Request Abstraction
 * 
 * @author I305675
 *
 */
public interface EmailNotificationRequest extends Serializable {
	
	/**
	 * 
	 * @return fromEmail
	 */
	String getFromEmail();
	/**
	 * 
	 * @param fromEmail to set fromEmail
	 */
	void setFromEmail(String fromEmail);
	/**
	 * 
	 * @return createDateTime
	 */
	Date getCreateDateTime();

	/**
	 * 
	 * @param createDateTime
	 *            to set createDateTime
	 */
	void setCreateDateTime(Date createDateTime);

	/**
	 * 
	 * @param emailType
	 *            to set emailType
	 */
	void setEmailType(EmailType emailType);

	/***
	 * 
	 * @return emailType
	 */
	EmailType getEmailType();

	/**
	 * 
	 * @param emailNotificationDirection
	 *            to set emailNotificationDirection
	 */
	void setEmailNotificationDirection(EmailNotificationDirection emailNotificationDirection);

	/**
	 * 
	 * @return emailNotificationDirection
	 */
	EmailNotificationDirection getEmailNotificationDirection();

	/**
	 * 
	 * @param targetUrl
	 *            to set targetUrl
	 */
	void setTargetUrl(String targetUrl);

	/**
	 * 
	 * @return targetUrl
	 */
	String getTargetUrl();

	/**
	 * 
	 * @param recipientName
	 *            to set recipientName
	 */
	void setRecipientName(String recipientName);

	/**
	 * @return the recipientName
	 */
	String getRecipientName();

	/**
	 * 
	 * @return the recipientEmail
	 */
	String getRecipientEmail();

	/**
	 * 
	 * @param recipientEmail
	 *            to set recipientEmail
	 */
	void setRecipientEmail(String recipientEmail);

	/**
	 * @return the emailSubject
	 */
	String getEmailSubject();

	/**
	 * @param emailSubject
	 *            the emailSubject to set
	 */
	void setEmailSubject(String emailSubject);

	/**
	 * @return the contentType
	 */
	EmailContentType getContentType();

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	void setContentType(EmailContentType contentType);
	/**
	 * @param message
	 *            the message to set
	 */

	void setMessage(String message);
	/**
	 * 
	 * @return message
	 */
	String getMessage();
	
	
	/**
	 * 
	 * @return recipient type
	 */
	RecipientType  getRecipientType();

}
