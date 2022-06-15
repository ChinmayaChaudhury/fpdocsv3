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

/**
 * Entity implementation class for Entity: FairPriceUser
 *
 */
@Entity
@Table(name = "T_FP_USERS") 
@NamedQueries(value = {
		@NamedQuery(name="FairPriceUser.findAll",query="Select q from InternalUser q"),
		@NamedQuery(name="FairPriceUser.findByID",query="Select q from InternalUser q WHERE q.fairPriceUserID=:fairPriceUserID"),
		@NamedQuery(name="FairPriceUser.findByUserEmail",query="Select q from InternalUser q WHERE q.fairPriceEmail=:fairPriceEmail"),
		@NamedQuery(name="FairPriceUser.findBySCIAccountID",query="Select q from InternalUser q WHERE q.sciAccountID=:sciAccountID"),
		
})
public class InternalUser extends AuditEntity implements Serializable, NotificationRequest,SystemAccount  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long fairPriceUserID; 
	@Column(length=150)
	private String firstName;
	@Column(length=150)
	private String lastName; 
	@Column(length=150)
	private String displayName; 
	@Column(length=150)
	private String jobTitle; 
	@Column(length=60)
	private String sciAccountID; 
	@Column(length=150)
	private String userName;
	private long fairPriceGroupID;
	@Column(length=120)
	private String fairPriceEmail;
	@Enumerated(EnumType.STRING)
	@Column(length=60)
	private SystemUserRole systemUserRole;
	@Enumerated(EnumType.STRING)
	@Column(length=60)
	private SystemAccountStatus systemAccountStatus;
	private boolean accountValidated;

	public InternalUser() {
		super();
	}

	public InternalUser(long fairPriceUserID) {
		super();
		this.fairPriceUserID=fairPriceUserID;
	}
	public long getFairPriceUserID() {
		return fairPriceUserID;
	}

	public void setFairPriceUserID(long fairPriceUserID) {
		this.fairPriceUserID = fairPriceUserID;
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

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
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
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFairPriceEmail() {
		return fairPriceEmail;
	}

	public void setFairPriceEmail(String fairPriceEmail) {
		this.fairPriceEmail = fairPriceEmail;
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

	public long getFairPriceGroupID() {
		return fairPriceGroupID;
	}

	public void setFairPriceGroupID(long fairPriceGroupID) {
		this.fairPriceGroupID = fairPriceGroupID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getEmailAddress() { 
		return getFairPriceEmail();
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getDisplayName() { 
		return displayName;
	}

	@Override
	public long getToUserID() { 
		return getFairPriceUserID();
	}
 
	/**
	 * @return the sciAccountID
	 */
	public String getSciAccountID() {
		return sciAccountID;
	}
	/**
	 * @param sciAccountID the sciAccountID to set
	 */
	public void setSciAccountID(String sciAccountID) {
		this.sciAccountID = sciAccountID;
	} 
	/**
	 * @return the jobTitle
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @param jobTitle the jobTitle to set
	 */
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	

	/**
	 * @return the systemAccountStatus
	 */
	public SystemAccountStatus getSystemAccountStatus() {
		return systemAccountStatus;
	}

	/**
	 * @param systemAccountStatus the systemAccountStatus to set
	 */
	public void setSystemAccountStatus(SystemAccountStatus systemAccountStatus) {
		this.systemAccountStatus = systemAccountStatus;
	}

	@Override
	public HashMap<String, Object> getAccountProfileDetails() { 
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(SystemAccount.KEY_SYSTEM_ROLE, getSystemUserRole());
		hashMap.put(SystemAccount.KEY_USER_ID, getFairPriceUserID());
		hashMap.put(SystemAccount.KEY_VENDOR_ID, 0);
		hashMap.put(SystemAccount.KEY_EMAIL, getEmailAddress()); 
		hashMap.put(SystemAccount.KEY_DISPLAY_NAME, getDisplayName());
		hashMap.put(SystemAccount.KEY_ACCOUNT_ACTIVATED, isAccountValidated());
		return hashMap;
	} 

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InternalUser other = (InternalUser) obj;
		if (sciAccountID == null) {
			if (other.sciAccountID != null)
				return false;
		} else if (!sciAccountID.equals(other.sciAccountID))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DSFairPriceUser [fairPriceUserID=" + fairPriceUserID + ", firstName=" + firstName + ", lastName="
				+ lastName + ", displayName=" + displayName + ", sciAccountID=" + sciAccountID + ", fairPriceGroupID="
				+ fairPriceGroupID + ", fairPriceEmail=" + fairPriceEmail + ", systemUserRole=" + systemUserRole
				+ ", accountActivated=" + accountValidated + "]";
	}

	 

}
