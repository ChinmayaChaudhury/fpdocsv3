package com.ntuc.vendorservice.scimservice.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ntuc.vendorservice.foundationcontext.catalog.model.VendorAccountModel;
import com.ntuc.vendorservice.scimservice.annotations.SCIRestApiParameter;
import com.ntuc.vendorservice.scimservice.services.FPSCIProfileXmlSaxParserTransformer;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.vendoradminservice.models.VendorCategory;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;

public class FPSCIUserAccount implements SCIUserAccount,SystemAccount {
	private static final long serialVersionUID = 1L;
	@SCIRestApiParameter(name = { "name_id","uid"})
	private String userID;
	@SCIRestApiParameter(name = {  "organization_wide_id" })
	private String organisationWideID;
	@SCIRestApiParameter(name = { "first_name" })
	private String firstName;
	@SCIRestApiParameter(name = { "last_name" })
	private String lastName;
	@SCIRestApiParameter(name = { "email" })
	private String email;
	@SCIRestApiParameter(name = { "display_name" })
	private String displayName;
	@SCIRestApiParameter(name = { "company" })
	private String companyName;
	@SCIRestApiParameter(name = { "send_email" })
	private Boolean sendEmail;
	@SCIRestApiParameter(name = { "title" })
	private String title; 
	@SCIRestApiParameter(name = { "target_url" })
	private String targetUrl; 
	private String salutation;
	private String jobTitle;
	private String department;
	private String industry;
	@SCIRestApiParameter(name = { "user_provisioning_role" })
	private String role;
	@SCIRestApiParameter(name = { "spCustomAttribute1" })
	private String spCustomAttribute1;
	@SCIRestApiParameter(name = { "spCustomAttribute2" })
	private String spCustomAttribute2;
	@SCIRestApiParameter(name = { "spCustomAttribute3" })
	private String spCustomAttribute3;
	@SCIRestApiParameter(name = { "spCustomAttribute4" })
	private String spCustomAttribute4;
	@SCIRestApiParameter(name = { "spCustomAttribute5" })
	private String spCustomAttribute5;
	private VendorCategory vendorCategory;
	private String vendorCode;
	@SCIRestApiParameter(name = { "user_profile_id" })
	private String profileID;
	@SCIRestApiParameter(name = { "status" })
	private String status;
	@SCIRestApiParameter(name = { "login_name" })
	private String loginName;
	@SCIRestApiParameter(name = { "organization_user_type" })
	private String userType;
	@SCIRestApiParameter(name = { "user_provisioning_role" })
	private String userProvisioningRole;
	@SCIRestApiParameter(name = { "language" })
	private String language;
	private String createBy;
	private String work;
	private String mobile;
	private List<Integer> vendorUserGroupIDs;
	private Integer fairpriceUserGroupID; 
	private SystemAccountStatus systemAccountStatus;
	@SCIRestApiParameter(name = { "country" })
	private String country;
	@SCIRestApiParameter(name = { "company_city" })
	private String companyCity;
	@SCIRestApiParameter(name = { "city" })
	private String city;
	private HashMap<String, String> addedProperty;

	private String vendorID;
	private Integer vendorAdminID;
	private String vendorUserID;
	private boolean allowDocumentShare;
	private String action;
	private String newLoginName;
	private List<String> selectedGroups;
	
	public List<String> getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(List<String> selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

	private String sciUserId;
	
	public String getVendorUserID() {
		return vendorUserID;
	}

	public void setVendorUserID(String vendorUserID) {
		this.vendorUserID = vendorUserID;
	}

	public String getSciUserId() {
		return sciUserId;
	}

	public void setSciUserId(String sciUserId) {
		this.sciUserId = sciUserId;
	}

	public String getNewLoginName() {
		return newLoginName;
	}

	public void setNewLoginName(String newLoginName) {
		this.newLoginName = newLoginName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Associated Vendor Accounts
	 */
	private List<VendorAccountModel> vendorAccounts = new ArrayList<VendorAccountModel>();
	
	public boolean isAllowDocumentShare() {
		return allowDocumentShare;
	}

	public void setAllowDocumentShare(boolean allowDocumentShare) {
		this.allowDocumentShare = allowDocumentShare;
	}

	public Integer getVendorAdminID() {
		return vendorAdminID;
	}

	public void setVendorAdminID(Integer vendorAdminID) {
		this.vendorAdminID = vendorAdminID;
	}

	public String getVendorID() {
		return vendorID;
	}

	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}

	public List<VendorAccountModel> getVendorAccounts() {
		return vendorAccounts;
	}

	public void setVendorAccounts(List<VendorAccountModel> vendorAccountModels) {
		this.vendorAccounts = vendorAccountModels;
	}
	
	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID
	 *            the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		if(displayName==null||displayName.isEmpty()){
			displayName= getFirstName() +" "+ getLastName();
		}
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
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName
	 *            the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the salutation
	 */
	public String getSalutation() {
		return salutation;
	}

	/**
	 * @param salutation
	 *            the salutation to set
	 */
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the sendEmail
	 */
	public boolean isSendEmail() {
		return sendEmail;
	}

	/**
	 * @param sendEmail
	 *            the sendEmail to set
	 */
	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	/**
	 * @return the targetUrl
	 */
	public String getTargetUrl() {
		return targetUrl;
	}

	/**
	 * @param targetUrl
	 *            the targetUrl to set
	 */
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	/**
	 * @return the spCustomAttribute1
	 */
	public String getSpCustomAttribute1() {
		return spCustomAttribute1;
	}

	/**
	 * @param spCustomAttribute1
	 *            the spCustomAttribute1 to set
	 */
	public void setSpCustomAttribute1(String spCustomAttribute1) {
		this.spCustomAttribute1 = spCustomAttribute1;
	}

	/**
	 * @return the spCustomAttribute2
	 */
	public String getSpCustomAttribute2() {
		return spCustomAttribute2;
	}

	/**
	 * @param spCustomAttribute2
	 *            the spCustomAttribute2 to set
	 */
	public void setSpCustomAttribute2(String spCustomAttribute2) {
		this.spCustomAttribute2 = spCustomAttribute2;
	}

	/**
	 * @return the spCustomAttribute3
	 */
	public String getSpCustomAttribute3() {
		return spCustomAttribute3;
	}

	/**
	 * @param spCustomAttribute3
	 *            the spCustomAttribute3 to set
	 */
	public void setSpCustomAttribute3(String spCustomAttribute3) {
		this.spCustomAttribute3 = spCustomAttribute3;
	}

	/**
	 * @return the spCustomAttribute4
	 */
	public String getSpCustomAttribute4() {
		return spCustomAttribute4;
	}

	/**
	 * @param spCustomAttribute4
	 *            the spCustomAttribute4 to set
	 */
	public void setSpCustomAttribute4(String spCustomAttribute4) {
		this.spCustomAttribute4 = spCustomAttribute4;
	}

	/**
	 * @return the spCustomAttribute5
	 */
	public String getSpCustomAttribute5() {
		return spCustomAttribute5;
	}

	/**
	 * @param spCustomAttribute5
	 *            the spCustomAttribute5 to set
	 */
	public void setSpCustomAttribute5(String spCustomAttribute5) {
		this.spCustomAttribute5 = spCustomAttribute5;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public HashMap<String, Object> getAccountProfileDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setVendorCategory(VendorCategory vendorCategory) {
		this.vendorCategory=vendorCategory;
		
	}
	public VendorCategory getVendorCategory() {
		return vendorCategory;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode=vendorCode; 
	}
	public String getVendorCode() {
		return vendorCode;
	}

	/**
	 * @return the organisationWideID
	 */
	public String getOrganisationWideID() {
		return organisationWideID;
	}

	/**
	 * @param organisationWideID the organisationWideID to set
	 */
	public void setOrganisationWideID(String organisationWideID) {
		this.organisationWideID = organisationWideID;
	}

	/**
	 * @return the profileID
	 */
	public String getProfileID() {
		return profileID;
	}

	/**
	 * @param profileID the profileID to set
	 */
	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public void setLoginName(String loginName) {
		this.loginName=loginName;
	 }
	public String getLoginName() {
		return loginName;
	}

	public void setUserType(String userType) {
		this.userType=userType;
		
	}
    public String getUserType() {
		return userType;
	}

	public void setUserProvisioningRole(String userProvisioningRole) {
		this.userProvisioningRole=userProvisioningRole;
		
	}
	public String getUserProvisioningRole() {
		return userProvisioningRole;
	}

	public void setLanguage(String language) {
		this.language=language; 
	}
	public String getLanguage() {
		return language;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	/**
	 * 
	 * @return vendorUserGroupID
	 */
	public List<Integer> getVendorUserGroupID() { 
		if(vendorUserGroupIDs==null){
			vendorUserGroupIDs=new ArrayList<Integer>();	
		}
		return vendorUserGroupIDs;
	}
	/**
	 * 
	 * @param vendorUserGroupID
	 */
	public void setVendorUserGroupID(List<Integer> vendorUserGroupID) {
		this.vendorUserGroupIDs = vendorUserGroupID;
	}

	/**
	 * @return the fairpriceUserGroupID
	 */
	public Integer getFairpriceUserGroupID() {
		return fairpriceUserGroupID;
	}

	/**
	 * @param fairpriceUserGroupID the fairpriceUserGroupID to set
	 */
	public void setFairpriceUserGroupID(Integer fairpriceUserGroupID) {
		this.fairpriceUserGroupID = fairpriceUserGroupID;
	}
	

	/**
	 * @return the industry
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * @param industry the industry to set
	 */
	public void setIndustry(String industry) {
		this.industry = industry;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FPSCIUserAccount [userID=" + userID + ", organisationWideID=" + organisationWideID + ", firstName="
				+ firstName + ", lastName=" + lastName + ", email=" + email + ", displayName=" + displayName
				+ ", companyName=" + companyName + ", sendEmail=" + sendEmail + ", title=" + title + ", targetUrl="
				+ targetUrl + ", salutation=" + salutation + ", jobTitle=" + jobTitle + ", department=" + department
				+ ", role=" + role + ", spCustomAttribute1=" + spCustomAttribute1 + ", spCustomAttribute2="
				+ spCustomAttribute2 + ", spCustomAttribute3=" + spCustomAttribute3 + ", spCustomAttribute4="
				+ spCustomAttribute4 + ", spCustomAttribute5=" + spCustomAttribute5 + ", vendorCategory="
				+ vendorCategory + ", vendorCode=" + vendorCode + ", profileID=" + profileID + ", status=" + status
				+ ", loginName=" + loginName + ", userType=" + userType + ", userProvisioningRole="
				+ userProvisioningRole + ", language=" + language + ", vendorGroupID=" + vendorUserGroupIDs
				+ ", fairpriceUserGroupID=" + fairpriceUserGroupID + "]";
	}

	@Override
	public void setSystemAccountStatus(SystemAccountStatus systemAccountStatus) {
		this.systemAccountStatus=systemAccountStatus;
	}

	@Override
	public SystemAccountStatus getSystemAccountStatus() { 
		return systemAccountStatus;
	}

	/**
	 * @return the createBy
	 */
	public String getCreateBy() {
		return createBy;
	}

	/**
	 * @param createBy the createBy to set
	 */
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCountry(String country) {
		this.country=country;
	}
	public String getCountry() {
		return country;
	}

	public void setCity(String city) {
		this.city=city;
		
	}
	public void setCompanyCity(String companyCity) {
		this.companyCity=companyCity;
		
	}
	public HashMap<String, String> getAddedProperty() {
		return addedProperty;
	}
	public void setAddedProperty(HashMap<String, String> addedProperty) {
		this.addedProperty = addedProperty;
	}
	public void addProperty(String key, String value){
		if(getAddedProperty()==null){
			this.addedProperty=new HashMap<String,String>();
		}
		boolean proceed = !(key.equalsIgnoreCase(FPSCIProfileXmlSaxParserTransformer.UserMetaDataProperty.NONE.name()));
		if(proceed&&!addedProperty.containsKey(key)){
			addedProperty.put(key, value);
		}
	
	}

	@Override
	public String getSciAccountID() { 
		return userID;
	}

}
