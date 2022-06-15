package com.ntuc.vendorservice.scimservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "APPLICATION_GROUPS") 
@NamedQueries(value = { 
		@NamedQuery(name = "ApplicationGroupsLookup.findGroupsByType", query = "SELECT v FROM ApplicationGroupsLookup v WHERE v.type=:type"),
		@NamedQuery(name = "ApplicationGroupsLookup.getGroupDesc", query = "SELECT v FROM ApplicationGroupsLookup v WHERE v.groupName=:groupName")
})
public class ApplicationGroupsLookup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;
	
	@Column(length=20)
	private String type; 
	
	@Column(length=60)
	private String groupName; 
	@Column(length=80)
	private String groupDesc; 

	public ApplicationGroupsLookup() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
