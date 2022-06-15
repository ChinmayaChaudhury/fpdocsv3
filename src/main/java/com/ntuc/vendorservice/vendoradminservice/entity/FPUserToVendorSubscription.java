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
@Table(name = "T_FAIRPRICE_VENDOR_SUBSC", uniqueConstraints = @UniqueConstraint(columnNames = {
		"FAIRPRICEUSERID_FAIRPRICEUSERID", "VENDORID_VENDORID" }) )
@NamedQueries({
		@NamedQuery(name = "FPUserToVendorSubscription.findAll", query = "select u from FPUserToVendorSubscription u"),
		@NamedQuery(name = "FPUserToVendorSubscription.findByFairPriceUserID", query = "select u from FPUserToVendorSubscription u where u.fairPriceUserID = :fairPriceUserID"),
		@NamedQuery(name = "FPUserToVendorSubscription.findByVendorID", query = "select u from FPUserToVendorSubscription u where u.vendorID = :vendorID "),
		@NamedQuery(name = "FPUserToVendorSubscription.findByFairPriceUserByVendorID", query = "select u.fairPriceUserID from FPUserToVendorSubscription u where u.vendorID = :vendorID ") })

public class FPUserToVendorSubscription extends AuditEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long subscriptionID;
	private VendorAccount vendorID;
	private InternalUser fairPriceUserID;
	private static final long serialVersionUID = 1L;

	public FPUserToVendorSubscription() {
		super();
	}

	public VendorAccount getVendorID() {
		return this.vendorID;
	}

	public void setVendorID(VendorAccount vendorID) {
		this.vendorID = vendorID;
	}

	public InternalUser getFairPriceUserID() {
		return this.fairPriceUserID;
	}

	public void setFairPriceUserID(InternalUser fairPriceUserID) {
		this.fairPriceUserID = fairPriceUserID;
	}

	public Long getSubscriptionID() {
		return this.subscriptionID;
	}

	public void setSubscriptionID(Long subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FPUserToVendorSubscription [subscriptionID=" + subscriptionID + ", vendorID=" + vendorID
				+ ", fairPriceUserID=" + fairPriceUserID + "]";
	}

}
