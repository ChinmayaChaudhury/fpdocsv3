package com.ntuc.vendorservice.scimservice.entity;

import java.io.Serializable;
 
import javax.persistence.*;

/**
 * Entity implementation class for Entity: CWFGroupToAppMapping
 *
 */
@Entity
@Table(name="T_CWFGRP_APP_MAPPING",uniqueConstraints=
@UniqueConstraint(columnNames={"COMP_ID", "SCI_GROUP_ID"})) 
@NamedQueries({
	@NamedQuery(name = "CWFGroupToCompositeMapping.findAll", query = "select u from CWFGroupToCompositeMapping u"),
	@NamedQuery(name = "CWFGroupToCompositeMapping.findByApplication", query = "select u from CWFGroupToCompositeMapping u where u.compositeAuthGroup = :compositeAuthGroup"),
	@NamedQuery(name = "CWFGroupToCompositeMapping.findByApplicationGroup", query = "select u from CWFGroupToCompositeMapping u where u.applicationGroup = :applicationGroup"),
	@NamedQuery(name = "CWFGroupToCompositeMapping.findGroupsByComposition", query = "select u.applicationGroup from CWFGroupToCompositeMapping u where u.compositeAuthGroup = :compositeAuthGroup")})

public class CWFGroupToCompositeMapping implements Serializable {  
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	@JoinColumn(name="COMP_ID")
	private CWFCompositeAuthGroup compositeAuthGroup;
	@ManyToOne
	@JoinColumn(name="SCI_GROUP_ID")
	private CWFApplicationGroup applicationGroup;
	public CWFGroupToCompositeMapping() {
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
	public CWFCompositeAuthGroup getCompositeAuthGroup() {
		return compositeAuthGroup;
	}
	/**
	 * @param application the application to set
	 */
	public void setCompositeAuthGroup(CWFCompositeAuthGroup compositeAuthGroup) {
		this.compositeAuthGroup = compositeAuthGroup;
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
