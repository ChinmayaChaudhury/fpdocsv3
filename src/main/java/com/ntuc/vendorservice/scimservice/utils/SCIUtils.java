package com.ntuc.vendorservice.scimservice.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ntuc.vendorservice.scimservice.annotations.SCIRestApiParameter;
import com.ntuc.vendorservice.scimservice.entity.CWFApplicationGroup;
import com.ntuc.vendorservice.scimservice.models.*;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SystemAccountStatus;
import com.ntuc.vendorservice.foundationcontext.utils.ClassUtils;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorUserGroup;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import com.ntuc.vendorservice.vendoradminservice.models.SystemUserRole;
import com.ntuc.vendorservice.vendoradminservice.models.VendorCategory;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Field;
import java.util.*;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

public final class SCIUtils {
	/**
	 * Extract the User Registration details for SCI Services
	 * @param userAccount
	 * @return
	 */
	public static List<NameValuePair> getSCIUserRegistrationRequest(FPSCIUserAccount userAccount){
		final List <NameValuePair> attributes= new ArrayList<>();
		ArrayList<Field> declaredColumnFields = ClassUtils.declaredFieldsWithAnnotation(userAccount.getClass().getDeclaredFields(), SCIRestApiParameter.class);
		for(Field field:declaredColumnFields){ 
			try {
				field.setAccessible(true); 
				Object object = field.get(userAccount);
				String fieldvalue= String.valueOf(object==null?"":object);
				SCIRestApiParameter sciRestApiParameter = field.getAnnotation(SCIRestApiParameter.class);
				for(String fieldName: sciRestApiParameter.name()){ 
					if(fieldvalue!=null&& !fieldvalue.isEmpty()){
						attributes.add(new BasicNameValuePair(fieldName, fieldvalue));
					} 
				} 
			} catch (IllegalArgumentException | IllegalAccessException e) { 
				e.printStackTrace();
			}
		}
		return attributes;
	} 
	/**

	/**
	 *
	 * @param userAccount
	 * @param sciAccountId
	 * @param vendorAccount
	 */

	public static void updateVendorAccount(final FPSCIUserAccount userAccount, final String sciAccountId, final VendorAccount vendorAccount) {
		vendorAccount.setSciAccountID(sciAccountId);  
		vendorAccount.setInheritCategoryQuota(true); 
		Date createDateTime = new Date();
		vendorAccount.setCreateDateTime(createDateTime); 
		vendorAccount.setVendorAdminEmail(userAccount.getEmail());
		vendorAccount.setVendorName(userAccount.getCompanyName()); 
		vendorAccount.setVendorCategory(userAccount.getVendorCategory()); 
		vendorAccount.setVendorCode(userAccount.getVendorCode());
		vendorAccount.setWork(userAccount.getWork());
		vendorAccount.setMobile(userAccount.getMobile());
	}
	/**
	 *
	 *
	 * @param userAccount
	 * @param sciAccountId
	 * @param vendorUser
	 */
	public static void assignVendorUserAccount(final FPSCIUserAccount userAccount, final String sciAccountId, final VendorUser vendorUser) {
		vendorUser.setSciAccountID(sciAccountId);
		vendorUser.setCreateDateTime(new Date()); 
		vendorUser.setJobTitle(userAccount.getJobTitle());
		vendorUser.setVendorCode(userAccount.getVendorCode());
		vendorUser.setVendorUserEmail(userAccount.getEmail());
		vendorUser.setFirstName(userAccount.getFirstName()); 
		vendorUser.setLastName(userAccount.getLastName()); 
		vendorUser.setSystemAccountStatus((userAccount.getSystemAccountStatus()!=null)? userAccount.getSystemAccountStatus() : SystemAccountStatus.NEW);
		vendorUser.setUserName(userAccount.getUserID());

		Collection<VendorUserGroup> assignGps = new ArrayList<VendorUserGroup>();
		if(!userAccount.getVendorUserGroupID().isEmpty()){
			VendorUserGroup userGroup = null;
			for(Integer vendorGroupID:userAccount.getVendorUserGroupID()){
					userGroup = new VendorUserGroup();
					userGroup.setVendorGroupID(vendorGroupID);
					assignGps.add(userGroup);
			} 
		}
		vendorUser.setVendorUserGroups(assignGps);
		
		vendorUser.setSystemUserRole(SystemUserRole.VendorUser);
	}
	
	public static FPSCIUserAccount toFPSCIUserAccount (List<SCIMUser> scimUsers) {
		SCIMUser next=getRecentSCIMUserDetails(scimUsers); 
		FPSCIUserAccount account=new FPSCIUserAccount();
		account.setUserID(next.getId());
		account.setCompanyName(next.getCompany());
		account.setFirstName(next.getName().getGivenName());
		account.setLastName(next.getName().getFamilyName());
		Set<SCIMEmail> emails = next.getEmails();
		account.setEmail(emails.toArray(new SCIMEmail[emails.size()])[0].getValue());			
		account.setDisplayName(next.getDisplayName());
		account.setDepartment(next.getDepartment());  
		account.setIndustry(next.getIndustryCrm());
		account.setSalutation(next.getName().getHonorificPrefix());
		Set<SCIMPhoneNumber> contactList = next.getPhoneNumbers();
		for (SCIMPhoneNumber phNo : contactList) {
			if(phNo.getType().equalsIgnoreCase(SCIMPhoneNumber.Type.work.toString())){
				account.setWork(phNo.getValue());
			}
			if(phNo.getType().equalsIgnoreCase(SCIMPhoneNumber.Type.mobile.toString())){
				account.setMobile(phNo.getValue());
			}
		}
		SystemAccountStatus currentStatus = SystemAccountStatus.NEW;
		if(next.getActive()!=null){
			currentStatus = (next.getActive())? SystemAccountStatus.ACTIVE : SystemAccountStatus.INACTIVE;
		}
		account.setSystemAccountStatus(currentStatus);
		return account;
	} 
	public static  SCIMUser getRecentSCIMUserDetails(List<SCIMUser> scimUsers){
		if(scimUsers.isEmpty()){
			return null;
		}
		SCIMUser next=Collections.max(scimUsers, new Comparator<SCIMUser>() { 
			@Override
			public int compare(SCIMUser o1, SCIMUser o2) { 
				return Long.valueOf(o1.getId().substring(1)).compareTo(Long.valueOf(o2.getId().substring(1)));
		} });
		return next;
	}
	public static VendorUser getVendorUser(VendorAccountAdministrator administrator, VendorAccount vendorAccount){
		VendorUser vendorUser=new VendorUser();
		vendorUser.setAccountValidated(administrator.isAccountValidated());
		vendorUser.setSciAccountID(administrator.getSciAccountID());
		vendorUser.setFirstName(administrator.getFirstName());
		vendorUser.setLastName(administrator.getLastName());
		vendorUser.setUserName(administrator.getUserName());
		vendorUser.setSystemUserRole(SystemUserRole.VendorUser);
		vendorUser.setVendorAccount(vendorAccount);
		vendorUser.setVendorCode(vendorAccount.getVendorCode());
		vendorUser.setSystemAccountStatus(SystemAccountStatus.NEW);
		vendorUser.setJobTitle(administrator.getJobTitle());
		vendorUser.setVendorUserEmail(administrator.getVendorUserEmail());
		vendorUser.setCreatedBy(administrator.getCreatedBy());
		vendorUser.setCreateDateTime(new Date()); 
		return vendorUser;
		
	}
	public static FPSCIUserAccount getFPSCIUserAccount(ProspectVendorAccount tempVendorAccount) {
		FPSCIUserAccount account=new FPSCIUserAccount();
		account.setUserID(tempVendorAccount.getLoginName());
		account.setCompanyName(tempVendorAccount.getCompanyName());
		account.setFirstName(tempVendorAccount.getFirstName());
		account.setLastName(tempVendorAccount.getLastName()); 
		account.setEmail(tempVendorAccount.getEmail());		
		account.setSendEmail(false);
		account.setSalutation(tempVendorAccount.getSalutation());
		account.setDisplayName(tempVendorAccount.getDisplayName());
		account.setDepartment(tempVendorAccount.getDepartment());  
		account.setIndustry(tempVendorAccount.getIndustry()); 
		account.setOrganisationWideID(tempVendorAccount.getEmail()); 
		return account;
	}
	
	/**
	 * Map the SCI User Account details
	 * @param iterator
	 * @return {@link FPSCIUserAccount}
	 */
	public static FPSCIUserAccount getFPSCIUserAccount(Iterator<JsonElement> iterator) {
		FPSCIUserAccount account=new FPSCIUserAccount();
		JsonObject next = iterator.next().getAsJsonObject();
		account.setUserID(next.get("uid").getAsString());
		account.setFirstName(next.get("first_name").getAsString());
		account.setLastName(next.get("last_name").getAsString());
		if(next.has("login_name")){
		  account.setLoginName(next.get("login_name").getAsString());
		}
		account.setEmail(next.get("email").getAsString());
		return account;
	}
	public static Set<SCIMGroup> parseSCIMGroups(String responseData)throws HttpResponseException {
		JsonObject parse = JsonParser.parseString(responseData).getAsJsonObject();
		JsonElement jsonElement = parse.get("user_groups");
		if(StringUtils.isEmpty(responseData)||jsonElement==null){
			throw new HttpResponseException(SC_NO_CONTENT, "No Groups found");
		}
		Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
		Set<SCIMGroup> groups=new HashSet<SCIMGroup>();
		while(iterator.hasNext()){
			SCIMGroup group=new SCIMGroup();
			JsonObject next = iterator.next().getAsJsonObject();
			group.setValue(next.get("name").getAsString());
			group.setId(next.get("id").getAsString()); 
			if(next.has("description")){
				group.setDescription(next.get("description").getAsString());
			}
			if(next.has("display_name")){
				group.setDisplay(next.get("display_name").getAsString());
			} 
			groups.add(group);
		} 
		return groups; 
	}
	
	public static List<CWFApplicationGroup> getCWFApplicationGroups(JsonElement jsonElement) {
		Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
		List<CWFApplicationGroup> applicationGroups=new ArrayList<CWFApplicationGroup>();
		while(iterator.hasNext()){
			CWFApplicationGroup applicationGroup=new CWFApplicationGroup();
			JsonObject next = iterator.next().getAsJsonObject();
			applicationGroup.setValue(next.get("value").getAsString());
			applicationGroup.setId(next.get("id").getAsString()); 
			if(next.has("description")){
				applicationGroup.setDescription(next.get("description").getAsString());
			}
			if(next.has("display")){
				applicationGroup.setDisplay(next.get("display").getAsString());
			} 
			applicationGroups.add(applicationGroup);
		}  
		return applicationGroups;
	}
	public static List<SCIMUser> getSCIMUsers(JsonElement jsonElement) {
		Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
		List<SCIMUser> scimUsers=new ArrayList<SCIMUser>();
		while(iterator.hasNext()){ 
			JsonObject next = iterator.next().getAsJsonObject();
			SCIMUser scimUser=new Gson().fromJson(next, SCIMUser.class);
			scimUsers.add(scimUser);
		}  
		return scimUsers;
	}
	public static VendorAccount getDSVendorAccount(final ProspectVendorAccount prospectVendorAccount) {
		VendorAccount vendorAccount=new VendorAccount();
		vendorAccount.setValidateEmailDomain(Boolean.FALSE);
		vendorAccount.setSystemAccountStatus(SystemAccountStatus.NEW);
		vendorAccount.setVendorCategory(VendorCategory.S);
		vendorAccount.setInheritCategoryQuota(Boolean.TRUE);
		vendorAccount.setVendorAdminEmail(prospectVendorAccount.getEmail());
		vendorAccount.setVendorCode(prospectVendorAccount.getVendorCode());
		vendorAccount.setValidateEmailDomain(false);
		vendorAccount.setSciAccountID(prospectVendorAccount.getSciUserID());
		vendorAccount.setVendorName(prospectVendorAccount.getCompanyName());
		vendorAccount.setCreateDateTime(new Date());
		vendorAccount.setWork(prospectVendorAccount.getMobile());
		vendorAccount.setMobile(prospectVendorAccount.getMobile());
		return vendorAccount;
	}
	public static VendorAccountAdministrator getDSVendorAdministrator(ProspectVendorAccount prospectVendorAccount) {
		VendorAccountAdministrator vendorAccountAdministrator =new VendorAccountAdministrator();
		vendorAccountAdministrator.setCreatedBy(prospectVendorAccount.getCreatedBy());
		vendorAccountAdministrator.setSciAccountID(prospectVendorAccount.getSciUserID());
		vendorAccountAdministrator.setUserName(prospectVendorAccount.getLoginName());
		vendorAccountAdministrator.setJobTitle(SystemUserRole.VendorAdministrator.name());
		vendorAccountAdministrator.setSystemUserRole(SystemUserRole.VendorAdministrator);
		vendorAccountAdministrator.setCreateDateTime(new Date());
		vendorAccountAdministrator.setVendorUserEmail(prospectVendorAccount.getEmail());
		vendorAccountAdministrator.setFirstName(prospectVendorAccount.getFirstName());
		vendorAccountAdministrator.setLastName(prospectVendorAccount.getLastName());
		vendorAccountAdministrator.setAllowDocumentShare(false);
		vendorAccountAdministrator.setAccountValidated(false);
		vendorAccountAdministrator.setSystemAccountStatus(SystemAccountStatus.NEW);
		return vendorAccountAdministrator;
	}
}
