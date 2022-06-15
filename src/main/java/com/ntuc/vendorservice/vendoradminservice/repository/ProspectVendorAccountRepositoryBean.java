package com.ntuc.vendorservice.vendoradminservice.repository;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.ntuc.vendorservice.foundationcontext.repository.DatabaseExceptionsHelper;
import com.ntuc.vendorservice.foundationcontext.repository.GenericRepositoryBean;
import org.apache.commons.lang.StringUtils;

import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.VendorAccountStatus;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;

@Stateless
@LocalBean
public class ProspectVendorAccountRepositoryBean implements GenericRepositoryBean<ProspectVendorAccount> {
	@PersistenceContext
	private EntityManager em;

	@Override
	public List<ProspectVendorAccount> findAll() {
		TypedQuery<ProspectVendorAccount> typedQuery = em.createNamedQuery("TempVendorAccount.findAll",ProspectVendorAccount.class);
		return typedQuery.getResultList();
	}

	public List<ProspectVendorAccount> findByStatus(VendorAccountStatus vendorAccountStatus) {
		TypedQuery<ProspectVendorAccount> typedQuery = em.createNamedQuery("TempVendorAccount.findByStatus",ProspectVendorAccount.class);
		typedQuery.setParameter("vendorAccountStatus", vendorAccountStatus);
		return typedQuery.getResultList();
	}

	@Override
	public ProspectVendorAccount add(ProspectVendorAccount e) throws AccountManagementException {
		try {
			upateEmail(e);
			em.persist(e); 
			return e;
		} catch (Exception ex) {
			ex.printStackTrace();
			DatabaseExceptionsHelper.isUniqueConstraintViolation(ex, e);
			throw new AccountManagementException(ex.getMessage());
		}
	}

	public ProspectVendorAccount findByEmail(String email) {
		try {
			TypedQuery<ProspectVendorAccount> typedQuery = em.createNamedQuery("TempVendorAccount.findByStatus",ProspectVendorAccount.class);
			typedQuery.setParameter("email", email);
			return typedQuery.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}
	public ProspectVendorAccount findByAccountID(Long accountID) {
		try {
			TypedQuery<ProspectVendorAccount> typedQuery = em.createNamedQuery("TempVendorAccount.findByAccountID",ProspectVendorAccount.class);
			typedQuery.setParameter("accountID", accountID);
			return typedQuery.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}

	public ProspectVendorAccount findBySciUserID(String sciUserID) {
		try {
			TypedQuery<ProspectVendorAccount> typedQuery = em.createNamedQuery("TempVendorAccount.findBySciUserID",ProspectVendorAccount.class);
			typedQuery.setParameter("sciUserID", sciUserID);
			return typedQuery.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}

	
	@Override
	public ProspectVendorAccount update(ProspectVendorAccount e) throws AccountManagementException {
		try {
			upateEmail(e);
			ProspectVendorAccount merge = em.merge(e);
			em.flush();
			return merge;
		} catch (Exception ex) {
			DatabaseExceptionsHelper.isUniqueConstraintViolation(ex, e);
			throw new AccountManagementException(ex.getMessage());
		}

	}

	public Result delete(ProspectVendorAccount tempVendorAccount) {
		try {
			ProspectVendorAccount account = em.merge(tempVendorAccount);
			em.remove(account);
			em.flush();
			return new Result(ResultType.SUCCESS, findAll());
		} catch (IllegalArgumentException e) {
			return new Result(ResultType.ERROR, e.getMessage());
		}
	}

	private void upateEmail(ProspectVendorAccount e) {
		if(e.getEmail()!=null){
			String email = StringUtils.lowerCase(e.getEmail());
			e.setEmail(email);
		}
		
	}
}
