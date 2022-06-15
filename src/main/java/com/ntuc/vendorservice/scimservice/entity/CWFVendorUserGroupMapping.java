package com.ntuc.vendorservice.scimservice.entity;

import java.io.Serializable;
 
import javax.persistence.*;

import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;

/**
 * Entity implementation class for Entity: CWFVendorUserGroupMapping
 *
 */
@Entity
@Table(name="T_CWF_VENDOR_GRP_MAPPING",uniqueConstraints=
@UniqueConstraint(columnNames={"VENDORUSERID", "SCI_GROUP_ID"})) 
@NamedQueries({
	@NamedQuery(name = "CWFVendorUserGroupMapping.findAll", query = "select u from CWFVendorUserGroupMapping u"), 
	@NamedQuery(name = "CWFVendorUserGroupMapping.findGroupMappingByVendorUser", query = "select u from CWFVendorUserGroupMapping u where u.vendorUser = :vendorUser"), 
	@NamedQuery(name = "CWFVendorUserGroupMapping.findByApplicationGroup", query = "select u from CWFVendorUserGroupMapping u where u.applicationGroup = :applicationGroup"),
	@NamedQuery(name = "CWFVendorUserGroupMapping.findGroupsByVendorUser", query = "select u.applicationGroup from CWFVendorUserGroupMapping u where u.vendorUser = :vendorUser")})

public class CWFVendorUserGroupMapping implements Serializable {  
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	@JoinColumn(name="VENDORUSERID")
	private VendorUser vendorUser;
	@ManyToOne
	@JoinColumn(name="SCI_GROUP_ID")
	private CWFApplicationGroup applicationGroup;
	public CWFVendorUserGroupMapping() {
		super();
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	} 
	
	/**
	 * @return the application
	 */
	public VendorUser getVendorUser() {
		return vendorUser;
	}
	/**
	 * @param application the application to set
	 */
	public void setVendorUser(VendorUser vendorUser) {
		this.vendorUser = vendorUser;
	}
	/**
	 * @return the applicationGroup
	 */
	public CWFApplicationGroup getApplicationGroup() {
		return applicationGroup;
	}
	/**
	 * @param applicationGroup the applicationGroup to set
	 */
	public void setApplicationGroup(CWFApplicationGroup applicationGroup) {
		this.applicationGroup = applicationGroup;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	 
}
