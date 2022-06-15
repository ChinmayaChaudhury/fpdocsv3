package com.ntuc.vendorservice.scimservice.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User based on SCIM records
 * 
 * @author I305675
 *
 */
public class SCIMUser implements SCIUserAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_SCHEMA="urn:ietf:params:scim:api:messages:2.0:User";
	private List<String> schemas;
	private String id;
	private String locale;
	private String userName;
	private String displayName;
	private String contactPreferenceEmail;
	private String contactPreferenceTelephone;
	private String industryCrm;
	private String company;
	private String department;
	private String relationshipToSAP;
	private String userType;
	private Boolean active;
	private SCIMName name;
	private Set<SCIMAddress> addresses;
	private Set<SCIMEmail> emails;
	private Set<SCIMPhoneNumber> phoneNumbers;
	private Set<SCIMGroup> groups;
	private String targetUrl;
	private String spCustomAttribute1;
	private String spCustomAttribute2;
	private String spCustomAttribute3;
	private String spCustomAttribute4;
	private String spCustomAttribute5;

	/**
	 * @return the schemas
	 */
	public List<String> getSchemas() {
		if(schemas==null){
			schemas=new ArrayList<String>();
			schemas.add(DEFAULT_SCHEMA);
		}
		return schemas;
	}

	/**
	 * @param schemas
	 *            the schemas to set
	 */
	public void setSchemas(List<String> schemas) {
		this.schemas = schemas;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the contactPreferenceEmail
	 */
	public String getContactPreferenceEmail() {
		return contactPreferenceEmail;
	}

	/**
	 * @param contactPreferenceEmail
	 *            the contactPreferenceEmail to set
	 */
	public void setContactPreferenceEmail(String contactPreferenceEmail) {
		this.contactPreferenceEmail = contactPreferenceEmail;
	}

	/**
	 * @return the contactPreferenceTelephone
	 */
	public String getContactPreferenceTelephone() {
		return contactPreferenceTelephone;
	}

	/**
	 * @param contactPreferenceTelephone
	 *            the contactPreferenceTelephone to set
	 */
	public void setContactPreferenceTelephone(String contactPreferenceTelephone) {
		this.contactPreferenceTelephone = contactPreferenceTelephone;
	}

	/**
	 * @return the industryCrm
	 */
	public String getIndustryCrm() {
		return industryCrm;
	}

	/**
	 * @param industryCrm
	 *            the industryCrm to set
	 */
	public void setIndustryCrm(String industryCrm) {
		this.industryCrm = industryCrm;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department
	 *            the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * @return the relationshipToSAP
	 */
	public String getRelationshipToSAP() {
		return relationshipToSAP;
	}

	/**
	 * @param relationshipToSAP
	 *            the relationshipToSAP to set
	 */
	public void setRelationshipToSAP(String relationshipToSAP) {
		this.relationshipToSAP = relationshipToSAP;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the name
	 */
	public SCIMName getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(SCIMName name) {
		this.name = name;
	}

	/**
	 * @return the addresses
	 */
	public Set<SCIMAddress> getAddresses() {
		if(addresses==null){
			addresses=new HashSet<SCIMAddress>();
		}
		return addresses;
	}

	/**
	 * @param addresses
	 *            the addresses to set
	 */
	public void setAddresses(Set<SCIMAddress> addresses) {
		this.addresses = addresses;
	}

	/**
	 * @return the emails
	 */
	public Set<SCIMEmail> getEmails() { 
		return emails;
	}

	/**
	 * @param emails
	 *            the emails to set
	 */
	public void setEmails(Set<SCIMEmail> emails) {
		this.emails = emails;
	}

	/**
	 * @return the phoneNumbers
	 */
	public Set<SCIMPhoneNumber> getPhoneNumbers() {
		if(phoneNumbers==null){
			phoneNumbers=new HashSet<SCIMPhoneNumber>();
		}
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers
	 *            the phoneNumbers to set
	 */
	public void setPhoneNumbers(Set<SCIMPhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	/**
	 * @return the groups
	 */
	public Set<SCIMGroup> getGroups() {
		if(groups==null){
			groups=new HashSet<SCIMGroup>();
		}
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(Set<SCIMGroup> groups) {
		this.groups = groups;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getUserID() { 
		return getId();
	}

	@Override
	public void setUserID(String userID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFirstName() { 
		return getName().getGivenName();
	}

	@Override
	public void setFirstName(String firstName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLastName() { 
		  return getName().getFamilyName();
	}

	@Override
	public void setLastName(String lastName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEmail() {
		String email="";
		if(getEmails().size()==1){
			String []a=new String[getEmails().size()];
			email=getEmails().toArray(a)[0];
		}else if(getEmails().size()>1){
			email=getEmails().toString();
		}
		return email;
	}

	@Override
	public void setEmail(String email) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCompanyName() { 
		return getCompany();
	}

	@Override
	public void setCompanyName(String companyName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub 
		return null;
	}

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSalutation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSalutation(String salutation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getJobTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJobTitle(String jobTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRole(String role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSendEmail() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSendEmail(boolean sendEmail) {
		// TODO Auto-generated method stub 
	}

	@Override
	public String getTargetUrl() { 
		return targetUrl;
	}

	@Override
	public void setTargetUrl(String targetUrl) {
		this.targetUrl=targetUrl;
		
	}

	@Override
	public String getSpCustomAttribute1() { 
		return spCustomAttribute1;
	}

	@Override
	public void setSpCustomAttribute1(String spCustomAttribute1) {
		this.spCustomAttribute1=spCustomAttribute1;
		
	}

	@Override
	public String getSpCustomAttribute2() { 
		return spCustomAttribute2;
	}

	@Override
	public void setSpCustomAttribute2(String spCustomAttribute2) {
		this.spCustomAttribute2=spCustomAttribute2;
		
	}

	@Override
	public String getSpCustomAttribute3() { 
		return spCustomAttribute3;
	}

	@Override
	public void setSpCustomAttribute3(String spCustomAttribute3) { 
		this.spCustomAttribute3=spCustomAttribute3;
		
	}

	@Override
	public String getSpCustomAttribute4() { 
		return spCustomAttribute4;
	}

	@Override
	public void setSpCustomAttribute4(String spCustomAttribute4) {
		this.spCustomAttribute4=spCustomAttribute4;
		
	}

	@Override
	public String getSpCustomAttribute5() { 
		return spCustomAttribute5;
	}

	@Override
	public void setSpCustomAttribute5(String spCustomAttribute5) {
		this.spCustomAttribute5=spCustomAttribute5;
		
	} 
}
