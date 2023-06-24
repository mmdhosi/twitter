package com.mytwitter.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupViewController {
    private Stage currentStage;
    @FXML
    private DatePicker birthdateField;

    @FXML
    private TextField countryField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField passwordRepeatField;

    @FXML
    private TextField phonenumberField;

    @FXML
    private Button submitButton;

    @FXML
    private TextField usernameField;

    @FXML
    private void submitButtonAction(ActionEvent event){
        //TODO: get each field
        //TODO: Check repeat password
        //TODO: EMAIL ya phone number bashe hatman
        //TODO: Requester.signup(user)
        String username = usernameField.getText();
    }


    public void setStage(Stage stage){
        this.currentStage = stage;
    }


}
