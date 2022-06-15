package com.ntuc.vendorservice.vendoradminservice.entity;

import com.ntuc.vendorservice.foundationcontext.catalog.entity.AuditEntity;

import java.io.Serializable;
import java.lang.Long;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: FPUserToVendorSubscription
 *
 */
@Entity
@Table(name = "T_VENDOR_ADMIN_MAPPING", uniqueConstraints = @UniqueConstraint(columnNames = {
		"VENDORACCOUNT_VENDORID", "VENDORADMINISTRATOR_VENDORADMINID"}))
@NamedQueries({
		@NamedQuery(name = "VendorToAdminMapping.findAll", query = "select u from VendorToAdminMapping u"),
		@NamedQuery(name = "VendorToAdminMapping.findByVendorAccount", query = "select u from VendorToAdminMapping u where u.vendorAccount = :vendorAccount"),
		@NamedQuery(name = "VendorToAdminMapping.findByVendorAdministrator", query = "select u from VendorToAdminMapping u where u.vendorAccountAdministrator = :vendorAdministrator ") })

public class VendorToAdminMapping extends AuditEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long mappingID; 
	@ManyToOne
	@JoinColumn(name = "VENDORACCOUNT_VENDORID")
	private VendorAccount vendorAccount;
	@ManyToOne
	@JoinColumn(name = "VENDORADMINISTRATOR_VENDORADMINID")
	private VendorAccountAdministrator vendorAccountAdministrator;
	private static final long serialVersionUID = 1L;
	/**
	 * @return the mappingID
	 */
	public Long getMappingID() {
		return mappingID;
	}
	/**
	 * @param mappingID the mappingID to set
	 */
	public void setMappingID(Long mappingID) {
		this.mappingID = mappingID;
	}
	/**
	 * @return the vendorAccount
	 */
	public VendorAccount getVendorAccount() {
		return vendorAccount;
	}
	/**
	 * @param vendorAccount the vendorAccount to set
	 */
	public void setVendorAccount(VendorAccount vendorAccount) {
		this.vendorAccount = vendorAccount;
	}
	/**
	 * @return the vendorAdministrator
	 */
	public VendorAccountAdministrator getVendorAdministrator() {
		return vendorAccountAdministrator;
	}
	/**
	 * @param vendorAccountAdministrator the vendorAdministrator to set
	 */
	public void setVendorAdministrator(VendorAccountAdministrator vendorAccountAdministrator) {
		this.vendorAccountAdministrator = vendorAccountAdministrator;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	} 

}
