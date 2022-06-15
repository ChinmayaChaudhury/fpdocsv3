package com.ntuc.vendorservice.emailingservice.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;

/**
 * Request Bean implementation class MailBean
 */
@Stateless
@LocalBean
public class MessagingBean  {

	public MessagingBean() {
		// TODO Auto-generated constructor stub
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MessagingBean.class);

	// MAIL_FROM will be replaces by the mail address from the Mail destination
	// (named 'Session') in the Cloud scenario
	// and will be displayed as sender in the local server scenario
	public static final String MAIL_FROM = "<documentsharing@fairprice.com.sg>"; 
	public static final String CWF_MAIL_FROM = "<cwfnotification@fairprice.com.sg>"; 
	private Session session;

	/**
	 * This method sends an e-mail to a single user. In this application sending
	 * e-mails is optional.
	 *
	 * @param mailTo
	 *            - the e-mail address of the person who receives the letter
	 * @param subject
	 *            - the e-mail subject
	 * @param mailContent
	 *            - the letter content
	 */
	public void send(EmailNotificationRequest emailNotificationRequest) throws MessagingException {
		Session session = getSession(); 
		if (session == null) {
			return;
		} 
		Transport transport = session.getTransport();
		transport.connect();

		Message msg = createMessage(emailNotificationRequest);

		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();
	}
	public void sendMultiple(EmailNotificationRequest emailNotificationRequest,List<String> recipientEmails) throws MessagingException {
		Session session = getSession(); 
		if (session == null) {
			return;
		} 
		Transport transport = session.getTransport();
		transport.connect();

		Message msg = createBCCMessage(emailNotificationRequest,recipientEmails);

		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();
	}
	public void sendWithAttachment(EmailNotificationRequest emailNotificationRequest)throws MessagingException{
		Session session = getSession(); 
		if (session == null) {
			return;
		} 
		Transport transport = session.getTransport();
		transport.connect(); 
		Message msg = createMultiPartMessage(emailNotificationRequest);
		
		transport.sendMessage(msg, msg.getAllRecipients());
	}
	private Message createBCCMessage(EmailNotificationRequest emailNotificationRequest,List<String> recipientEmails)
			throws MessagingException { 
		InternetAddress addressFrom = new InternetAddress(getFromAddress(emailNotificationRequest));  
		Message message = new MimeMessage(session);
		message.setFrom(addressFrom);
		List<InternetAddress> addressTo=new ArrayList<InternetAddress>();
		for(String recipientEmail:recipientEmails){
			addressTo.add(new InternetAddress(recipientEmail)); 
		}
		message.setRecipients(Message.RecipientType.BCC,  addressTo.toArray(new InternetAddress[recipientEmails.size()]));
		message.setSubject(emailNotificationRequest.getEmailSubject()); 
		message.setContent(emailNotificationRequest.getMessage(),emailNotificationRequest.getContentType().getValue()); 
		return message;
	}
	
	private Message createMessage(EmailNotificationRequest emailNotificationRequest) throws MessagingException {

		InternetAddress addressFrom = new InternetAddress(getFromAddress(emailNotificationRequest)); 
		InternetAddress addressTo = new InternetAddress(emailNotificationRequest.getRecipientEmail());
		Message message = new MimeMessage(session);
		message.setFrom(addressFrom);
		message.setRecipient(Message.RecipientType.TO, addressTo);
		message.setSubject(emailNotificationRequest.getEmailSubject());
		message.setContent(emailNotificationRequest.getMessage(),emailNotificationRequest.getContentType().getValue()); 
		return message;
	} 
	String  getFromAddress(EmailNotificationRequest emailNotificationRequest){
		String fromEmail = emailNotificationRequest.getFromEmail();
		return fromEmail==null?MAIL_FROM:fromEmail;  
	}
	private Message createMultiPartMessage(EmailNotificationRequest emailNotificationRequest)
			throws MessagingException { 
		InternetAddress addressFrom = new InternetAddress(getFromAddress(emailNotificationRequest)); 
		InternetAddress addressTo = new InternetAddress(emailNotificationRequest.getRecipientEmail());
		Message message = new MimeMessage(session);
		message.setFrom(addressFrom);
		message.setRecipient(Message.RecipientType.TO, addressTo);
		message.setSubject(emailNotificationRequest.getEmailSubject());

		MimeBodyPart messageBody = new MimeBodyPart(); 
		messageBody.setContent(emailNotificationRequest.getMessage(),emailNotificationRequest.getContentType().getValue());
		
		
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBody);
        
        // Part two is attachment
        BodyPart messageBodyPart = new MimeBodyPart();
        String fileName = getClass().getResource("/resources/fpuser/FPInternalUser_manual.pdf").getFile();
        EmailNotificationDirection emailNotificationDirection = emailNotificationRequest.getEmailNotificationDirection();
		if(emailNotificationDirection==EmailNotificationDirection.VADMIN_2_VUSER){
        	fileName = getClass().getResource("/resources/vendoruser/Vendor User Guide.pdf").getFile();
        }
        if(emailNotificationDirection==EmailNotificationDirection.FPADMIN_2_FPUSER){
        	fileName = getClass().getResource("/resources/fpuser/FPInternalUser_manual.pdf").getFile();
        }
        if(emailNotificationDirection==EmailNotificationDirection.FPADMIN_2_VENDORADMIN){
        	//fileName = getClass().getResource("/resources/vendoradmin/VendorAdmin_manual.pdf").getFile();
        	fileName = getClass().getResource("/resources/vendoruser/Vendor User Guide.pdf").getFile();
        } 
        if(emailNotificationDirection==EmailNotificationDirection.ARTICLE_CREATION){
        	fileName = getClass().getResource("/resources/interim/User Manual_Vendor New Article Submission.zip").getFile();
        }
        File file=new File(fileName);
		DataSource source = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(file.getName());
        multipart.addBodyPart(messageBodyPart); 
		message.setContent(multipart); 
		return message;
	}

	/**
	 * NEO- Dependency  --> Version in CF
	 *
	 * @return
	 */
	private Session getSession() {
		if (session == null) { 
			try {
				InitialContext ctx = new InitialContext();
				session = (Session) ctx.lookup("java:comp/env/mail/Session");
			} catch (NamingException exc) {
				LOGGER.error("NamingException has occurred while trying to lookup Session!");
			}
		}
		return session;
	} 

}
