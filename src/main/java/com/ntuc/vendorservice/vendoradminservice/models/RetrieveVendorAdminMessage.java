package com.ntuc.vendorservice.vendoradminservice.models;

import java.util.Date;
import java.util.List;


import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;

public class RetrieveVendorAdminMessage {
	
	private long vendorID;
	private List<String> selectedGroups;
	private String vendorName; 
	private String vendorCode;
	private String sciAccountID;
	private String work;
	private String mobile;
	private String vendorAdminEmail;
	private boolean accountValidated;
	private Date vendorActivatedDate;
	private VendorCategory vendorCategory;
	private SystemAccountStatus systemAccountStatus;
	private boolean inheritCategoryQuota;  
	private Long allocatedQuota;
	private String vendorEmailDomain;
	private double quotaUtilization; 
	private boolean validateEmailDomain;
	private String createdBy;
	private String activatedBy;
	private Date createDateTime;
	
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getActivatedBy() {
		return activatedBy;
	}
	public void setActivatedBy(String activatedBy) {
		this.activatedBy = activatedBy;
	}
	public Date getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	public long getVendorID() {
		return vendorID;
	}
	public void setVendorID(long vendorID) {
		this.vendorID = vendorID;
	}
	public List<String> getSelectedGroups() {
		return selectedGroups;
	}
	public void setSelectedGroups(List<String> selectedGroups) {
		this.selectedGroups = selectedGroups;
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
	public String getSciAccountID() {
		return sciAccountID;
	}
	public void setSciAccountID(String sciAccountID) {
		this.sciAccountID = sciAccountID;
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
	public SystemAccountStatus getSystemAccountStatus() {
		return systemAccountStatus;
	}
	public void setSystemAccountStatus(SystemAccountStatus systemAccountStatus) {
		this.systemAccountStatus = systemAccountStatus;
	}
	public boolean isInheritCategoryQuota() {
		return inheritCategoryQuota;
	}
	public void setInheritCategoryQuota(boolean inheritCategoryQuota) {
		this.inheritCategoryQuota = inheritCategoryQuota;
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
	public double getQuotaUtilization() {
		return quotaUtilization;
	}
	public void setQuotaUtilization(double quotaUtilization) {
		this.quotaUtilization = quotaUtilization;
	}
	public boolean isValidateEmailDomain() {
		return validateEmailDomain;
	}
	public void setValidateEmailDomain(boolean validateEmailDomain) {
		this.validateEmailDomain = validateEmailDomain;
	}

}
