package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ntuc.vendorservice.vendoradminservice.entity.VendorUserGroup;

/**
 * Session Bean implementation class VendorUserGroupBean
 */
@Stateless
@LocalBean
public class VendorUserGroupBean { 
	@PersistenceContext
	EntityManager em;
	/**
	 * List all the available VendorUserGroup
	 * 
	 * @return
	 */
	public List<VendorUserGroup> findAll() {
		return em.createNamedQuery("VendorUserGroup.findAll", VendorUserGroup.class).getResultList();
	}

	/**
	 * Create new vendor groups
	 * 
	 * @param vendorUserGroup
	 */
	public VendorUserGroup add(VendorUserGroup vendorUserGroup) {
		em.persist(vendorUserGroup);
		em.flush();
		return vendorUserGroup;
	}

	public VendorUserGroup findByGroupID(long vendorGroupID) {
		return em.createNamedQuery("VendorUserGroup.findByGroupID", VendorUserGroup.class)
				.setParameter("vendorGroupID", vendorGroupID).getSingleResult();
	}
 

}
