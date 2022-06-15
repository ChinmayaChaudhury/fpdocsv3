package com.ntuc.vendorservice.vendoradminservice.entity;

import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

@Entity
@Table(name = "T_USER_PROFILE")
@NamedQueries({ @NamedQuery(name = "UserProfile.findAll", query = "select u from UserProfile u"),
		@NamedQuery(name = "UserProfile.findByID", query = "select u from UserProfile u where u.profileID = :profileID"),
		@NamedQuery(name = "UserProfile.findByVendorIDSystemUserID", query = "select u from UserProfile u where u.systemUserRole = :systemUserRole AND u.vendorID = :vendorID AND u.systemUserID = :systemUserID") })
public class UserProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long profileID;
	@Column(length = 30)
	private Long systemUserID;
	@Column(length = 30)
	private Long vendorID;
	@Column(length = 120)
	private String userName;
	@Column(length = 120)
	private String firstName;
	@Column(length = 120)
	private String lastName;
	@Column(length = 120)
	private String position;
	@Transient
	private String email;
	@Transient
	private String displayName;
	@Transient
	private boolean accountActivated;
	@Transient
	private List<VendorAccount> account;
	@Transient
	private String vendorCode;
	@Transient
	private String loginName;
	@Enumerated(EnumType.STRING)
	@Column(length = 60)
	private SystemUserRole systemUserRole;
	@Temporal(TemporalType.DATE)
	protected Date createDateTime;

	 

	/**
	 * @return the profileID
	 */
	public Long getProfileID() {
		return profileID;
	}

	/**
	 * @return the vendorID
	 */
	public Long getVendorID() {
		return vendorID;
	}

	public long getSystemUserID() {
		return systemUserID;
	}

	public void setSystemUserID(Long systemUserID) {
		this.systemUserID = systemUserID;
	} 
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public SystemUserRole getSystemUserRole() {
		return systemUserRole;
	}

	public void setSystemUserRole(SystemUserRole systemUserRole) {
		this.systemUserRole = systemUserRole;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isAccountActivated() {
		return accountActivated;
	}

	public void setAccountActivated(boolean accountActivated) {
		this.accountActivated = accountActivated;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}  
	/**
	 * @return the account
	 */
	public List<VendorAccount> getAccount() {
		if(account==null){
			account=new ArrayList<VendorAccount>();
		}
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(List<VendorAccount> account) {
		this.account = account;
	}

	/**
	 * @param profileID the profileID to set
	 */
	public void setProfileID(Long profileID) {
		this.profileID = profileID;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @param vendorID the vendorID to set
	 */
	public void setVendorID(Long vendorID) {
		this.vendorID = vendorID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserProfile [profileID=" + profileID + ", systemUserID=" + systemUserID + ", vendorID=" + vendorID
				+ ", userName=" + userName + ", firstName=" + firstName + ", lastName=" + lastName + ", position="
				+ position + ", email=" + email + ", displayName=" + displayName + ", accountActivated="
				+ accountActivated + ", account=" + account + ", vendorCode=" + vendorCode + ", systemUserRole="
				+ systemUserRole + ", createDateTime=" + createDateTime + "]";
	}
	
}
