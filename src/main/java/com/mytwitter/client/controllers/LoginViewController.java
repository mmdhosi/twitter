package com.mytwitter.client.controllers;


import com.mytwitter.client.Requester;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {
    private Stage currentStage;
    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label errorLBL;


    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }


    @FXML
    private void submitButtonAction(ActionEvent event) {
        String username=usernameTextField.getText();
        String password= passwordField.getText();
        Requester requester = Requester.login(username, password);

        if(requester == null){
            errorLBL.setText(" username or password is wrong");
        } else {
            currentStage.hide();

            new HomeController(currentStage);
        }
    }

}
