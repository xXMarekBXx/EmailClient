package com.barosanu.controller;

import com.barosanu.EmailManager;
import com.barosanu.controller.services.MessageRendererService;
import com.barosanu.model.EmailMessage;
import com.barosanu.view.ViewFactory;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.concurrent.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.nio.file.Paths;

public class EmailDetailsController extends BaseController implements Initializable {

    private String LOCATION_OF_DOWNLOAD = Paths.get(System.getProperty("user.home"), "Downloads").toString();

    @FXML
    private WebView webView;

    @FXML
    private Label attachmentsLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label senderLabel;

    @FXML
    private HBox hBoxDownloads;

    public EmailDetailsController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        EmailMessage emailMessage = emailManager.getSelectedMessage();
        subjectLabel.setStyle("-fx-text-fill: black;");
        senderLabel.setStyle("-fx-text-fill: black;");
        subjectLabel.setText(emailMessage.getSubject());
        senderLabel.setText(emailMessage.getSender());
        laodAttachments(emailMessage);

        MessageRendererService messageRendererService = new MessageRendererService(webView.getEngine());
        messageRendererService.setEmailMessage(emailMessage);
        messageRendererService.restart();
    }

    private void laodAttachments(EmailMessage emailMessage) {
        if(emailMessage.hasAttachments()){
            for(MimeBodyPart mimeBodyPart: emailMessage.getAttachmentList()){
                try {
                    Button button = new AttachmentButton(mimeBodyPart);
                    hBoxDownloads.getChildren().add(button);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }else{
            attachmentsLabel.setText("");
        }
    }

    private class AttachmentButton extends Button{

        private MimeBodyPart mimeBodyPart;
        private String downloadedFilePath;

        public AttachmentButton(MimeBodyPart mimeBodyPart) throws MessagingException {
            this.mimeBodyPart = mimeBodyPart;
            this.setText(mimeBodyPart.getFileName());
            this.downloadedFilePath = Paths.get(LOCATION_OF_DOWNLOAD, mimeBodyPart.getFileName()).toString();

            this.setOnAction(e -> downloadAttachment());
        }

        private void downloadAttachment() {
            colorBlue();
            Service<Void> service = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            mimeBodyPart.saveFile(downloadedFilePath);
                            System.out.println("File saved in: " + downloadedFilePath);
                            return null;
                        }
                    };
                }
            };
            service.start();
            service.setOnSucceeded(e->{
                colorGreen();
                this.setOnAction( e2->{
                    File file = new File(downloadedFilePath);
                    Desktop desktop = Desktop.getDesktop();
                    if(file.exists()){
                        try{
                            desktop.open(file);
                        }catch(Exception exception){
                            exception.printStackTrace();
                        }
                    }
                });
            });
        }

        private void colorBlue(){
            this.setStyle("-fx-background-color: Blue");
        }

        private void colorGreen(){
            this.setStyle("-fx-background-color: Green");
        }
    }
}
