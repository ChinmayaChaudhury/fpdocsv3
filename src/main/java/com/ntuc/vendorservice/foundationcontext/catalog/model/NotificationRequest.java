package com.ntuc.vendorservice.foundationcontext.catalog.model;

public interface NotificationRequest {
	
	String getEmailAddress();
	
	String getDisplayName();
	
	long getToUserID();
}
