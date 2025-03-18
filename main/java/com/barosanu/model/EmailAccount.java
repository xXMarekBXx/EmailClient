package com.barosanu.model;

import javax.mail.Store;
import java.util.Properties;

public class EmailAccount {

    private String address;
    private String password;
    private Properties properties;
    private Store store;

    public EmailAccount(String address, String password) {
        this.address = address;
        this.password = password;
        properties = new Properties();

        // IMAP configuration
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", "poczta.interia.pl");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        // SMTP configuration
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "poczta.interia.pl");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.starttls.enable", "true");
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
