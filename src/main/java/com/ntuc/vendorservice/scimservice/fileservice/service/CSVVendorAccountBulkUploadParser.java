package com.ntuc.vendorservice.scimservice.fileservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.ntuc.vendorservice.foundationcontext.utils.ValidationUtils;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileContentException;
import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileFormatException;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.models.VendorCategory;

/**
 * Handle CSV file Uploads
 * 
 * @author I305675
 *
 */
public class CSVVendorAccountBulkUploadParser implements BulkUploadParser {
	 
	@Override
	public List<FPSCIUserAccount> readAccountsFromFile(InputStream inputStream)
			throws IOException, BulkFileContentException, BulkFileFormatException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		List<FPSCIUserAccount> userAccounts = new ArrayList<FPSCIUserAccount>();
		String line;
		List<String> lineArray = new ArrayList<String>();
		while ((line = bufferedReader.readLine()) != null) { 
			lineArray.add(line);
		}
		;
		for (int index=0; index<lineArray.size();index++) { 
			String row=lineArray.get(index);
			if(index==0){
				continue;
			}
			String[] split = row.trim().split("(\",\")");
			if ((split.length != 13)) { 
				throw new BulkFileFormatException(BulkFileFormatException.WRONG_FILE_FORMAT_MESSAGE);
			} 
			FPSCIUserAccount userAccount = new FPSCIUserAccount();
			userAccount.setSendEmail(Boolean.FALSE);
			for (int count = 0; count < split.length; count++) {
				String s = split[count].trim();
				s = s.replace("\"", "");
				switch (count) {
				case 0:
					userAccount.setUserID(s);
					userAccount.setOrganisationWideID(s);
					break;
				case 1:
					userAccount.setFirstName(s);
					break;
				case 2:
					userAccount.setLastName(s);
					break;
				case 3: 
					if(ValidationUtils.validate(s)){
						userAccount.setEmail(s);	
					}else{
						throw new BulkFileContentException(
								String.format(BulkFileContentException.EMAIL_MESSAGE,s));
					}
					break;
				case 4:
					userAccount.setDisplayName(s);
					break;
				case 5:
					userAccount.setVendorCode(s);
					break;
				case 6:
					userAccount.setCompanyName(s);
					break;
				case 7:
					userAccount.setTitle(s);
					break;
				case 8:
					userAccount.setSalutation(s);
					break;
				 
				case 9:
					userAccount.setJobTitle(s);
					break;
				case 10:
					try {
						userAccount.setVendorCategory(VendorCategory.valueOf(s));
					} catch (IllegalArgumentException e) {
						throw new BulkFileContentException(
								BulkFileContentException.VENDOR_CATEGORY_MESSAGE);
					}
					break;
				case 11 :
					userAccount.setMobile(s);
					break;
				case 12 :
					userAccount.setWork(s);
					break;
			}
			}
			userAccount.setSystemAccountStatus(SystemAccountStatus.NEW);
			userAccount.setRole(getSystemUserRole().name());
			if(userAccounts.contains(userAccount)){
				throw  new BulkFileContentException(
						String.format(BulkFileContentException.DUPLICATE_VENDORCODE_CONTENT_MESSAGE,userAccount.getVendorCode()));
			}
			userAccounts.add(userAccount); 
		}

		return userAccounts;
	}

	public SystemUserRole getSystemUserRole() {
		return SystemUserRole.VendorAdministrator;
	}

}
