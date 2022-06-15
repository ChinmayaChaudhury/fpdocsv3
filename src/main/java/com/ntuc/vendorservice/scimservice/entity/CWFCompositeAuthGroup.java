package com.ntuc.vendorservice.scimservice.entity;

import com.ntuc.vendorservice.foundationcontext.catalog.entity.AuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="T_CWF_COMPOSITE_GROUP") 
@NamedQueries(value = {
	@NamedQuery(name="CWFCompositeAuthGroup.findAll",query="Select q from CWFCompositeAuthGroup q") ,
	@NamedQuery(name="CWFCompositeAuthGroup.findByID",query="Select q from CWFCompositeAuthGroup q WHERE q.id=:id") ,
	@NamedQuery(name="CWFCompositeAuthGroup.findByApplicationType",query="Select q from CWFCompositeAuthGroup q WHERE q.applicationType=:applicationType") 
})
public class CWFCompositeAuthGroup extends AuditEntity {
	@Id
	@GeneratedValue
	@Column(name="COMP_ID")
	private Long id; 
	@Column(length=100, unique=true)
	private String name;
	@Column(length=200)
	private String description;
	@Column(length=100)
	@Enumerated(EnumType.STRING)
	private CWFCompositeAuthType applicationType;
	@Column(name="ACTIVATED")
	private boolean activated;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the applicationType
	 */
	public CWFCompositeAuthType getApplicationType() {
		return applicationType;
	}
	/**
	 * @param applicationType the applicationType to set
	 */
	public void setApplicationType(CWFCompositeAuthType applicationType) {
		this.applicationType = applicationType;
	}
	
	/**
	 * @return the activated
	 */
	public boolean isActivated() {
		return activated;
	}
	/**
	 * @param activated the activated to set
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CWFCompositeAuthGroup other = (CWFCompositeAuthGroup) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	} 

}
