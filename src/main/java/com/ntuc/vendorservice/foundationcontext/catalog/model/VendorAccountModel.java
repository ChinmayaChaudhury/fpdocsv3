package com.ntuc.vendorservice.foundationcontext.catalog.model;

public class VendorAccountModel {
	
	private String vendorCode;
	private String vendorName;
	private String vendorID;
	private String vendorUserID;
	private boolean isVendorAdmin;
	private boolean isVendorUser;
	private String groupsAssigned;
	private String groupsNames;
	
	public String getGroupsNames() {
		return groupsNames;
	}
	public void setGroupsNames(String groupsNames) {
		this.groupsNames = groupsNames;
	}
	public String getVendorUserID() {
		return vendorUserID;
	}
	public void setVendorUserID(String vendorUserID) {
		this.vendorUserID = vendorUserID;
	}
	public boolean isVendorUser() {
		return isVendorUser;
	}
	public void setVendorUser(boolean isVendorUser) {
		this.isVendorUser = isVendorUser;
	}
	public String getGroupsAssigned() {
		return groupsAssigned;
	}
	public void setGroupsAssigned(String groupsAssigned) {
		this.groupsAssigned = groupsAssigned;
	}
	public boolean isVendorAdmin() {
		return isVendorAdmin;
	}
	public void setVendorAdmin(boolean isVendorAdmin) {
		this.isVendorAdmin = isVendorAdmin;
	}
	public String getVendorID() {
		return vendorID;
	}
	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	@Override
	public String toString() {
		return "VendorAccountModel{" +
				"vendorCode='" + vendorCode + '\'' +
				", vendorName='" + vendorName + '\'' +
				", vendorID='" + vendorID + '\'' +
				", vendorUserID='" + vendorUserID + '\'' +
				", isVendorAdmin=" + isVendorAdmin +
				", isVendorUser=" + isVendorUser +
				", groupsAssigned='" + groupsAssigned + '\'' +
				", groupsNames='" + groupsNames + '\'' +
				'}';
	}
}
