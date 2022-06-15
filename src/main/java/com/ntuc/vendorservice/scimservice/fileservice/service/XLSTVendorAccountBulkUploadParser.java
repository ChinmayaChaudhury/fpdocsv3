package com.ntuc.vendorservice.scimservice.fileservice.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ntuc.vendorservice.foundationcontext.utils.ValidationUtils;
import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileContentException;
import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileFormatException;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.models.VendorCategory;

/**
 * 
 * @author I305675
 *
 */
public class XLSTVendorAccountBulkUploadParser implements BulkUploadParser {
	
	 

	@Override
	public List<FPSCIUserAccount> readAccountsFromFile(InputStream inputStream) throws IOException, BulkFileContentException, BulkFileFormatException {
		List<FPSCIUserAccount> userAccounts = new ArrayList<FPSCIUserAccount>();
		@SuppressWarnings("resource")
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0);
		for (Row row : sheet) { 
			if (row.getLastCellNum() != 13){ 
				throw new BulkFileFormatException(BulkFileFormatException.WRONG_FILE_FORMAT_MESSAGE);
			}
			if (row.getRowNum() == 0) {
				continue;
			}
			FPSCIUserAccount userAccount= new FPSCIUserAccount();
			userAccount.setSendEmail(Boolean.FALSE);
			int index=0; 
			for (Cell cell : row) {
			   CellReference cellReference=new CellReference(cell.getRowIndex(), cell.getColumnIndex());
			   String cellRef="";
			   for(String name:cellReference.getCellRefParts()){
				   cellRef=cellRef+(name==null?"":name);   
			   } 
				String s="";
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					s=cell.getRichStringCellValue().getString();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if(index==5){
					   NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US); 
					   DecimalFormat df = (DecimalFormat)numberFormat;
					   df.applyPattern("####");
					   s = df.format(cell.getNumericCellValue()); 
					}else{
					  throw new BulkFileContentException(String.format(BulkFileContentException.CELL_FORMAT_MESSAGE,cellRef));
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
				case Cell.CELL_TYPE_FORMULA: 
				default:
					throw new BulkFileContentException(String.format(BulkFileContentException.CELL_FORMAT_MESSAGE,cellRef));
				}
				switch (index) {
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
				index++;
			}
			userAccount.setSystemAccountStatus(SystemAccountStatus.NEW);
			userAccount.setRole(getSystemUserRole().name());
			if(userAccounts.contains(userAccount)){
				throw  new BulkFileContentException(
						String.format(BulkFileContentException.DUPLICATE_VENDORCODE_CONTENT_MESSAGE,userAccount.getVendorCode()));
			}
			userAccounts.add(userAccount);
		}
		inputStream.close();
		return userAccounts;
	}

	public SystemUserRole getSystemUserRole() {
		return   SystemUserRole.VendorAdministrator;
	}

}
