package com.barosanu.controller.services;

import com.barosanu.EmailManager;
import com.barosanu.controller.EmailLoginResult;
import com.barosanu.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.*;

public class LoginService extends Service<EmailLoginResult> {

    private final EmailAccount emailAccount;
    private final EmailManager emailManager;

    public LoginService(EmailAccount emailAccount, EmailManager emailManager) {
        this.emailAccount = emailAccount;
        this.emailManager = emailManager;
    }

    private EmailLoginResult login() {
        // Using javax.mail.Authenticator to handle authentication
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println("Authenticating user: " + emailAccount.getAddress());
                return new PasswordAuthentication(emailAccount.getAddress(), emailAccount.getPassword());
            }
        };

        try {
            Thread.sleep(100);
            // Create a mail session with the provided authenticator
            Session session = Session.getInstance(emailAccount.getProperties(), authenticator);
            Store store = session.getStore("imaps");

            // Retrieve the IMAP host from properties
            String imapHost = emailAccount.getProperties().getProperty("mail.imap.host");

            System.out.println("Connecting to IMAP server: " + imapHost);

            // Connect to the mail server using credentials
            store.connect(imapHost, emailAccount.getAddress(), emailAccount.getPassword());

            // Store the connection for further use
            emailAccount.setStore(store);
            emailManager.addEmialAccount(emailAccount);


            System.out.println("Login successful!");
            return EmailLoginResult.SUCCESS;
        } catch (NoSuchProviderException e) {
            System.err.println("Mail provider error: " + e.getMessage());
            e.printStackTrace();
            return EmailLoginResult.FAILED_BY_NETWORK;
        } catch (AuthenticationFailedException e) {
            System.err.println("Login failed: Incorrect email or password!");
            e.printStackTrace();
            return EmailLoginResult.FAILED_BY_CREDENTIALS;
        } catch (MessagingException e) {
            System.err.println("Unexpected mail error: " + e.getMessage());
            e.printStackTrace();
            return EmailLoginResult.FAILED_BY_UNEXPECTED_ERROR;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Task<EmailLoginResult> createTask() {
        return new Task<EmailLoginResult>() {
            @Override
            protected EmailLoginResult call() throws Exception {
                return login();
            }
        };
    }
}
