package com.ntuc.vendorservice.vendoraccountservice.services;

import javax.servlet.http.HttpServletRequest;

import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountExistsException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountManagementException;
import com.ntuc.vendorservice.vendoradminservice.exceptions.AccountNotExistsException;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;

public interface ProspectVendorAccountService {

	/**
	 *
	 * @param prospectVendorAccount
	 * @param request
	 * @return
	 * @throws AccountExistsException
	 * @throws AccountManagementException
	 */
	Result createTempVendorAccount(ProspectVendorAccount prospectVendorAccount, HttpServletRequest request) throws AccountExistsException, AccountManagementException;

	/**
	 *
	 * @param prospectVendorAccount
	 * @param request
	 * @return
	 * @throws AccountNotExistsException
	 * @throws AccountManagementException
	 */
	Result updateTempVendorAccount(ProspectVendorAccount prospectVendorAccount, HttpServletRequest request) throws AccountNotExistsException, AccountManagementException;

	/**
	 *
	 * @param fpSciAccount
	 * @param request
	 * @param baseUrl
	 * @return
	 * @throws AccountManagementException
	 * @throws AccountNotExistsException
	 * @throws AccountExistsException
	 */
	Result activateTempVendorAccount(FPSCIUserAccount fpSciAccount, HttpServletRequest request, String baseUrl) throws AccountManagementException, AccountNotExistsException, AccountExistsException;
	/**
	 *
	 * @param sciUserID
	 * @param request
	 * @return
	 * @throws AccountNotExistsException
	 * @throws AccountManagementException
	 */
	Result deactivateTempVendorAccount(String sciUserID, HttpServletRequest request) throws  AccountNotExistsException, AccountManagementException;
	
	 

}
