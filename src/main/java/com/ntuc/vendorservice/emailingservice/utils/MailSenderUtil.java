package com.ntuc.vendorservice.emailingservice.utils;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;
import com.ntuc.vendorservice.emailingservice.models.EmailType;
import com.ntuc.vendorservice.foundationcontext.catalog.constants.AccountFLPUrl;
import com.ntuc.vendorservice.foundationcontext.utils.IOUtils;
import com.ntuc.vendorservice.vendoraccountservice.entity.VendorUser;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;
import com.ntuc.vendorservice.vendoradminservice.entity.VendorAccountAdministrator;
import org.apache.commons.lang.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This utility class is used for processing the templates for e-mails.
 * */
public class MailSenderUtil {

	public static final String CWF_ACCOUNT_ACTIVATION_SUBJECT = "Activation of Account for CWF";
	public static final String CWF_NEW_VENDOR_LOGIN_SUBJECT = "New Vendor Login Account";
	private EmailNotificationRequest emailNotificationRequest;
	
	public MailSenderUtil(EmailNotificationRequest emailNotificationRequest) { 
		this.emailNotificationRequest = emailNotificationRequest; 
	}
	/**
	 * This method sends e-mail to the user whose account have been activated
	 * @param userAccount
	 * @param accountFlpLink
	 * @return
	 * @throws IOException
	 * @throws MessagingException
	 */
	public String getEmailActivationMessage(final SystemAccount  userAccount,String accountFlpLink) throws IOException, MessagingException {
		String template = commonTemplateProcessing(getTemplate(EmailType.ACCOUNT_ACTIVATION));
		String loginName="";
		String sciUserID="";
		String email="";
		if(userAccount instanceof InternalUser){
			InternalUser user=(InternalUser)userAccount;
			loginName=user.getUserName();
			sciUserID=user.getSciAccountID();
			email=user.getFairPriceEmail();
		}
		if(userAccount instanceof VendorUser){
			VendorUser user=(VendorUser)userAccount;
			loginName=user.getUserName();
			sciUserID=user.getSciAccountID();
			email=user.getVendorUserEmail();
		}
		if(userAccount instanceof VendorAccountAdministrator){
			template=commonTemplateProcessing(getTemplate(EmailType.VADMIN_ACCOUNT_ACTIVATION)); 
			VendorAccountAdministrator user=(VendorAccountAdministrator)userAccount;
			loginName=user.getUserName();
			sciUserID=user.getSciAccountID();
			email=user.getVendorUserEmail();
		}
		
		//Notification sent when Prospect Vendor is changed to Vendor Administrator
//		if(userAccount instanceof ProspectVendorAccount){
//			template=commonTemplateProcessing(getTemplate(EmailType.CWF_VADMIN_ACTIVATION)); 
//			ProspectVendorAccount user=(ProspectVendorAccount)userAccount;
//			loginName=user.getLoginName();
//			previousLoginName = user.getNewSciUserID();
//			sciUserID=user.getSciUserID();
//			email=user.getEmail();
//		}
		if(loginName.equals(sciUserID)){
			loginName=email;
		}
		
		String message = template.replace("${link}", emailNotificationRequest.getTargetUrl()); 
		
		message = message.replace("${loginName}", loginName); 
		message = message.replace("${sciUserID}", sciUserID);
		if(accountFlpLink!=null){
			message = message.replace("${lpdLink}", accountFlpLink); 
		}
		return message;
		
	}

	
	public String getQuotaEmail(String displayName,String percentage, String vendorCode) throws IOException { 
		String message = commonTemplateProcessing(getTemplate(EmailType.FREE_QUOTA));  
		message = message.replace("${displayName}", displayName); 
		message = message.replace("${percentage}", percentage); 
		message=message.replace("${vendorAdministratorConsoleUrl}", emailNotificationRequest.getTargetUrl()); 
		message = message.replace("${vendorCode}", vendorCode); 
		return message;
	}


	public String getCWFAccountNotificationMessage(ProspectVendorAccount  prospectVendorAccount,String accountFlpLink) throws IOException { 
		String message = getTemplate(EmailType.CWF_ACCOUNT_ACTIVATION);
		message = message.replace("${displayName}", prospectVendorAccount.getDisplayName()); 
		message = message.replace("${sciUserID}", prospectVendorAccount.getSciUserID()); 
		message = message.replace("${loginName}", prospectVendorAccount.getLoginName());  
		message = message.replace("${link}", prospectVendorAccount.getActivationLink());
		message= message.replace("${companyName}", prospectVendorAccount.getCompanyName());
		message=message.replace("${lpdLink}", accountFlpLink);
		return message;
	}
	public String getCWFVendorAccountNotificationMessage(ProspectVendorAccount  prospectVendorAccount,String accountFlpLink) throws IOException { 
		String message = getTemplate(EmailType.CWF_VADMIN_ACTIVATION);
		System.err.println("Inside the Mail Sender Util and attributes are " + prospectVendorAccount.getNewSciUserID() + " and the login Name is " + prospectVendorAccount.getLoginName());
		message = message.replace("${displayName}", prospectVendorAccount.getDisplayName()); 
		System.err.println("Old SCI User id is  " + prospectVendorAccount.getNewSciUserID());
		message = message.replace("${previousLoginName}", (prospectVendorAccount.getNewSciUserID()!=null) ? prospectVendorAccount.getNewSciUserID() : ""); 
		System.err.println("SCI User update is  " + prospectVendorAccount.getNewSciUserID());
		message = message.replace("${loginName}", prospectVendorAccount.getLoginName()); 
		message= message.replace("${companyName}", prospectVendorAccount.getCompanyName());
		message=message.replace("${link}", accountFlpLink);
		return message;
	}
	
	public String getDropOutBoxDocumentParticipationNotificationMessage(String displayName,String folderName) throws IOException { 
		String message = getTemplate(EmailType.DROP_PARTICIPANT_DOCUMENT_NOTIFICATION);
		message = message.replace("${displayName}", displayName); 
		message = message.replace("${folderName}", folderName);  
		return message;
	}
	
	private String getTemplate(final EmailType emailType) throws IOException {
		InputStream is = null;
		String template = null; 
		switch(emailType) {
		case DOCUMENT_SHARE: 
			is = getClass().getResourceAsStream("/resources/document_share_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break; 
		case DOCUMENT_SHARE_PASSWORD:
			is = getClass().getResourceAsStream("/resources/document_share_passcode_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case ACCOUNT_ACTIVATION:
			is = getClass().getResourceAsStream("/resources/account_activation_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case VADMIN_ACCOUNT_ACTIVATION:
			is = getClass().getResourceAsStream("/resources/vaccount_activation_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case CWF_VADMIN_ACTIVATION:
			is = getClass().getResourceAsStream("/resources/cwf_vaccount_activation_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case FREE_QUOTA:
			is = getClass().getResourceAsStream("/resources/document_share_quota_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case NEWLY_UPLOADED:
			is = getClass().getResourceAsStream("/resources/document_share_uploaded_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case ADDED_PARTICIPANT_DOCUMENT_NOTIFICATION:
			is = getClass().getResourceAsStream("/resources/add_participant_notification_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case DROP_PARTICIPANT_DOCUMENT_NOTIFICATION:
			is = getClass().getResourceAsStream("/resources/drop_participant_notification_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case CWF_ACCOUNT_ACTIVATION:
			is = getClass().getResourceAsStream("/resources/cwf_account_activation_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		case OTHERS:
		default:
			template="";
			break; 
		}
		
		if(is != null){
			is.close();
		}
		
		return template;
	}
	
	/**
	 * This method proceeds some replacements in the template  
	 * 
	 * @param template - the template over which the replacing operations are going to be held
	 * @return the template after the replacements 
	 * */
	private String commonTemplateProcessing(String template) { 
		template = template.replace("${displayname}", emailNotificationRequest.getRecipientName());  
		return template;
	}
	
	public static String getAccountTargetUrl(String baseUrl) { 
		for(AccountFLPUrl accountFLPUrl:AccountFLPUrl.values()){
			if(StringUtils.contains(baseUrl, accountFLPUrl.getAccount())){
				return accountFLPUrl.getUrl();
			}
		}
		return AccountFLPUrl.MAIN.getUrl();
	}
	@Deprecated
	public static String getDocumentsTargetUrl(EmailNotificationDirection notificationType,String baseUrl) { 
		String accessEndPoint=""; 
		switch (notificationType) {
		case FPADMIN_2_FPUSER:
			accessEndPoint="/fpuser";
			break; 
		case FPADMIN_2_VENDORADMIN: 
			accessEndPoint="/vadmin";
			break; 
		case VADMIN_2_VUSER:
			accessEndPoint="/vuser";
			break;  
		default:
			break;
		}
		return new StringBuilder().append(baseUrl).append(accessEndPoint).toString();
	}
	
} 
