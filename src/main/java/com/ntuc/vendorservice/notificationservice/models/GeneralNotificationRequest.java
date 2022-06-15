package com.ntuc.vendorservice.notificationservice.models;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ntuc.vendorservice.emailingservice.models.EmailContentType;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;
import com.ntuc.vendorservice.emailingservice.models.EmailType;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.RecipientType;

public class GeneralNotificationRequest implements EmailNotificationRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String recipientEmail;
	private List<String> recipientEmails;
	private String recipientName;
	private String emailSubject;
	private String message; 
	private EmailContentType contentType;
	private EmailType emailType;
	private Date createDateTime;
	private EmailNotificationDirection emailNotificationDirection;
	private String targetUrl;
	private String fromEmail;
	private RecipientType recipientType;
	private String sourceType;
	private String formNo;
	private String appntDate;
	
	public String getFormNo() {
		return formNo;
	}
	public void setFormNo(String formNo) {
		this.formNo = formNo;
	}
	public String getAppntDate() {
		return appntDate;
	}
	public void setAppntDate(String appntDate) {
		this.appntDate = appntDate;
	}
	/**
	 * @return sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}
	/**
	 * @param sourceType
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	/**
	 * @return the recipientEmail
	 */
	public String getRecipientEmail() {
		return recipientEmail;
	}
	/**
	 * @param recipientEmail the recipientEmail to set
	 */
	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}
	/**
	 * @return the recipientName
	 */
	public String getRecipientName() {
		return recipientName;
	}
	/**
	 * @param recipientName the recipientName to set
	 */
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	/**
	 * @return the emailSubject
	 */
	public String getEmailSubject() {
		return emailSubject;
	}
	/**
	 * @param emailSubject the emailSubject to set
	 */
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the contentType
	 */
	public EmailContentType getContentType() {
		return contentType;
	}
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(EmailContentType contentType) {
		this.contentType = contentType;
	}
	@Override
	public Date getCreateDateTime() { 
		return createDateTime;
	}
	@Override
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime=createDateTime;
		
	}
	@Override
	public void setEmailType(EmailType emailType) {
		this.emailType=emailType;
		
	}
	@Override
	public EmailType getEmailType() { 
		return emailType;
	}
	@Override
	public void setEmailNotificationDirection(EmailNotificationDirection emailNotificationDirection) {
		this.emailNotificationDirection=emailNotificationDirection;
		
	}
	@Override
	public EmailNotificationDirection getEmailNotificationDirection() { 
		return emailNotificationDirection;
	}
	@Override
	public void setTargetUrl(String targetUrl) {
		this.targetUrl=targetUrl;
		
	}
	@Override
	public String getTargetUrl() { 
		return targetUrl;
	}
	
	/**
	 * @return the recipientEmails
	 */
	public List<String> getRecipientEmails() {
		if(null==recipientEmails){
			recipientEmails= new ArrayList<>();
		}
		return recipientEmails;
	}
	/**
	 * @param recipientEmails the recipientEmails to set
	 */
	public void setRecipientEmails(List<String> recipientEmails) {
		this.recipientEmails = recipientEmails;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EmailMessage [recipientEmail=" + recipientEmail + ", recipientName=" + recipientName + ", emailSubject="
				+ emailSubject + ", message=" + message + ", contentType=" + contentType + ", emailType=" + emailType
				+ ", createDateTime=" + createDateTime + ", notificationType=" + emailNotificationDirection + ", targetUrl="
				+ targetUrl + "]";
	}
	@Override
	public String getFromEmail() { 
		return fromEmail;
	}
	@Override
	public void setFromEmail(String fromEmail) {
		this.fromEmail=fromEmail;
		
	}
	@Override
	public RecipientType getRecipientType() { 
		return recipientType;
	}
	
   public void setRecipientType(RecipientType recipientType) {
	   this.recipientType = recipientType;
   }
	
	
}
