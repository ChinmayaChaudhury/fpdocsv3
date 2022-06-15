package com.ntuc.vendorservice.emailingservice.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ntuc.vendorservice.emailingservice.constants.EmailUrls;
import com.ntuc.vendorservice.emailingservice.models.EmailContentType;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationDirection;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;
import com.ntuc.vendorservice.emailingservice.models.EmailType;
import com.ntuc.vendorservice.emailingservice.services.EmailNotificationRequestFactory;
import com.ntuc.vendorservice.emailingservice.services.EmailerService.EmailSource;
import com.ntuc.vendorservice.emailingservice.services.VendorEmailServiceBean;
import com.ntuc.vendorservice.emailingservice.utils.MailSenderUtil;
import com.ntuc.vendorservice.foundationcontext.catalog.constants.AccountFLPUrl;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.RequestDataException;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.foundationcontext.catalog.web.BaseController;
import com.ntuc.vendorservice.foundationcontext.utils.ClassUtils;
import com.ntuc.vendorservice.notificationservice.models.GeneralNotificationRequest;
import com.ntuc.vendorservice.notificationservice.services.AsyncProspectVendorAccountNotificationBean;
import com.ntuc.vendorservice.vendoradminservice.entity.InternalUser;
import com.ntuc.vendorservice.vendoradminservice.entity.ProspectVendorAccount;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Send email when
 * 1. Account is activated
 * 2. Document share requests is released
 * @author I305675
 *
 */
@WebServlet(name="SendMailController",urlPatterns={"/send/act","/send/cwf_act","/send/ds","/mail/test","/mail/attachment","/mail/noattachment"})
@MultipartConfig
public class SendMailController extends BaseController {
   private static final Logger LOGGER = LoggerFactory.getLogger(SendMailController.class);
   @EJB
   protected VendorEmailServiceBean vendorEmailServiceBean;
   @EJB
   protected AsyncProspectVendorAccountNotificationBean asyncMailerEJB;
   private static final long serialVersionUID = 1;
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       handleRequest(request, response);
   }
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       super.doPost(request, response);
   }

   @Override
   protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       final String requestURI = request.getRequestURI();
       final EmailUrls fromValue = EmailUrls.fromValue(requestURI);
       response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
       EmailNotificationRequest emailNotificationRequest=null;
       try{
           switch (fromValue) {
           case CWF_ACTIVATION_EMAIL:
               try {
                   final String postedJsonData = getPostedJsonData(request);
                   final JsonObject documentData = JsonParser.parseString(postedJsonData).getAsJsonObject();
                   final ProspectVendorAccount prospectVendorAccount = new Gson().fromJson(documentData, ProspectVendorAccount.class);
                   asyncMailerEJB.sendNotification(prospectVendorAccount, AccountFLPUrl.getUrl(request.getRequestURL().toString()));
                   response.setStatus(HttpServletResponse.SC_ACCEPTED);
                   response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS, "message sent to " + prospectVendorAccount.getEmail())));
               }   catch (RequestDataException e) {
               e.printStackTrace();
               response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
              }
               break;
           case ACTIVATION_EMAIL:
               emailNotificationRequest = EmailNotificationRequestFactory.getInstance(EmailType.ACCOUNT_ACTIVATION, EmailNotificationDirection.FPADMIN_2_VENDORADMIN);
               InternalUser fairPriceUser = new InternalUser();
               emailNotificationRequest.setRecipientEmail(fairPriceUser.getEmailAddress());
               emailNotificationRequest.setRecipientEmail(fairPriceUser.getDisplayName());
               emailNotificationRequest.setEmailType(EmailType.ACCOUNT_ACTIVATION);
               emailNotificationRequest.setContentType(EmailContentType.PLAIN);
               MailSenderUtil util = new MailSenderUtil(emailNotificationRequest);
               emailNotificationRequest.setMessage(util.getEmailActivationMessage(fairPriceUser,null));
               vendorEmailServiceBean.sendEmailWithoutAttachment(request,emailNotificationRequest, EmailSource.INTERNAL);
               response.setStatus(HttpServletResponse.SC_ACCEPTED);
               response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS,"message sent to "+emailNotificationRequest.getRecipientEmail())));
               break;
           case CWF_REQUEST_EMAIL:
           case CWF_REQUEST_NO_ATTACHMENT_EMAIL:
               try {
                   final String postedJsonData = getPostedJsonData(request);
                   final JsonObject documentData = JsonParser.parseString(postedJsonData).getAsJsonObject();
                   GeneralNotificationRequest generalNotificationRequest=new Gson().fromJson(documentData, GeneralNotificationRequest.class);
                   try{
                        List<String> recipientEmails = generalNotificationRequest.getRecipientEmails();
                        EmailContentType emailContentType = EmailContentType.valueOf(documentData.get("contentType").getAsString().trim());
                        generalNotificationRequest.setContentType(emailContentType);
                        if(recipientEmails.size()>1){
                            vendorEmailServiceBean.sendEmailBCCWithoutAttachment(request,generalNotificationRequest, recipientEmails,EmailSource.CWF);
                        }else{
                            if(recipientEmails.size()==1){
                               generalNotificationRequest.setRecipientEmail(recipientEmails.get(0));
                            }
                            vendorEmailServiceBean.sendEmailWithoutAttachment(request,generalNotificationRequest,EmailSource.CWF);
                        }
                        String recipientEmail = recipientEmails.size()>0?recipientEmails.toString():generalNotificationRequest.getRecipientEmail();
                        response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS,"message sent to "+recipientEmail)));
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                    }catch(MessagingException e ){
                        e.printStackTrace();
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
                    } catch(Exception e ){
                       e.printStackTrace();
                       response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                       response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,"Service error")));
                   }
               } catch (RequestDataException e) {
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
               }
               break;
           case CWF_REQUEST_ATTACHMENT_EMAIL:
               try{
                   GeneralNotificationRequest notificationRequest = ClassUtils.getObjectFromRequestParamater(request, GeneralNotificationRequest.class);
                   if(notificationRequest==null){
                       throw new RequestDataException("Please set all the required parameters");
                   }
                   if(notificationRequest.getMessage()==null){
                       throw new RequestDataException("Message field not set");
                   }
                   final List<String> recipientEmails = notificationRequest.getRecipientEmails();
                   if (recipientEmails.isEmpty()){
                       String recipientEmail = notificationRequest.getRecipientEmail();
                       if(StringUtils.isNotEmpty(recipientEmail)){
                           recipientEmails.add(recipientEmail);
                       }
                   }
                   if (recipientEmails.isEmpty()){
                       throw new RequestDataException("Message field <recipientEmail/recipientEmails> not set");
                   }
                   emailNotificationRequest=notificationRequest;
                   vendorEmailServiceBean.sendEmailWithAttachment(request, notificationRequest, EmailSource.CWF);
                   response.getWriter().print(new Gson().toJson(new Result(ResultType.SUCCESS,notificationRequest)));
               }catch (RequestDataException e) {
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,e.getMessage())));
               }
              
               break;
           default:
               break;
           }

       }  catch (MessagingException exc) {
           final String message = "Could not send email to " + emailNotificationRequest.getRecipientEmail() + " due to a MessagingException. See the logs.";
           LOGGER.error(message, exc);  
           response.getWriter().print(new Gson().toJson(new Result(ResultType.ERROR,message)));
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
       }
   }

}

