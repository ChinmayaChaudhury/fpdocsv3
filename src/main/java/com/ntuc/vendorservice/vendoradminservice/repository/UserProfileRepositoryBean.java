package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.entity.UserProfile;

/**
 * Session Bean implementation class UserProfileBean
 */
@Stateless
@LocalBean
public class UserProfileRepositoryBean implements GenericRepositoryBean<UserProfile> {
	@PersistenceContext
	EntityManager em;
	@Override
	public List<UserProfile> findAll() {
		return em.createNamedQuery("UserProfile.findAll", UserProfile.class).getResultList();
	} 
	public  UserProfile findByID(long profileID) {
		try{
			return em.createNamedQuery("UserProfile.findByID", UserProfile.class).setParameter("profileID", profileID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public  UserProfile findByVendorIDSystemUserID(long vendorID,long systemUserID,SystemUserRole systemUserRole) {
		try{
			return em.createNamedQuery("UserProfile.findByVendorIDSystemUserID", UserProfile.class).
					setParameter("systemUserRole", systemUserRole).
					setParameter("vendorID", vendorID).setParameter("systemUserID", systemUserID).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	} 

	@Override
	public UserProfile add(UserProfile e)throws EntityExistsException{
	    UserProfile findByVendorIDSystemUserID = findByVendorIDSystemUserID(e.getVendorID(),e.getSystemUserID(),e.getSystemUserRole());
		if (findByVendorIDSystemUserID==null) {
			em.persist(e);
			em.flush();
			return e;
		} else{
			throw new javax.persistence.EntityExistsException();
		} 
	}

	@Override
	public UserProfile update(UserProfile e) throws javax.persistence.EntityNotFoundException {
		Query query = em.createQuery(
					"UPDATE UserProfile v SET "
					+ "v.systemUserID =:systemUserID , "
					+ "v.userName =:userName  ,"
					+ "v.firstName =:firstName  ,"
					+ "v.lastName =:lastName  ," 
					+ "v.position =:position  ,"
					+ "v.systemUserRole =:systemUserRole  "
					+ "WHERE v.profileID=:profileID");
		    query.setParameter("systemUserRole", e.getSystemUserRole());
			query.setParameter("firstName", e.getFirstName());
			query.setParameter("lastName", e.getLastName());
			query.setParameter("position", e.getPosition());
			query.setParameter("userName", e.getUserName());
			query.setParameter("systemUserID", e.getSystemUserID());
			query.setParameter("profileID", e.getProfileID());
            int executeUpdate = query.executeUpdate();
            if(executeUpdate>0){
            	return e;
            	
		    }  throw new javax.persistence.EntityNotFoundException();
	} 

}
