package com.ntuc.vendorservice.vendoradminservice.entity;
 

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Basic;
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

import com.ntuc.vendorservice.foundationcontext.catalog.entity.AuditEntity;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.foundationcontext.catalog.model.NotificationRequest;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.models.VendorCategory;

@Entity
@Table(name = "T_VENDORS")
@NamedQueries(value = {
		@NamedQuery(name="DSVendorAccount.findAll",query="Select q from VendorAccount q"),
		@NamedQuery(name="DSVendorAccount.findByID",query="Select q from VendorAccount q WHERE q.vendorID=:vendorID"),
		@NamedQuery(name="DSVendorAccount.findByVendorCode",query="Select q from VendorAccount q WHERE q.vendorCode=:vendorCode"),
		@NamedQuery(name="DSVendorAccount.findByVendorAdminEmail",query="Select q from VendorAccount q where q.vendorAdminEmail=:vendorAdminEmail"),
		@NamedQuery(name = "DSVendorAccount.findBySciAccountID", query = "Select q from VendorAccount q where q.sciAccountID=:sciAccountID")
	})

public class VendorAccount extends AuditEntity implements NotificationRequest, SystemAccount {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long vendorID;
	@Column(length=120)
	private String vendorName; 
	@Column(length=60,unique=true)
	private String vendorCode;
	@Column(length=60)
	private String sciAccountID;
	@Column(length=15)
	private String work;
	@Column(length=15)
	private String mobile;
	@Column(length=120) 
	private String vendorAdminEmail;
	@Basic
	private boolean accountValidated;
	@Basic
	@Temporal(TemporalType.DATE)
	private Date vendorActivatedDate;
	@Enumerated(EnumType.STRING)
	@Column(length=60)
	private VendorCategory vendorCategory;
	@Enumerated(EnumType.STRING)
	@Column(length=60)
	private SystemAccountStatus systemAccountStatus;
	@Basic
	private boolean inheritCategoryQuota;  
	private Long allocatedQuota;
	@Column(length=60)
	private String vendorEmailDomain;
	@Transient
	private double quotaUtilization; 
	private boolean validateEmailDomain;
	

	public VendorAccount() {
		super();
	}
	public long getVendorID() {
		return vendorID;
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
	public void setVendorID(long vendorID) {
		this.vendorID = vendorID;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorAdminEmail() {
		return vendorAdminEmail;
	}

	public void setVendorAdminEmail(String vendorAdminEmail) {
		this.vendorAdminEmail = vendorAdminEmail;
	}

	public boolean isAccountValidated() {
		return accountValidated;
	}

	public void setAccountValidated(boolean accountValidated) {
		this.accountValidated = accountValidated;
	}

	public Date getVendorActivatedDate() {
		return vendorActivatedDate;
	}

	public void setVendorActivatedDate(Date vendorActivatedDate) {
		this.vendorActivatedDate = vendorActivatedDate;
	}

	public VendorCategory getVendorCategory() {
		return vendorCategory;
	}

	public void setVendorCategory(VendorCategory vendorCategory) {
		this.vendorCategory = vendorCategory;
	}

	public Long getAllocatedQuota() {
		return allocatedQuota;
	}

	public void setAllocatedQuota(Long allocatedQuota) {
		this.allocatedQuota = allocatedQuota;
	}

	public String getVendorEmailDomain() {
		return vendorEmailDomain;
	}

	public void setVendorEmailDomain(String vendorEmailDomain) {
		this.vendorEmailDomain = vendorEmailDomain;
	}

	public boolean isInheritCategoryQuota() {
		return inheritCategoryQuota;
	}

	public void setInheritCategoryQuota(boolean inheritCategoryQuota) {
		this.inheritCategoryQuota = inheritCategoryQuota;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DSVendorAccount [vendorID=" + vendorID + ", vendorName=" + vendorName + ", vendorCode=" + vendorCode
				+ ", sciAccountID=" + sciAccountID + ", vendorAdminEmail=" + vendorAdminEmail + ", accountValidated="
				+ accountValidated + ", vendorActivatedDate=" + vendorActivatedDate + ", vendorCategory="
				+ vendorCategory + ", systemAccountStatus=" + systemAccountStatus + ", inheritCategoryQuota="
				+ inheritCategoryQuota + ", allocatedQuota=" + allocatedQuota + ", vendorEmailDomain="
				+ vendorEmailDomain + ", quotaUtilization=" + quotaUtilization
				+ ", validateEmailDomain=" + validateEmailDomain + "]";
	}
	@Override
	public String getEmailAddress() {
		return getVendorAdminEmail();
	}
	@Override
	public String getDisplayName() { 
		return getVendorName();
	}
	@Override
	public long getToUserID() { 
		return getVendorID();
	}  
	/**
	 * @return the quotaUtilization
	 */
	public double getQuotaUtilization() {
		return quotaUtilization;
	}
	/**
	 * @param quotaUtilization the quotaUtilization to set
	 */
	public void setQuotaUtilization(double quotaUtilization) {
		this.quotaUtilization = quotaUtilization;
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
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public HashMap<String, Object> getAccountProfileDetails() { 
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(SystemAccount.KEY_SYSTEM_ROLE, SystemUserRole.VendorAdministrator);
		hashMap.put(SystemAccount.KEY_USER_ID, getVendorID());
		hashMap.put(SystemAccount.KEY_VENDOR_ID, getVendorID());
		hashMap.put(SystemAccount.KEY_EMAIL, getEmailAddress()); 
		hashMap.put(SystemAccount.KEY_DISPLAY_NAME, getDisplayName());
		hashMap.put(SystemAccount.KEY_ACCOUNT_ACTIVATED, isAccountValidated());
		return hashMap;
	}
	@Override
	public void setSystemAccountStatus(SystemAccountStatus systemAccountStatus) {
		this.systemAccountStatus=systemAccountStatus;
		
	} 
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendorAccount other = (VendorAccount) obj;
		if (sciAccountID == null) {
			if (other.sciAccountID != null)
				return false;
		} else if (!sciAccountID.equals(other.sciAccountID))
			return false;
		if (vendorAdminEmail == null) {
			if (other.vendorAdminEmail != null)
				return false;
		} else if (!vendorAdminEmail.equals(other.vendorAdminEmail))
			return false;
		if (vendorCode == null) {
			if (other.vendorCode != null)
				return false;
		} else if (!vendorCode.equals(other.vendorCode))
			return false;
		return true;
	}
	@Override
	public SystemAccountStatus getSystemAccountStatus() { 
		return systemAccountStatus;
	} 
	/**
	 * @return the validateEmailDomain
	 */
	public boolean isValidateEmailDomain() {
		return validateEmailDomain;
	}
	/**
	 * @param validateEmailDomain the validateEmailDomain to set
	 */
	public void setValidateEmailDomain(boolean validateEmailDomain) {
		this.validateEmailDomain = validateEmailDomain;
	} 

}
