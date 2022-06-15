package com.ntuc.vendorservice.vendoradminservice.entity;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.ntuc.vendorservice.foundationcontext.catalog.entity.AuditEntity;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.foundationcontext.catalog.model.NotificationRequest;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;

@Entity
@Table(name = "T_VENDOR_ADMIN")
@NamedQueries(value = {
		@NamedQuery(name = "DSVendorAdministrator.findAll", query = "SELECT v FROM VendorAccountAdministrator v"),
		@NamedQuery(name = "DSVendorAdministrator.findByVendorUserEmail", query = "Select q from VendorAccountAdministrator q where q.vendorUserEmail=:vendorUserEmail"),
		@NamedQuery(name = "DSVendorAdministrator.findByVendorAdminID", query = "Select q from VendorAccountAdministrator q where q.vendorAdminID=:vendorAdminID"),
		@NamedQuery(name = "DSVendorAdministrator.findByVendorUserName", query = "Select q from VendorAccountAdministrator q where q.userName=:userName"),
		@NamedQuery(name = "DSVendorAdministrator.findBySciAccountID", query = "Select q from VendorAccountAdministrator q where q.sciAccountID=:sciAccountID") })

public class VendorAccountAdministrator extends AuditEntity implements Serializable, NotificationRequest, SystemAccount {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long vendorAdminID;
	@Column(length = 150)
	private String firstName;
	@Column(length = 150)
	private String lastName;
	@Column(length = 150)
	private String jobTitle;
	@Column(length = 60, unique = true)
	private String sciAccountID;
	@Column(length=150)
	private String userName;
	@Column(length = 150, unique = true)
	private String vendorUserEmail;
	@Column(length = 100)
	@Enumerated(EnumType.STRING)
	private SystemUserRole systemUserRole;
	@Enumerated(EnumType.STRING)
	@Column(length = 60)
	private SystemAccountStatus systemAccountStatus;
	private boolean accountValidated; 
	@Column(name="ALLOW_DOC_SHARE")
	private boolean allowDocumentShare;

	public VendorAccountAdministrator() {
		super();
	}

	public long getVendorAdminID() {
		return vendorAdminID;
	}

	public void setVendorAdminID(long vendorAdminID) {
		this.vendorAdminID = vendorAdminID;
	} 
	
	public String getVendorUserEmail() {
		return vendorUserEmail;
	}

	public void setVendorUserEmail(String vendorUserEmail) {
		this.vendorUserEmail = vendorUserEmail;
	}

	public SystemUserRole getSystemUserRole() {
		return systemUserRole;
	}

	public void setSystemUserRole(SystemUserRole systemUserRole) {
		this.systemUserRole = systemUserRole;
	}

	public boolean isAccountValidated() {
		return accountValidated;
	}

	public void setAccountValidated(boolean accountValidated) {
		this.accountValidated = accountValidated;
	}

	@Override
	public String getEmailAddress() {
		return getVendorUserEmail();
	}

	/**
	 * @return the jobTitle
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @param jobTitle
	 *            the jobTitle to set
	 */
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
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
	 * @return the sciAccountID
	 */
	public String getSciAccountID() {
		return sciAccountID;
	}

	/**
	 * @param sciAccountID
	 *            the sciAccountID to set
	 */
	public void setSciAccountID(String sciAccountID) {
		this.sciAccountID = sciAccountID;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public HashMap<String, Object> getAccountProfileDetails() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(SystemAccount.KEY_SYSTEM_ROLE, getSystemUserRole());
		hashMap.put(SystemAccount.KEY_USER_ID, getVendorAdminID()); 
		hashMap.put(SystemAccount.KEY_DISPLAY_NAME, getDisplayName());
		hashMap.put(SystemAccount.KEY_EMAIL, getEmailAddress());
		hashMap.put(SystemAccount.KEY_ACCOUNT_ACTIVATED, isAccountValidated());
		return hashMap;
	}

	@Override
	public String getDisplayName() {
		return getFirstName().concat(" ").concat(getLastName());
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public long getToUserID() {
		return getVendorAdminID();
	}

	/**
	 * @return the systemAccountStatus
	 */
	public SystemAccountStatus getSystemAccountStatus() {
		return systemAccountStatus;
	}

	/**
	 * @param systemAccountStatus
	 *            the systemAccountStatus to set
	 */
	public void setSystemAccountStatus(SystemAccountStatus systemAccountStatus) {
		this.systemAccountStatus = systemAccountStatus;
	}

	/**
	 * @return the allowDocumentsShare
	 */
	public boolean isAllowDocumentShare() {
		return allowDocumentShare;
	}

	/**
	 * @param allowDocumentsShare the allowDocumentsShare to set
	 */
	public void setAllowDocumentShare(boolean allowDocumentShare) {
		this.allowDocumentShare = allowDocumentShare;
	}

}
