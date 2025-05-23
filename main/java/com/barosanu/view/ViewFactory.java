package com.barosanu.view;

import com.barosanu.EmailManager;
import com.barosanu.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ViewFactory {

    private EmailManager emailManager;
    private ArrayList<Stage> activeStages;
    private boolean mainViewInitialized = false;

    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
        activeStages = new ArrayList<Stage>();
    }

    public boolean isMainViewInitialized(){
        return mainViewInitialized;
    }

    //View options handling:
    private ColorTheme colorTheme = ColorTheme.DARK;
    private FontSize fontSize = FontSize.MEDIUM;

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(ColorTheme colorTheme) {
        this.colorTheme = colorTheme;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public void showLoginWindow(){
        System.out.println("showLoginWindow function from ViewFactory called");

        BaseController controller = new LoginWindowController(emailManager, this, "/view/css/LoginWindow.fxml");
        initializeStage(controller);
    }

    public void showMainWindow(){

        System.out.println("showMainWindow function from ViewFactory called");

        BaseController controller = new MainWindowController(emailManager, this, "/view/css/MainWindow.fxml");
        initializeStage(controller);
        mainViewInitialized = true;
    }

    public void showOptionsWindow(){

        System.out.println("showOptionsWindow function from ViewFactory called");

        BaseController controller = new OptionsWindowController(emailManager, this, "/view/css/OptionsWindow.fxml");
        initializeStage(controller);
    }

    public void showComposeMessageWindow(){
        System.out.println("showComposeMessageWindow function from ViewFactory called");
        BaseController controller = new ComposeMessageController(emailManager, this, "/view/css/ComposeMessageWindow.fxml");
        initializeStage(controller);
    }

    public void showEmailDetailsWindow(){
        System.out.println("showEmailDetailsWindow function from ViewFactory called");
        try{
            BaseController controller = new EmailDetailsController(emailManager, this, "/view/css/EmailDetailsWindow.fxml");
            initializeStage(controller);
        }catch(Exception e){
            System.out.println("Failed to load EmailDetailsWindow.fxml");
            e.printStackTrace();
        }
    }

    private void initializeStage(BaseController baseController){

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
        fxmlLoader.setController(baseController);
        Parent parent;
        try{
            parent = fxmlLoader.load();
        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        updateStyle(scene);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        activeStages.add(stage);
    }

    public void closeStage(Stage stageToClose){
        stageToClose.close();
        activeStages.remove(stageToClose);
    }

    public void updateAllStyles() {
        System.out.println("updateStyles 1");
        for(Stage stage: activeStages){
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(ColorTheme.getCssPath(colorTheme)).toExternalForm());
            scene.getStylesheets().add(getClass().getResource(FontSize.getCssPath(fontSize)).toExternalForm());
            updateStyle(scene);
        }
    }

    private void updateStyle(Scene scene){
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(ColorTheme.getCssPath(colorTheme)).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(FontSize.getCssPath(fontSize)).toExternalForm());
    }
}
