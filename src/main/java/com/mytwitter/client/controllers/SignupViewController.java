package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import com.mytwitter.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupViewController {

    private Stage currentStage;

    @FXML
    private DatePicker birthdateField;

    @FXML
    private TextField countryField;

    @FXML
    private Label repeatPasswordError;

    @FXML
    private Label requiredError;

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
    private Label passwordError;

    @FXML
    private Label usernameError;

    @FXML
    private Label emailError;



    private void waitToFix(Alert alert){
        try {
            alert.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void submitButtonAction(ActionEvent event) {
        //TODO: uniq bodan
        Alert alert = new Alert(Alert.AlertType.ERROR);

        if (Objects.equals(usernameField.getText(), "")) {
            usernameError.setText("username is required");
            waitToFix(alert);
        }
        usernameError.setText("");


        if (Objects.equals(passwordField.getText(), "")) {
            passwordError.setText("password is required");
            waitToFix(alert);
        }
        else {
            passwordError.setText("");
            String pass = passwordField.getText();
            if (pass.length() < 8) {
                passwordError.setText("password must be 8 character at least");
                waitToFix(alert);
            }
            passwordError.setText("");
            if (!pass.matches(".*[A-Z].*")) {
                passwordError.setText("password must have capital characters");
                waitToFix(alert);
            }
            passwordError.setText("");
            if (!pass.matches(".*[a-z].*")) {
                passwordError.setText("password must have small characters");
                waitToFix(alert);
            }
            passwordError.setText("");
        }
        if (!Objects.equals(passwordField.getText(), passwordRepeatField.getText())){
            if(Objects.equals(repeatPasswordError.getText(), "")){
                repeatPasswordError.setText("repeated password is required");
                waitToFix(alert);
            }
            repeatPasswordError.setText("");
            repeatPasswordError.setText("repeated password is wrong");
            waitToFix(alert);
        }
        repeatPasswordError.setText("");
        if(Objects.equals(emailField.getText(), "") && Objects.equals(phonenumberField.getText(), "")){
            requiredError.setText("Email or Phone number is required");
            waitToFix(alert);
        }
        requiredError.setText("");

        String email = emailField.getText();
        if(!Objects.equals(email, "")) {
            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                emailError.setText("email is invalid");
                waitToFix(alert);
            }
            emailError.setText("");
        }

        String username = usernameField.getText();
        String firstname= firstNameField.getText();
        String lastname=lastNameField.getText();
        String phonenumber=phonenumberField.getText();
        String password=passwordField.getText();
        String country=countryField.getText();
        String birthdate= String.valueOf(birthdateField.getValue());
        User user=new User(username,firstname,lastname,email,phonenumber,password,country,birthdate);
//        Requester.signup(user);

        currentStage.hide();

        Scene loginScene = null;
        FXMLLoader loginloader = null;
        try {
            loginloader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            loginScene = new Scene(loginloader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage signupStage = new Stage();
        LoginViewController controller = loginloader.getController();
        signupStage.setScene(loginScene);
        controller.setCurrentStage(signupStage);
        signupStage.show();
    }


    public void setStageCurrent(Stage stage){
        this.currentStage = stage;
    }


}