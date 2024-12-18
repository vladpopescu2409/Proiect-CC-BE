package com.project.HR.Connect.service;

import com.project.HR.Connect.dto.EmailDTO;
import com.project.HR.Connect.entitie.Request;
import com.project.HR.Connect.entitie.User;
import com.spire.doc.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Service
public class EmailSenderService {
    @Autowired
    DocumentCreatorService documentCreatorService;

    public Session session(){
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        return Session.getInstance(properties, new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("noreply.hrconnect1@gmail.com", "fzmereeemwmuefgw");
            }
        });
    }
    public void sendEmail(EmailDTO emailDTO) {
        Session session = session();

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("noreply.hrconnect1@gmail.com"));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailDTO.getEmailRecipient()));
            mimeMessage.setSubject(emailDTO.getSubject());
            mimeMessage.setText(emailDTO.getBody());

            Transport.send(mimeMessage);
            System.out.println("Mail sent!");

        }catch (MessagingException e){
            e.printStackTrace();
        }
    }
    public void sendEmailWithAttachment(EmailDTO emailDTO) {
        Session session = session();

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("noreply.hrconnect1@gmail.com"));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailDTO.getEmailRecipient()));
            mimeMessage.setSubject(emailDTO.getSubject());

            Multipart multipart = new MimeMultipart();
            MimeBodyPart attachmentPart = new MimeBodyPart();
            MimeBodyPart textPart = new MimeBodyPart();

        try {
            Document document = documentCreatorService.createDocumentFromExampleTemplate();
            File file = documentCreatorService.convertDocumentToFile(document);
            attachmentPart.attachFile(file);

            textPart.setText(emailDTO.getBody());
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            mimeMessage.setContent(multipart);
            Transport.send(mimeMessage);
            System.out.println("Mail sent!");
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

    @Async
    public void sendWelcomeEmail(String emailAddress, String firstName){
        String newLine = System.getProperty("line.separator");
        String body = "Hello, " + firstName +"!" + newLine + "We are delighted to inform you that your account has " +
                "been successfully created! Please remember to change your password within 24 hours of receiving this email."
                + newLine + newLine + "Cheers," + newLine + "HR Connect Team";
        EmailDTO emailDTO = new EmailDTO(emailAddress, "Welcome to HR Connect!", body);
        sendEmail(emailDTO);
    }

    public boolean sendFailEmail(String emailAddress, String firstName){
        String newLine = System.getProperty("line.separator");
        String body = "Hello, " + firstName +"!" + newLine + "We regret to inform you that we are unable to generate " +
                 "your requested document due to unfulfilled requirements" + newLine
                + "We apologize for any inconvenience caused." + newLine + newLine + "Kind regards," + newLine +
                "HR Connect Team";
        EmailDTO emailDTO = new EmailDTO(emailAddress, "Document fail", body);
        sendEmail(emailDTO);

        return false;
    }

    public boolean sendAcceptanceEmail(String emailAddress, String firstName, String requestName){
        String newLine = System.getProperty("line.separator");
        String body = "Hello, " + firstName +"!" + newLine + "We are delighted to inform you that your " + requestName
                + " request has been approved" + newLine + newLine + "Have a nice day," + newLine + "HR Connect Team";
        EmailDTO emailDTO = new EmailDTO(emailAddress, "Request approved", body);
        sendEmail(emailDTO);

        return true;
    }

    @Async
    public void sendEmailWithDocument(Request request, long numberOfRequests) {
        Session session = session();
        User requester = request.getRequester();

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("noreply.hrconnect1@gmail.com"));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(requester.getLoginDetails().getEmail()));

            mimeMessage.setSubject("Requested document");

            Multipart multipart = new MimeMultipart();
            MimeBodyPart attachmentPart = new MimeBodyPart();
            MimeBodyPart textPart = new MimeBodyPart();

            try {
                Document document = documentCreatorService.createDocumentFromTemplate(request, numberOfRequests);

                File file = documentCreatorService.convertDocumentToFile(document);
                attachmentPart.attachFile(file);
                String newLine = System.getProperty("line.separator");
                String body = "Hello, " + requester.getFirstName() +"!" + newLine + "Here is your requested document"
                        + newLine + newLine + "Cheers," + newLine + "HR Connect Team";

                textPart.setText(body);
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);
                mimeMessage.setContent(multipart);
                Transport.send(mimeMessage);
                System.out.println("Mail sent!");
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

}
