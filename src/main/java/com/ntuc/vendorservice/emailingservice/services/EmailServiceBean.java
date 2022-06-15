package com.ntuc.vendorservice.emailingservice.services;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;

import com.ntuc.vendorservice.foundationcontext.adapter.BTPConnectivityServiceAdapter;
import com.ntuc.vendorservice.foundationcontext.adapter.BTPDestinationServiceAdapter;
import com.ntuc.vendorservice.foundationcontext.adapter.ConnectivitySocket5ProxyAdapter;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.RecipientType;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperties;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType;
import com.sap.xsa.security.container.XSUserInfoException;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.MailConnectException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import javax.mail.Authenticator;

import static com.ntuc.vendorservice.emailingservice.constants.DestinationConstants.*;

@Stateless
@LocalBean
public class EmailServiceBean implements EmailerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceBean.class);
	private BTPDestinationServiceAdapter btpDestinationServiceAdapter;
	@PostConstruct
	protected void initializeDestinationProxy(){
		this.btpDestinationServiceAdapter =new BTPDestinationServiceAdapter();
	}
	@Override
	public void sendEmailWithoutAttachment(HttpServletRequest request, final EmailNotificationRequest emailNotificationRequest, final EmailSource emailSource) throws MessagingException {
		try {
			final Properties properties = fnProcessData.apply(request);
			final Session session = getSession(properties);
			final Message message = createMessage(request,emailNotificationRequest, emailSource);
			if(isInternetConnection(properties)){
				sendEmailOverInternetConnection(session, message);
			}else {
				sendEmailViaSocketBasedConnection(properties, session, message);
			}
		} catch (XSUserInfoException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new MessagingException(e.getMessage());
		}catch (MailConnectException e){
			LOGGER.debug(e.getMessage(), e);
			throw new MessagingException("Connection to smtp server timed out");
		}
	}

	@Override
	public void sendEmailBCCWithoutAttachment(HttpServletRequest request,final EmailNotificationRequest emailNotificationRequest, final List<String> recipientEmails, EmailSource emailSource) throws MessagingException {
		try {
			final Properties properties = fnProcessData.apply(request);
			final Session  session = getSession(properties);
			final Message message = createBCCMessage(request, emailNotificationRequest, recipientEmails, emailSource);
			if(isInternetConnection(properties)){
				sendEmailOverInternetConnection(session, message);
			}else {
				sendEmailViaSocketBasedConnection(properties, session, message);
			}
		} catch (MailConnectException e){
			LOGGER.debug(e.getMessage(), e);
			throw new MessagingException("Connection to smtp server timed out");
		} catch (XSUserInfoException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new MessagingException(e.getMessage());
		}
	}
	/**
	 *
	 * @param emailNotificationRequest
	 * @param emailSource
	 * @return
	 * @throws MessagingException
	 */
	protected Message createMessage(HttpServletRequest request, EmailNotificationRequest emailNotificationRequest, EmailSource emailSource) throws MessagingException {
		try {
			final String senderEmail = getEmailSource(emailSource, emailNotificationRequest);
			final InternetAddress senderAddress = new InternetAddress(senderEmail);
			final InternetAddress recipientAddress = new InternetAddress(emailNotificationRequest.getRecipientEmail());
			final Properties properties = fnProcessData.apply(request);
			final Session session = getSession(properties);
			final Message message = new MimeMessage(session);
			message.setFrom(senderAddress);
			Message.RecipientType recipientType = getRecipientType(emailNotificationRequest);
			message.setRecipient(recipientType, recipientAddress);
			message.setSubject(emailNotificationRequest.getEmailSubject());
			message.setContent(emailNotificationRequest.getMessage(), emailNotificationRequest.getContentType().getValue());
			return message;
		} catch (XSUserInfoException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new MessagingException(e.getMessage());
		}

	}

	/**
	 *
	 * @param emailNotificationRequest
	 * @param recipientEmails
	 * @param emailSource
	 * @return
	 * @throws MessagingException
	 */
	protected Message createBCCMessage(HttpServletRequest request,EmailNotificationRequest emailNotificationRequest, List<String> recipientEmails, EmailSource emailSource) throws MessagingException {
		try {
			String senderEmail = getEmailSource(emailSource, emailNotificationRequest);
			InternetAddress senderAddress = new InternetAddress(senderEmail);
			final Properties properties = fnProcessData.apply(request);
			final Session session = getSession(properties);
			final Message message = new MimeMessage(session);
			message.setFrom(senderAddress);
			final List<InternetAddress> recipientAddresses = new ArrayList<>();
			for (String recipientAddress : recipientEmails) {
				recipientAddresses.add(new InternetAddress(recipientAddress));
			}
			Message.RecipientType recipientType = getRecipientType(emailNotificationRequest);
			message.setRecipients(recipientType, recipientAddresses.toArray(new InternetAddress[recipientEmails.size()]));
			message.setSubject(emailNotificationRequest.getEmailSubject());
			message.setContent(emailNotificationRequest.getMessage(), emailNotificationRequest.getContentType().getValue());
			return message;
		} catch (XSUserInfoException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new MessagingException(e.getMessage());
		}
	}

	/**
	 *
	 * @param emailNotificationRequest
	 * @return
	 */
	protected Message.RecipientType getRecipientType(final EmailNotificationRequest emailNotificationRequest) {
		final RecipientType recipientType = emailNotificationRequest.getRecipientType() == null ? RecipientType.TO : emailNotificationRequest.getRecipientType();
		switch (recipientType) {
			case BCC:
				return Message.RecipientType.BCC;
			case CC:
				return Message.RecipientType.CC;
			case TO:
			case UNKNOWN:
			default:
				return Message.RecipientType.TO;
		}
	}

	/**
	 *
	 * @param emailSource
	 * @param emailNotificationRequest
	 * @return
	 */
	private String getEmailSource(final EmailSource emailSource, EmailNotificationRequest emailNotificationRequest) {
		if(StringUtils.isNotEmpty(emailNotificationRequest.getFromEmail())){
			return emailNotificationRequest.getFromEmail();
		}
		switch (emailSource) {
			case CWF:
				return CWF_MAIL_FROM;
			case INTERNAL:
				return MAIL_FROM;
			case OTHERS:
			default:
				return OTHER_MAIL_FROM;
		}
	}

	/**
	 * Can obtain destinations
	 * @param request
	 * @return
	 */
	final Function<HttpServletRequest, Properties> fnProcessData = (request) -> {
		final DestinationProperties mailDestinationProperties = btpDestinationServiceAdapter.getMailDestinationProperties(request);
		final String cloudConnectorLocation = request.getSession().getServletContext().getInitParameter(MAIL_DESTINATION_LOCATION);
		final Properties properties = new Properties();
		mailDestinationProperties.getPropertyNames().forEach(s -> {
			try {
				String value = mailDestinationProperties.get(s, String.class).get();
				properties.put(s, value);
			}catch (Exception e){
			}
		});
		properties.put("MailDestinationLocation", cloudConnectorLocation);
		return properties;
	};
	/**
	 *
	 * @param emailConnectionSession
	 * @return
	 * @throws MessagingException
	 */
	protected void sendEmailOverInternetConnection(final Session emailConnectionSession, final Message message)throws  MessagingException{
		try {
			Transport transport = emailConnectionSession.getTransport();
			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException e){
			LOGGER.debug(e.getMessage(), e);
			final String errorMessage = e.getMessage().contains("SMTP host")?"SMTP Sever Connection issues":"Exception encountered";
			throw new MessagingException(errorMessage);
		}
	}
	/**
	 *
	 * @param properties
	 * @param emailConnectionSession
	 * @return
	 * @throws MessagingException
	 */
	protected void sendEmailViaSocketBasedConnection(final Properties properties, final Session emailConnectionSession, final Message message) throws MessagingException {
		try {
			final String smtpDestinationHost = properties.getProperty("mail.smtp.host");
			final int smtpDestinationPort =  Integer.parseInt(properties.getProperty("mail.smtp.port"));
			final String connectivityServiceAccessToken = BTPConnectivityServiceAdapter.getConnectivityServiceAccessToken((onPremiseProxyHost, onPremiseProxyPort, onPremiseSocks5ProxyPort, onPremiseProxyHttpPort) -> {
				LOGGER.info("connectivity service token acquired {} , port {}", onPremiseProxyHost, onPremiseSocks5ProxyPort);
			});
			final String sccLocationID = !isInternetConnection(properties)? properties.getProperty(MAIL_DESTINATION_LOCATION):null;
			final Socket connectivitySocket5ProxyAdapter = new ConnectivitySocket5ProxyAdapter(connectivityServiceAccessToken, sccLocationID);
			final SMTPTransport transport = (SMTPTransport) emailConnectionSession.getTransport("smtp");
			connectivitySocket5ProxyAdapter.connect(new InetSocketAddress(smtpDestinationHost, smtpDestinationPort));
			transport.connect(connectivitySocket5ProxyAdapter);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (IOException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new MessagingException(e.getMessage());
		} catch (MessagingException e){
			LOGGER.debug(e.getMessage(), e);
			final String errorMessage = e.getMessage().contains("SMTP host")?"SMTP Sever Connection issues":"Exception encountered";
			throw new MessagingException(errorMessage);
		}

	}

	/**
	 *
	 * @param properties
	 * @return
	 */
	protected boolean isInternetConnection(final Properties properties) {
		final String proxyType = properties.getProperty(DESTINATION_PROXY_TYPE, ProxyType.INTERNET.getIdentifier());
		final boolean isInternetConnection = proxyType.equalsIgnoreCase(ProxyType.INTERNET.getIdentifier());
		return isInternetConnection;
	}
	/**
	 *
	 * @param properties
	 * @return
	 */
	protected Session getSession(final Properties properties) {
		//
		final Properties smtpProperties = new Properties(properties);
		smtpProperties.remove(MAIL_DESTINATION_LOCATION);
		smtpProperties.remove("Type");
		smtpProperties.remove("Name");
		smtpProperties.remove("ProxyType");
		//
		if(isInternetConnection(properties)) {
			smtpProperties.remove("mail.smtp.host");
			smtpProperties.remove("mail.smtp.port");
		}
		Session emailConnectionSession = Session.getInstance(smtpProperties);
		if (Boolean.valueOf(smtpProperties.getProperty(DESTINATION_MAIL_SMTP_AUTH))) {
			emailConnectionSession = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpProperties.getProperty(DESTINATION_MAIL_USER), smtpProperties.getProperty(DESTINATION_MAIL_PASSWORD));
				}
			});
		}
		return emailConnectionSession;
	}


}
