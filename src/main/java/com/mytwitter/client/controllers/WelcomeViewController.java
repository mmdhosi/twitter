package com.mytwitter.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeViewController  {

    private Stage currentStage;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private void signupAction(ActionEvent event) {
        currentStage.hide();
        Scene signupScene = null;
        FXMLLoader signupLoader = null;
        try {
            signupLoader = new FXMLLoader(getClass().getResource("/fxml/signup-view.fxml"));
            signupScene = new Scene(signupLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage signupStage = new Stage();
        SignupViewController controller = signupLoader.getController();
        signupStage.setScene(signupScene);
        controller.setStageCurrent(signupStage);
        signupStage.show();
    }
    private void loginAction(ActionEvent event) {
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


    public Button getLoginButton() {
        return loginButton;
    }

    public Button getSignupButton() {
        return signupButton;
    }



    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}

