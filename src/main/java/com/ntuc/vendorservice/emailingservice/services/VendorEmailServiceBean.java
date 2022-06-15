package com.ntuc.vendorservice.emailingservice.services;


import com.ntuc.vendorservice.documentservice.exceptions.DocumentNotFoundException;
import com.ntuc.vendorservice.documentservice.models.BPMDocument;
import com.ntuc.vendorservice.documentservice.service.VendorBoardingDocumentService;
import com.ntuc.vendorservice.emailingservice.models.EmailNotificationRequest;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.ResultType;
import com.ntuc.vendorservice.foundationcontext.catalog.enums.SourceType;
import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import com.ntuc.vendorservice.notificationservice.models.GeneralNotificationRequest;
import com.ntuc.vendorservice.foundationcontext.utils.FileUtils;
import com.ntuc.vendorservice.foundationcontext.utils.IOUtils;
import com.sap.xsa.security.container.XSUserInfoException;
import com.sun.mail.util.MailConnectException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@Stateless
@LocalBean
public class VendorEmailServiceBean extends EmailServiceBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorEmailServiceBean.class);
    @EJB
    protected VendorBoardingDocumentService vendorBoardingDocumentService;

    public void sendEmailWithAttachment(final HttpServletRequest request, final EmailNotificationRequest emailNotificationRequest, final EmailSource emailSource) throws MessagingException {
        try {
            final GeneralNotificationRequest generalNotificationRequest = (GeneralNotificationRequest) emailNotificationRequest;

            // Set Message for Appointment Confirmation
            final String sourceType = generalNotificationRequest.getSourceType();
            if (sourceType != null && sourceType.equalsIgnoreCase("APPNTCONF")) {
                final InputStream appointmentTemplateInputStream = getClass().getResourceAsStream("/resources/appointment_confirmation_template.txt");
                final String appointmentTemplate = new String(IOUtils.toByteArray(appointmentTemplateInputStream), "UTF-8");
                final String updateFormPlaceholder = appointmentTemplate.replace("${formNo}", generalNotificationRequest.getFormNo());
                final String updateAppointmentDatePlaceholder = updateFormPlaceholder.replace("${appntDate}", generalNotificationRequest.getAppntDate());
                emailNotificationRequest.setMessage(updateAppointmentDatePlaceholder);
            }
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(emailNotificationRequest.getMessage(), emailNotificationRequest.getContentType().getValue());
            List<String> recipientEmails = generalNotificationRequest.getRecipientEmails();
            Message emailMessage;
            if (recipientEmails != null && recipientEmails.size() > 1) {
                emailMessage = createBCCMessage(request,emailNotificationRequest, recipientEmails, emailSource);
            } else {
                emailMessage = createMessage(request,emailNotificationRequest, emailSource);
            }
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // If Source Type is True, Attach Vendor User Guide/CWF documents
            if (sourceType != null) {
                attachedVendorDocuments(sourceType, multipart);
            } else {
                final Collection<javax.servlet.http.Part> collection = request.getParts();
                for (Part filePart : collection) {
                    String contentType = filePart.getContentType();
                    if (StringUtils.isNotEmpty(contentType) || contentType != null) {
                        messageBodyPart = new MimeBodyPart();
                        String fileName = FileUtils.getFileName(filePart);
                        InputStream inputStream = filePart.getInputStream();
                        String suffix = fileName.substring(fileName.lastIndexOf("."));
                        File file = File.createTempFile(fileName, suffix);
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        IOUtils.writeToOutputStream(inputStream, fileOutputStream);
                        DataSource dataSource = new FileDataSource(file);
                        messageBodyPart.setDataHandler(new DataHandler(dataSource));
                        messageBodyPart.setFileName(fileName);
                        multipart.addBodyPart(messageBodyPart);
                    }
                }
            }
            emailMessage.setContent(multipart);
            final Properties properties = fnProcessData.apply(request);
            final Session  session = getSession(properties);
            if(isInternetConnection(properties)){
                sendEmailOverInternetConnection(session, emailMessage);
            }else {
                sendEmailViaSocketBasedConnection(properties, session, emailMessage);
            }
        } catch (IOException | ServletException e) {
            throw new MessagingException("Message Exception in Send Email attachment : " + e.getMessage());
        }catch (XSUserInfoException e) {
            LOGGER.debug(e.getMessage(), e);
            throw new MessagingException(e.getMessage());
        }catch (MailConnectException e){
            LOGGER.debug(e.getMessage(), e);
            throw new MessagingException("Connection to smtp server timed out");
        }
    }
    /**
     *
     * @param sourceType
     * @param multipart
     * @throws MessagingException
     * @throws IOException
     * @throws DocumentNotFoundException
     */
    private void attachedVendorDocuments(final String sourceType, final Multipart multipart) throws MessagingException, IOException {
        // Part two is attachment
        if (sourceType.equalsIgnoreCase("VENDOR")) {
            final String fileName = getClass().getResource("/resources/vendoruser/Vendor User Guide.pdf").getFile();
            final File file = new File(fileName);
            final DataSource docSource = new FileDataSource(file);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(docSource));
            messageBodyPart.setFileName(file.getName());
            multipart.addBodyPart(messageBodyPart);
        } else if (sourceType.equalsIgnoreCase("APPNTCONF")) {
            //BPM Attachment documents required to be
            // SAP Document service in CF.
            //
            final Result documentRequestResults = vendorBoardingDocumentService.getBPMDocumentByName(SourceType.APPNT_CONF_SRC.getValue(), SourceType.APPNT_CONF_PATH.getValue());
            //Consider links from SAP Document service in CF.
            //Download those documents.
            if (documentRequestResults.getResultType().equals(ResultType.SUCCESS)) {
                final BPMDocument bpmFolder = (BPMDocument) documentRequestResults.getResult();
                final List<BPMDocument> bpmDocuments = bpmFolder.getCollection();
                //CmisObject tempDoc = null;
               // for (BPMDocument elementDoc : bpmDocuments) {
                    /*tempDoc = directoryServiceBean.getDocumentByOpenCmisID(elementDoc.getOpenCmisID());
                    Document castDoc = (Document) tempDoc;
                    InputStream inputStream = castDoc.getContentStream().getStream();
                    final String fileName = castDoc.getContentStream().getFileName();
                    String suffix = fileName.substring(fileName.lastIndexOf("."));
                    final File file = File.createTempFile(fileName, suffix);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    IOUtils.writeToOutputStream(inputStream, fileOutputStream);
                    final DataSource dataSource = new FileDataSource(file);
                    final MimeBodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(new DataHandler(dataSource));
                    messageBodyPart.setFileName(fileName);
                    multipart.addBodyPart(messageBodyPart);
                }*/
            }
        }
    }
}
