package com.ntuc.vendorservice.vendoradminservice.entity;

import com.ntuc.vendorservice.foundationcontext.catalog.entity.AuditEntity;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.VendorAccountStatus;
import com.ntuc.vendorservice.foundationcontext.catalog.model.NotificationRequest;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: TempVendorAccount
 *
 */
@Entity
@Table(name = "T_PROSPECT_VENDOR_ACCOUNT", uniqueConstraints = @UniqueConstraint(columnNames = { "VENDORCODE",
		"LOGINNAME", "SCIUSERID", "ORGANIZATIONWIDEID" }))
@NamedQueries(value = {
		@NamedQuery(name = "TempVendorAccount.findAll", query = "Select q from ProspectVendorAccount q"),
		@NamedQuery(name = "TempVendorAccount.findByStatus", query = "Select q from ProspectVendorAccount q WHERE q.vendorAccountStatus=:vendorAccountStatus"),
		@NamedQuery(name = "TempVendorAccount.findByEmail", query = "Select q from ProspectVendorAccount q WHERE q.email=:email"),
		@NamedQuery(name = "TempVendorAccount.findBySciUserID", query = "Select q from ProspectVendorAccount q WHERE q.sciUserID=:sciUserID"),
		@NamedQuery(name = "TempVendorAccount.findByAccountID", query = "Select q from ProspectVendorAccount q WHERE q.accountID=:accountID") })
public class ProspectVendorAccount extends AuditEntity implements Serializable, NotificationRequest, SystemAccount {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator = "TEMP_USER_SEQ")
	@SequenceGenerator(name = "TEMP_USER_SEQ", sequenceName = "TEMP_USER_SEQ_ID", allocationSize = 1)
	private Long accountID;
	@Column(length = 100)
	private String firstName;
	@Column(length = 100)
	private String lastName;
	@Column(length = 100)
	private String displayName;
	@Column(length = 100)
	private String email;
	@Column(length = 100, unique = true, nullable = true)
	private String vendorCode;
	@Column(length = 150)
	private String companyName;
	@Column(length = 150)
	private String department;
	@Column(length = 150)
	private String industry;
	@Column(length = 5)
	private String salutation;
	@Column(length = 60, unique = true)
	private String loginName;
	@Column(length = 60, unique = true)
	private String sciUserID;
	@Column(length = 60, unique = true)
	private String userID;
	@Column(length = 60, unique = true)
	private String organisationWideID;
	@Enumerated(EnumType.STRING)
	@Column(length = 60)
	private VendorAccountStatus vendorAccountStatus;
	private boolean emailSent;
	@Column(length = 60, unique = true)
	private String newSciUserID;
	@Column(length = 400, unique = true)
	private String activationLink;
	@Column(length = 100)
	private String telephone;
	@Transient
	private String fax; 
	@Transient
	private String mobile;
	@Transient
	private List<String> groups;

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone
	 *            the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public ProspectVendorAccount() {
		super();
	}

	/**
	 * @return the accountID
	 */
	public Long getAccountID() {
		return accountID;
	}

	/**
	 * @param accountID
	 *            the accountID to set
	 */
	public void setAccountID(Long accountID) {
		this.accountID = accountID;
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
	 * @return the vendorCode
	 */
	public String getVendorCode() {
		return vendorCode;
	}

	/**
	 * @param vendorCode
	 *            the vendorCode to set
	 */
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
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

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName
	 *            the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the sciUserID
	 */
	public String getSciUserID() {
		return sciUserID;
	}

	/**
	 * @param sciUserID
	 *            the sciUserID to set
	 */
	public void setSciUserID(String sciUserID) {
		this.sciUserID = sciUserID;
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
	 * @return the organisationWideID
	 */
	public String getOrganisationWideID() {
		return organisationWideID;
	}

	/**
	 * @param organisationWideID
	 *            the organisationWideID to set
	 */
	public void setOrganisationWideID(String organisationWideID) {
		this.organisationWideID = organisationWideID;
	}

	/**
	 * @return the vendorAccountStatus
	 */
	public VendorAccountStatus getVendorAccountStatus() {
		return vendorAccountStatus;
	}

	/**
	 * @param vendorAccountStatus
	 *            the vendorAccountStatus to set
	 */
	public void setVendorAccountStatus(VendorAccountStatus vendorAccountStatus) {
		this.vendorAccountStatus = vendorAccountStatus;
	}

	/**
	 * @return the emailSent
	 */
	public boolean isEmailSent() {
		return emailSent;
	}

	/**
	 * @param emailSent
	 *            the emailSent to set
	 */
	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	/**
	 * @return the newSciUserID
	 */
	public String getNewSciUserID() {
		return newSciUserID;
	}

	/**
	 * @param newSciUserID
	 *            the newSciUserID to set
	 */
	public void setNewSciUserID(String newSciUserID) {
		this.newSciUserID = newSciUserID;
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
	 * @return the industry
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * @param industry
	 *            the industry to set
	 */
	public void setIndustry(String industry) {
		this.industry = industry;
	}

	/**
	 * @return the activationLink
	 */
	public String getActivationLink() {
		return activationLink;
	}

	/**
	 * @param activationLink
	 *            the activationLink to set
	 */
	public void setActivationLink(String activationLink) {
		this.activationLink = activationLink;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		if (groups == null) {
			groups = new ArrayList<String>();
		}
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
 
	@Override
	public String getEmailAddress() {
		return getEmail();
	}
	
	@Override
	public long getToUserID() {
		return getAccountID();
	}
	
	/**
	 * @return the systemAccountStatus
	 */
	@Override
	public SystemAccountStatus getSystemAccountStatus() {
		return SystemAccountStatus.ACTIVE;
	}
	
	/**
	 * @param systemAccountStatus
	 *            the systemAccountStatus to set
	 */
	public void setSystemAccountStatus(SystemAccountStatus systemAccountStatus) {
		//TODO Auto-generated method stub
	}

	@Override
	public HashMap<String, Object> getAccountProfileDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSciAccountID() {
		return sciUserID;
	}

}
