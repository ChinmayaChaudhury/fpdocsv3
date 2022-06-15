package com.ntuc.vendorservice.scimservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * CWF application group
 * @author I305675
 *
 */
@Entity
@Table(name="T_CWFAPPLICATION")
@NamedQueries(value = {
		@NamedQuery(name="CWFApplicationGroup.findAll",query="Select q from CWFApplicationGroup q") ,
		@NamedQuery(name="CWFApplicationGroup.findByID",query="Select q from CWFApplicationGroup q WHERE q.id=:id")	})
public class CWFApplicationGroup implements Serializable{ 
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length=60,name="SCI_GROUP_ID")
	private String id; 
	@Column(length=100)
	private String value;
	@Column(length=150)
	private String display; 
	@Column(length=250)
	private String description;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}
	/**
	 * @param display the display to set
	 */
	public void setDisplay(String display) {
		this.display = display;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
		CWFApplicationGroup other = (CWFApplicationGroup) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	} 
	 
}
