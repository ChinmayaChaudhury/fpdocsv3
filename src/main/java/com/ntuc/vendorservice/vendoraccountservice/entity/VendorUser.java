package com.ntuc.vendorservice.vendoraccountservice.entity;

import java.io.Serializable;
import java.util.HashMap;
 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ntuc.vendorservice.foundationcontext.catalog.entity.AuditEntity;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.foundationcontext.catalog.model.NotificationRequest;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorUserGroup;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;

@Entity
@Table(name = "T_VENDOR_USERS")
@NamedQueries(value = { @NamedQuery(name = "DSVendorUser.findAll", query = "SELECT v FROM VendorUser v"),
		@NamedQuery(name = "DSVendorUser.findAllByVendorID", query = "SELECT v FROM VendorUser v WHERE v.vendorAccount=:vendorAccount "),
		@NamedQuery(name = "DSVendorUser.findByVendorUserID", query = "SELECT v FROM VendorUser v WHERE v.vendorUserID=:vendorUserID "),
		@NamedQuery(name = "DSVendorUser.findAllByVendorIDAndVendorUserGroup", query = "SELECT v FROM VendorUser v inner join v.vendorUserGroups g WHERE v.vendorAccount=:vendorAccount AND g.vendorGroupID=:vendorGroupID"),
		@NamedQuery(name = "DSVendorUser.findByVendorUserEmail", query = "Select q from VendorUser q where q.vendorUserEmail=:vendorUserEmail"),
		@NamedQuery(name = "DSVendorUser.findBySciAccountID", query = "Select q from VendorUser q where q.sciAccountID=:sciAccountID"),
		@NamedQuery(name = "DSVendorUser.findByVendorUserIDAndVendorID", query = "Select q from VendorUser q where q.vendorUserID=:vendorUserID AND q.vendorAccount=:vendorAccount"),
		@NamedQuery(name = "DSVendorUser.findByVendorUserEmailAndVendorID", query = "Select q from VendorUser q where q.vendorUserEmail=:vendorUserEmail AND q.vendorAccount=:vendorAccount")

})
public class VendorUser extends AuditEntity implements Serializable, NotificationRequest, SystemAccount {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	@Id
	@GeneratedValue
	private Long vendorUserID;
	@Column(length = 150)
	private String firstName;
	@Column(length = 150)
	private String lastName;
	@Column(length = 150)
	private String jobTitle;
	@Column(length = 60)
	private String sciAccountID;
	@Column(length = 150)
	private String vendorUserEmail;
	@Column(length = 100)
	@Enumerated(EnumType.STRING)
	private SystemUserRole systemUserRole;
	@Enumerated(EnumType.STRING)
	@Column(length = 60)
	private SystemAccountStatus systemAccountStatus;
	private boolean accountValidated; 
	@Column(length = 150)
	private String vendorCode;
	@Column(length = 150)
	private String userName;
	@ManyToOne
	@JoinColumn(name = "vendorAccount_vendorID", referencedColumnName = "vendorID", nullable = true)
	private VendorAccount vendorAccount;
	@Transient
	private Collection<Long> vendorUserGroupIDs;
	@ManyToMany  
	@JoinTable
	private Collection<VendorUserGroup> vendorUserGroups;

	public VendorUser() {
		super();
	}

	public Long getVendorUserID() {
		return vendorUserID;
	}

	public void setVendorUserID(Long vendorUserID) {
		this.vendorUserID = vendorUserID;
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
		hashMap.put(SystemAccount.KEY_USER_ID, getVendorUserID());
		hashMap.put(SystemAccount.KEY_VENDOR_ID, getVendorAccount().getVendorID());
		hashMap.put(SystemAccount.KEY_DISPLAY_NAME, getDisplayName());
		hashMap.put(SystemAccount.KEY_EMAIL, getEmailAddress());
		hashMap.put(SystemAccount.KEY_ACCOUNT_ACTIVATED, isAccountValidated());
		return hashMap;
	}

	@Override
	public String getDisplayName() {
		return getFirstName().concat(" ").concat(getLastName());
	}

	@Override
	public long getToUserID() {
		return getVendorUserID();
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
	 * @param vendorAccount
	 *            the vendorAccount to set
	 */
	public void setVendorAccount(VendorAccount vendorAccount) {
		this.vendorAccount = vendorAccount;
	}

	/**
	 * @param vendorID
	 *            the vendorAccount to set
	 */
	public void setVendorAccount(Long vendorID) {
		VendorAccount vendorAccount = new VendorAccount();
		vendorAccount.setVendorID(vendorID);
		this.vendorAccount = vendorAccount;
	}

	public Long getVendorID() {
		return vendorAccount.getVendorID();
	}

	public VendorAccount getVendorAccount() {
		return vendorAccount;
	}

	public Collection<VendorUserGroup> getVendorUserGroups() {
		if(vendorUserGroups==null){
			vendorUserGroups=new  ArrayList<VendorUserGroup>();
		}  
	    return vendorUserGroups;
	}

	public void setVendorUserGroups(Collection<VendorUserGroup> param) {
	    this.vendorUserGroups = param;
	}
	
	/**
	 * @param vendorUserGroupIDs the vendorUserGroupIDs to set
	 */
	public void setVendorUserGroupIDs(Collection<Long> vendorUserGroupIDs) {
		this.vendorUserGroupIDs = vendorUserGroupIDs;
	}

	/**
	 * @return the vendorUserGroupIDs
	 */
	public Collection<Long> getVendorUserGroupIDs() {
		if(vendorUserGroupIDs==null){
			vendorUserGroupIDs=new ArrayList<Long>();
		}  
		return vendorUserGroupIDs;
	}

	public void setVendorUserGroupID(Collection<VendorUserGroup> userGroups) {
		for(VendorUserGroup userGroup:userGroups){  
			getVendorUserGroupIDs().add(userGroup.getVendorGroupID());
		} 
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
		VendorUser other = (VendorUser) obj;
		if (sciAccountID == null) {
			if (other.sciAccountID != null)
				return false;
		} else if (!sciAccountID.equals(other.sciAccountID))
			return false;
		if (vendorUserEmail == null) {
			if (other.vendorUserEmail != null)
				return false;
		} else if (!vendorUserEmail.equals(other.vendorUserEmail))
			return false;
		return true;
	} 
	
	
}
