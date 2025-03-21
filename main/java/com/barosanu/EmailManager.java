package com.barosanu;

import com.barosanu.controller.services.FetchFolderService;
import com.barosanu.model.EmailAccount;
import com.barosanu.model.EmailTreeItem;
import javafx.scene.control.TreeItem;

public class EmailManager {

    private EmailTreeItem<String> foldersRoot = new EmailTreeItem<String>("");

    public EmailTreeItem<String> getFoldersRoot(){
        return foldersRoot;
    }

    public void addEmialAccount(EmailAccount emailAccount){
        EmailTreeItem<String> treeItem = new EmailTreeItem<String>(emailAccount.getAddress());
//        treeItem.setExpanded(true);
//            treeItem.getChildren().add(new TreeItem<String>("INBOX"));
//            treeItem.getChildren().add(new TreeItem<String>("Sent"));
//            treeItem.getChildren().add(new TreeItem<String>("Folder1"));
//            treeItem.getChildren().add(new TreeItem<String>("Spam"));
        FetchFolderService fetchFolderService = new FetchFolderService(emailAccount.getStore(), treeItem);
        fetchFolderService.start();
        foldersRoot.getChildren().add(treeItem);
    }
}
