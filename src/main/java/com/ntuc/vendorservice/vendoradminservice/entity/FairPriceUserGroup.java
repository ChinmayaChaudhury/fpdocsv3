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
@Table(name = "T_FP_USER_GROUPS") 
@NamedQueries(value = {
	@NamedQuery(name="FairPriceUserGroup.findAll",query="Select q from FairPriceUserGroup q") 
})
public class FairPriceUserGroup extends AuditEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long fairPriceGroupID;
	@Column(length=120)
	private String fairPriceUserGroupName;
	@Column(length=300)
	private String fairPriceUserGroupDesc;

	public FairPriceUserGroup() {
		super();
	}
	public long getFairPriceGroupID() {
		return fairPriceGroupID;
	}

	public void setFairPriceGroupID(long fairPriceGroupID) {
		this.fairPriceGroupID = fairPriceGroupID;
	}

	public String getFairPriceUserGroupName() {
		return fairPriceUserGroupName;
	}

	public void setFairPriceUserGroupName(String fairPriceUserGroupName) {
		this.fairPriceUserGroupName = fairPriceUserGroupName;
	}
	public String getFairPriceUserGroupDesc() {
		return fairPriceUserGroupDesc;
	}
	public void setFairPriceUserGroupDesc(String fairPriceUserGroupDesc) {
		this.fairPriceUserGroupDesc = fairPriceUserGroupDesc;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
