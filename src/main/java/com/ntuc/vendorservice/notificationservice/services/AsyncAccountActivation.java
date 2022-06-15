package com.ntuc.vendorservice.notificationservice.services;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import com.ntuc.vendorservice.foundationcontext.catalog.constants.AccountFLPUrl;
import com.ntuc.vendorservice.notificationservice.entity.ActivationNotificationRequest;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailContentType;
import com.ntuc.vendorservice.emailingservice.services.MessagingBean;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;

@Stateless
@LocalBean
public class AsyncAccountActivation {
    private static final Log LOGGER = LogFactory.getLog(AsyncAccountActivation.class);
    @EJB
    protected MessagingBean messagingBean;

    @Asynchronous
    public void dispatchAccountActivationNotifications(ActivationNotificationRequest activationNotificationRequest, SystemAccount systemAccount, String accountFlpLink) {
        final MailSenderUtil senderUtil = new MailSenderUtil(activationNotificationRequest);
        try {
            String message = senderUtil.getEmailActivationMessage(systemAccount,accountFlpLink);
            activationNotificationRequest.setMessage(message);
            activationNotificationRequest.setFromEmail(MessagingBean.CWF_MAIL_FROM);
            if(activationNotificationRequest.getEmailNotificationDirection()==EmailNotificationDirection.FPADMIN_2_VENDORADMIN){
                if(systemAccount instanceof ProspectVendorAccount){
                    ProspectVendorAccount prospectVendorAccount = (ProspectVendorAccount) systemAccount;
                    if(prospectVendorAccount!=null){
                        activationNotificationRequest.setEmailSubject(MailSenderUtil.CWF_NEW_VENDOR_LOGIN_SUBJECT);
                        message = senderUtil.getCWFVendorAccountNotificationMessage(prospectVendorAccount, AccountFLPUrl.getUrl(activationNotificationRequest.getTargetUrl()));
                        activationNotificationRequest.setMessage(message);
                        activationNotificationRequest.setContentType(EmailContentType.HTML);
                    }
                }
            }
            messagingBean.sendWithAttachment(activationNotificationRequest);

        } catch (MessagingException exc) {
            LOGGER.error(exc.getMessage(), exc);
            LOGGER.info(activationNotificationRequest.toString());
        } catch (Exception exc) {
            LOGGER.error(exc.getMessage(), exc);
            LOGGER.info(activationNotificationRequest.toString());
        }

    }


}
