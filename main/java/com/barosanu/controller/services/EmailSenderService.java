package com.barosanu.controller.services;

import com.barosanu.controller.EmailSendingResult;
import com.barosanu.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;

public class EmailSenderService extends Service<EmailSendingResult> {

    private EmailAccount emailAccount;
    private String subject;
    private String recipient;
    private String content;
    private List<File> attachments;

    public EmailSenderService(EmailAccount emailAccount, String subject, String recipient, String content, List<File> attachments) {
        this.emailAccount = emailAccount;
        this.subject = subject;
        this.recipient = recipient;
        this.content = content;
        this.attachments = attachments;

        System.out.println("EmailSenderService - SUBJECT: '" + subject + "'");
    }

    @Override
    protected Task<EmailSendingResult> createTask() {
        return new Task<EmailSendingResult>() {
            @Override
            protected EmailSendingResult call() {
                try {
                    //Create the message:
                    MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
                    mimeMessage.setFrom(emailAccount.getAddress());
                    mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                    mimeMessage.setSubject(subject);

                    //Set the content:
                    Multipart multipart = new MimeMultipart();
                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(content, "text/html");
                    multipart.addBodyPart(messageBodyPart);
                    mimeMessage.setContent(multipart);

                    // Adding attachments to the email
                    if (attachments != null && attachments.size() > 0) {
                        System.out.println("Attachments found: " + attachments.size());

                        for (File file : attachments) {
                            if (file == null || !file.exists()) {
                                System.out.println("Attachment file is null or does not exist: " + file);
                                continue;
                            }

                            try {
                                MimeBodyPart mimeBodyPart = new MimeBodyPart();

                                // Creating a DataSource for the file
                                javax.activation.DataSource source = new javax.activation.FileDataSource(file.getAbsolutePath());
                                mimeBodyPart.setDataHandler(new DataHandler(source));
                                mimeBodyPart.setFileName(file.getName());

                                // Adding the attachment to the multipart
                                multipart.addBodyPart(mimeBodyPart);

                                System.out.println("Attachment added successfully: " + file.getName());

                            } catch (Exception e) {
                                System.out.println("Failed to attach file: " + file.getName());
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println("No attachments to add.");
                    }

                    //Sending the message:
                    Transport transport = emailAccount.getSession().getTransport();
                    transport.connect(
                            emailAccount.getProperties().getProperty("outgoingHost"),
                            emailAccount.getAddress(),
                            emailAccount.getPassword()
                    );
                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                    transport.close();

                    return EmailSendingResult.SUCCESS;
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return EmailSendingResult.FAILED_BY_PROVIDER;
                } catch (Exception e) {
                    e.printStackTrace();
                    return EmailSendingResult.FAILED_BY_UNEXPECTED_ERROR;
                }
            }
        };
    }
}
