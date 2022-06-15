package com.ntuc.vendorservice.emailingservice.services;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;
import com.ntuc.vendorservice.emailingservice.models.EmailType;
import com.ntuc.vendorservice.notificationservice.models.GeneralNotificationRequest;
import com.ntuc.vendorservice.notificationservice.entity.ActivationNotificationRequest;

/**
 * Email Notification adapter
 * @author I305675
 *
 */
public class EmailNotificationRequestFactory {

	private static EmailNotificationRequest notificationRequest =  new GeneralNotificationRequest();

	public static EmailNotificationRequest getInstance(EmailType emailType, EmailNotificationDirection notificationDirection) {
		switch (emailType) {
		case ACCOUNT_ACTIVATION: 
			notificationRequest=new ActivationNotificationRequest();
			break;
		default:
			notificationRequest=new GeneralNotificationRequest();
			break;
		}
		notificationRequest.setEmailType(emailType);
		notificationRequest.setEmailNotificationDirection(notificationDirection);;
		return notificationRequest;
	}

}
