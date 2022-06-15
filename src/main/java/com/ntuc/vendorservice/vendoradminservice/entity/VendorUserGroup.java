package com.ntuc.vendorservice.vendoradminservice.entity;

import com.ntuc.vendorservice.foundationcontext.catalog.entity.AuditEntity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "T_VENDOR_USER_GROUPS") 
@NamedQueries(value = {
	@NamedQuery(name="VendorUserGroup.findAll",query="Select q from VendorUserGroup q") ,
	@NamedQuery(name="VendorUserGroup.findByGroupID",query="Select q from VendorUserGroup q WHERE q.vendorGroupID=:vendorGroupID") 
})
public class VendorUserGroup extends AuditEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long vendorGroupID;
	@Column(length=150)
	private String vendorUserGroupName; 
	@Column(length=300)
	private String vendorUserGroupDesc; 

	public VendorUserGroup() {
		super();
	}
	public long getVendorGroupID() {
		return vendorGroupID;
	}

	public void setVendorGroupID(long vendorGroupID) {
		this.vendorGroupID = vendorGroupID;
	}

	public String getVendorUserGroupName() {
		return vendorUserGroupName;
	}

	public void setVendorUserGroupName(String vendorUserGroupName) {
		this.vendorUserGroupName = vendorUserGroupName;
	}

	public String getVendorUserGroupDesc() {
		return vendorUserGroupDesc;
	}
	public void setVendorUserGroupDesc(String vendorUserGroupDesc) {
		this.vendorUserGroupDesc = vendorUserGroupDesc;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "VendorUserGroup [vendorGroupID=" + vendorGroupID + ", vendorUserGroupName=" + vendorUserGroupName + "]";
	} 
	
	
}
