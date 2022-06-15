package com.ntuc.vendorservice.foundationcontext.catalog.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class AuditEntity {
	@Column(length = 60)
	protected String createdBy;
	@Column(length = 60)
	protected String activatedBy;
	@Temporal(TemporalType.DATE)
	protected Date activationDate;
	@Temporal(TemporalType.DATE)
	protected Date createDateTime;

	public AuditEntity() {
		super();
	}
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

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	@Override
	public String toString() {
		return "AuditEntity [createdBy=" + createdBy + ", activatedBy=" + activatedBy + ", activationDate="
				+ activationDate + ", createDateTime=" + createDateTime + "]";
	}

	
}
