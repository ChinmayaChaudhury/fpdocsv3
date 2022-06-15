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

/**
 * Handle CSV file Uploads
 * 
 * @author I305675
 *
 */
public class CSVInternalUserBulkUploadParser implements BulkUploadParser {
	 
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
		for (int count=0;count<lineArray.size();count++) { 
			String row=lineArray.get(count);
			if(count==0){
				continue;
			}
			String[] split = row.trim().split("(\",\")"); 
			if (split.length != 8) { 
				throw new BulkFileFormatException(BulkFileFormatException.WRONG_FILE_FORMAT_MESSAGE);
			}
			FPSCIUserAccount userAccount = new FPSCIUserAccount();
			userAccount.setSendEmail(Boolean.FALSE);
			for (int index = 0; index < split.length; index++) {
				String s = split[index].trim();
				s = s.replace("\"", "");
				switch (index) { 
				case 0:
					userAccount.setUserID(s);
					userAccount.setOrganisationWideID(s);
					break;
				case 1:
					userAccount.setSalutation(s);
					break;
				case 2:
					userAccount.setFirstName(s);
					break;
				case 3:
					userAccount.setLastName(s);
					break;
				case 4:
					userAccount.setDisplayName(s);
					break;
				case 5:
					userAccount.setDepartment(s);
					break;
				case 6:
					userAccount.setJobTitle(s);
					break; 
				case 7:
					if (ValidationUtils.validate(s)) {
						userAccount.setEmail(s);
					} else {
						throw new BulkFileContentException(String.format(BulkFileContentException.EMAIL_MESSAGE, s));
					}
					break;  

				}
			}
			userAccount.setSystemAccountStatus(SystemAccountStatus.NEW);
			userAccount.setRole(getSystemUserRole().name());
			if(userAccounts.contains(userAccount)){
				throw  new BulkFileContentException(
						String.format(BulkFileContentException.DUPLICATE_EMAIL_CONTENT_MESSAGE,userAccount.getEmail()));
			}
			userAccounts.add(userAccount);
		}

		return userAccounts;
	}

	public SystemUserRole getSystemUserRole() {
		return SystemUserRole.FairPriceInternalUser;
	}

}
