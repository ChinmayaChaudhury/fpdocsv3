package com.ntuc.vendorservice.notificationservice.entity;

import java.util.Date;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.RecipientType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;
import com.ntuc.vendorservice.emailingservice.models.EmailContentType;
import com.ntuc.vendorservice.emailingservice.models.EmailStatus;
import com.ntuc.vendorservice.emailingservice.models.EmailType;

@Entity
@Table(name = "T_EMAIL_ACTNOTIFICATION")
@NamedQueries(value = {
		@NamedQuery(name = "ActivationNotificationRequest.findAll", query = "SELECT e FROM ActivationNotificationRequest e"),
		@NamedQuery(name = "ActivationNotificationRequest.findEmailNotificationByVendorAccount", query = "SELECT e FROM ActivationNotificationRequest e WHERE e.recipientEmail=:recipientEmail AND e.vendorID=:vendorID AND e.emailNotificationDirection=:emailNotificationDirection"),
		@NamedQuery(name = "ActivationNotificationRequest.findPendingEmailNotificationByStatus", query = "SELECT e FROM ActivationNotificationRequest e WHERE e.emailStatus=:emailStatus ") })
public class ActivationNotificationRequest implements EmailNotificationRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long emailNotificationID;
	@Column(length = 60)
	@Enumerated(EnumType.STRING)
	private EmailType emailType;
	@Column(length = 30)
	private long toUserID;
	@Column(length = 200)
	private String recipientEmail;
	@Column(length = 200)
	private String recipientName;
	@Column(length = 200)
	private String emailSubject;
	@Column(length = 3000)
	private String message;
	@Column(length = 250)
	private String targetUrl;
	@Column(length = 10)
	private Long vendorID;
	@Enumerated(EnumType.STRING)
	@Column(length = 60)
	private EmailNotificationDirection emailNotificationDirection;
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private EmailStatus emailStatus;
	@Temporal(TemporalType.DATE)
	private Date createDateTime;
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private EmailContentType contentType;
	@Transient
	private String fromEmail;
	@Transient
	private RecipientType recipientType;

	public Long getEmailNotificationID() {
		return emailNotificationID;
	}

	public void setEmailNotificationID(Long emailNotificationID) {
		this.emailNotificationID = emailNotificationID;
	}

	public EmailType getEmailType() {
		return emailType;
	}

	public void setEmailType(EmailType emailType) {
		this.emailType = emailType;
	}

	public long getToUserID() {
		return toUserID;
	}

	public void setToUserID(long toUserID) {
		this.toUserID = toUserID;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String toEmailAddress) {
		this.recipientEmail = toEmailAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EmailNotificationDirection getEmailNotificationDirection() {
		return emailNotificationDirection;
	}

	public void setEmailNotificationDirection(EmailNotificationDirection emailNotificationDirection) {
		this.emailNotificationDirection = emailNotificationDirection;
	}

	public EmailStatus getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(EmailStatus emailStatus) {
		this.emailStatus = emailStatus;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Long getVendorID() {
		return vendorID;
	}

	public void setVendorID(Long vendorID) {
		this.vendorID = vendorID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	@Override
	public String getRecipientName() {
		return recipientName;
	}

	@Override
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;

	}

	@Override
	public String getEmailSubject() {
		return emailSubject;
	}

	@Override
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	/**
	 * @return the contentType
	 */
	@Override
	public EmailContentType getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	@Override
	public void setContentType(EmailContentType contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return "ActivationNotificationRequest [emailNotificationID=" + emailNotificationID + ", emailType=" + emailType
				+ ", toUserID=" + toUserID + ", recipientEmail=" + recipientEmail + ", message=" + message
				+ ", vendorID=" + vendorID + ", emailNotificationDirection=" + emailNotificationDirection
				+ ", emailStatus=" + emailStatus + ", createDateTime=" + createDateTime + "]";
	}

	@Override
	public String getFromEmail() {
		return fromEmail;
	}

	@Override
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;

	}

	@Override
	public RecipientType getRecipientType() { 
		return recipientType;
	}
	
   public void setRecipientType(RecipientType recipientType) {
	   this.recipientType = recipientType;
   }
}
