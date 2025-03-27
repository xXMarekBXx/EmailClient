package com.barosanu.model;

import javax.mail.Session;
import javax.mail.Store;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class EmailAccount {

    private String address;
    private String password;
    private Properties properties;
    private Store store;
    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public EmailAccount(String address, String password) {
        this.address = address;
        this.password = password;
        properties = new Properties();

        // === IMAP Configuration ===
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", "imap.interia.pl");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.auth", "true");
        properties.put("mail.imap.ssl.enable", "true");

        // === SMTP Configuration ===
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.interia.pl");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");

        // === Enable STARTTLS ===
        properties.put("mail.smtp.starttls.enable", "true");

         /**
         * Disable SSL verification to avoid connecting problems
         * This forces Java to accept all SSL certificates, even invalid ones.
         */
        disableSSLCertificateValidation();
    }

    private void disableSSLCertificateValidation() {
        try {
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new SecureRandom());
            SSLContext.setDefault(sslContext);
            System.out.println("SSL certificate validation disabled.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === Getters & Setters ===
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

    @Override
    public String toString() {
        return address;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
