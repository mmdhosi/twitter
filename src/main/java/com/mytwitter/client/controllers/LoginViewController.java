package com.mytwitter.client.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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


    }

}
