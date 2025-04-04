package com.barosanu.controller.services;

import com.barosanu.model.EmailMessage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import org.jsoup.Jsoup;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

public class MessageRendererService extends Service<Void> {

    private static EmailMessage emailMessage;
    private WebEngine webEngine;
    private static StringBuffer stringBuffer;

    public MessageRendererService(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.stringBuffer = new StringBuffer();
        this.setOnSucceeded(event -> displayMessage());
    }

    public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    private void displayMessage() {
        webEngine.loadContent(stringBuffer.toString());
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                try {
                    loadMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    public static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString().trim();
        } else if (message.isMimeType("text/html")) {
            return Jsoup.parse(message.getContent().toString()).html();
        } else if (message.isMimeType("multipart/*")) {
            return getTextFromMultipart((Multipart) message.getContent());
        }
        return "";
    }

    private static String getTextFromMultipart(Multipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent().toString().trim());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(Jsoup.parse(bodyPart.getContent().toString()).html());
            } else if (bodyPart.isMimeType("multipart/*")) {
                result.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            } else if (BodyPart.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                MimeBodyPart mbp = (MimeBodyPart) bodyPart;

                // Prevent duplicate attachments
                if (!emailMessage.containsAttachment(mbp.getFileName())) {
                    handleAttachment(bodyPart);
                }
            }
        }
        return result.toString();
    }

    private static void handleAttachment(BodyPart bodyPart) throws Exception {
        MimeBodyPart mbp = (MimeBodyPart) bodyPart;
        emailMessage.addAttachment(mbp);
        System.out.println("Attachments found: " + mbp.getFileName());
    }

    private void loadMessage() throws Exception {
        stringBuffer.setLength(0);
        Message message = emailMessage.getMessage();
        String contentType = message.getContentType();
        System.out.println("Content Type: " + contentType);
        if (isSimpleType(contentType)) {
            System.out.println("isSimpleType");
            stringBuffer.append(getTextFromMessage(message));
        } else if (isMultipartType(contentType)) {
            System.out.println("isMultipartType");
            Multipart multipart = (Multipart) message.getContent();
            stringBuffer.append(getTextFromMultipart(multipart));
        }
    }

    private boolean isSimpleType(String contentType) {
        return contentType.toLowerCase().contains("text/plain") ||
                contentType.toLowerCase().contains("text/html");
    }

    private boolean isMultipartType(String contentType) {
        return contentType.toLowerCase().contains("multipart");
    }
}
