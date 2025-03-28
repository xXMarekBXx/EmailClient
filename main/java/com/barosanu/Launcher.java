package com.barosanu;

import com.barosanu.view.ViewFactory;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class Launcher extends Application{

    @FXML
    public void buttonAction(){
        System.out.println("Clicked on Button!");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        ViewFactory viewFactory = new ViewFactory(new EmailManager());
        viewFactory.showLoginWindow();
    }
}
