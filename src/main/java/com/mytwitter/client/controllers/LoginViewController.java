package com.mytwitter.client.controllers;


import com.mytwitter.client.Requester;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {
    private Stage currentStage;
    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label errorLBL;

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }


    @FXML
    private void submitButtonAction(ActionEvent event) {
        //TODO: vojod username va dorost bodan password
        String username=usernameTextField.getText();
        String password=passwordTextField.getText();
        Requester requester = Requester.login(username, password);
        //TODO: * for password

        if(requester == null){
            //TODO: username or pass is wrong
        } else {
            currentStage.hide();

            Scene homeScene = null;
            FXMLLoader homeLoader = null;
            try {
                homeLoader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
                homeScene = new Scene(homeLoader.load());

            } catch (IOException e) {
                e.printStackTrace();
            }
            //TODO: not very important. Server is offline
            Stage homeStage = new Stage();
            HomeController controller = homeLoader.getController();
            homeStage.setScene(homeScene);
            controller.setCurrentStage(homeStage);
            controller.setRequester(requester);
            homeStage.show();
        }
    }

}
