package com.ntuc.vendorservice.vendoradminservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "T_VENDOR_AUTH_GROUPS") 
@NamedQueries(value = {
		@NamedQuery(name = "VendorAuthorizationGroups.getDistinctVendors", 
				query = "SELECT DISTINCT v.vendorCode,v.vendorName FROM VendorAuthorizationGroups v where v.vendorAdminID = :vendorAdminID"),
		@NamedQuery(name = "VendorAuthorizationGroups.getAssignmentsOnVendorAdmin", 
		query = "SELECT v FROM VendorAuthorizationGroups v where v.vendorAdminID = :vendorAdminID and v.vendorCode = :vendorCode and v.groupName = :groupName"),
		@NamedQuery(name = "VendorAuthorizationGroups.getAssignmentsOnVendorUser", 
		query = "SELECT v FROM VendorAuthorizationGroups v where v.vendorUserID = :vendorUserID and v.vendorCode = :vendorCode and v.groupName = :groupName"),
		@NamedQuery(name = "VendorAuthorizationGroups.manageAccessOnVendorUser", 
		query = "SELECT v FROM VendorAuthorizationGroups v where v.vendorUserID = :vendorUserID"),
		@NamedQuery(name = "VendorAuthorizationGroups.manageAccessOnVendorAdmin", 
		query = "SELECT v FROM VendorAuthorizationGroups v where v.vendorAdminID = :vendorAdminID"),
		@NamedQuery(name = "VendorAuthorizationGroups.deleteAuthorizationForVendorUser", 
		query = "DELETE FROM VendorAuthorizationGroups u where u.vendorUserID = :vendorUserID"),
		@NamedQuery(name = "VendorAuthorizationGroups.deleteAuthorizationForVendorAdmin", 
		query = "DELETE FROM VendorAuthorizationGroups u where u.vendorAdminID = :vendorAdminID"),
		@NamedQuery(name = "VendorAuthorizationGroups.getAdminGroupAssignments", 
		query = "SELECT v FROM VendorAuthorizationGroups v where v.vendorAdminID  = :vendorAdminID and v.groupName != :adminGrpName")
	})

public class VendorAuthorizationGroups implements Serializable {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;
	private long vendorAdminID;
	private Long vendorUserID;
	
	@Column(length=50)
	private String vendorCode; 
	
	@Column(length=100)
	private String vendorName; 
	
	@Column(length=60)
	private String groupName; 
	@Column(length=80)
	private String groupDesc; 

	public VendorAuthorizationGroups() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getVendorAdminID() {
		return vendorAdminID;
	}

	public void setVendorAdminID(long vendorAdminID) {
		this.vendorAdminID = vendorAdminID;
	}

	public Long getVendorUserID() {
		return vendorUserID;
	}

	public void setVendorUserID(Long vendorUserID) {
		this.vendorUserID = vendorUserID;
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}
}
