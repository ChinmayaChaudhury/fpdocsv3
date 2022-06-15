package com.ntuc.vendorservice.scimservice.models;

import java.io.Serializable;

/**
 * SCI Account interface
 * 
 * @author I305675
 *
 */
public interface SCIUserAccount extends Serializable {
	public static enum STATUS{
		New,Active
	}
	/**
	 * @return the userID
	 */
	public String getUserID();

	/**
	 * @param userID
	 *            the userID to set
	 */
	public void setUserID(String userID);

	/**
	 * @return the firstName
	 */
	public String getFirstName();

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName);

	/**
	 * @return the lastName
	 */
	public String getLastName();

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName);

	/**
	 * @return the email
	 */
	public String getEmail();

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email);

	/**
	 * @return the displayName
	 */
	public String getDisplayName();

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName);

	/**
	 * @return the companyName
	 */
	public String getCompanyName();

	/**
	 * @param companyName
	 *            the companyName to set
	 */
	public void setCompanyName(String companyName);

	/**
	 * @return the title
	 */
	public String getTitle();

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title);

	/**
	 * @return the salutation
	 */
	public String getSalutation();

	/**
	 * @param salutation
	 *            the salutation to set
	 */
	public void setSalutation(String salutation);

	public String getJobTitle();

	public void setJobTitle(String jobTitle);

	/**
	 * @return the role
	 */
	public String getRole();

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role);

	/**
	 * @return the sendEmail
	 */
	public boolean isSendEmail();

	/**
	 * @param sendEmail
	 *            the sendEmail to set
	 */
	public void setSendEmail(boolean sendEmail);

	/**
	 * @return the targetUrl
	 */
	public String getTargetUrl();

	/**
	 * @param targetUrl
	 *            the targetUrl to set
	 */
	public void setTargetUrl(String targetUrl);

	/**
	 * @return the spCustomAttribute1
	 */
	public String getSpCustomAttribute1();

	/**
	 * @param spCustomAttribute1
	 *            the spCustomAttribute1 to set
	 */
	public void setSpCustomAttribute1(String spCustomAttribute1);

	/**
	 * @return the spCustomAttribute2
	 */
	public String getSpCustomAttribute2();

	/**
	 * @param spCustomAttribute2
	 *            the spCustomAttribute2 to set
	 */
	public void setSpCustomAttribute2(String spCustomAttribute2);

	/**
	 * @return the spCustomAttribute3
	 */
	public String getSpCustomAttribute3();

	/**
	 * @param spCustomAttribute3
	 *            the spCustomAttribute3 to set
	 */
	public void setSpCustomAttribute3(String spCustomAttribute3);

	/**
	 * @return the spCustomAttribute4
	 */
	public String getSpCustomAttribute4();

	/**
	 * @param spCustomAttribute4
	 *            the spCustomAttribute4 to set
	 */
	public void setSpCustomAttribute4(String spCustomAttribute4);

	/**
	 * @return the spCustomAttribute5
	 */
	public String getSpCustomAttribute5();

	/**
	 * @param spCustomAttribute5
	 *            the spCustomAttribute5 to set
	 */
	public void setSpCustomAttribute5(String spCustomAttribute5);
}
