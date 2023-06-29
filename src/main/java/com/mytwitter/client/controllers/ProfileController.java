package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProfileController {
    @FXML
    private Button homeButton;
    @FXML
    private Button followButton;
    @FXML
    private Button blockButton;

    private Stage currentStage;
    private Requester requester;

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    @FXML
    public void clickOnHomeButton(ActionEvent event) {

        currentStage.hide();

        Scene homeScene = null;
        FXMLLoader homeLoader = null;
        try {
            homeLoader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            homeScene = new Scene(homeLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage homeStage = new Stage();
        HomeController controller = homeLoader.getController();
        homeStage.setScene(homeScene);
        controller.setCurrentStage(homeStage);
        controller.setRequester(requester);
        homeStage.show();
    }



    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public void clickOnBlockOrUnblock(javafx.event.ActionEvent event) {
        //TODO:get username
        String[] jwtParts = requester.getJwt().split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(jwtParts[1]));

        String usernameRegex = "\"username\":\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(payload);
        String username = null;
        if (matcher.find()) {
            username = matcher.group(1);
        } else {
            System.out.println("Username not found in JWT payload.");
        }
            if (Objects.equals(blockButton.getText(), "block")) {
                requester.block(username);
                blockButton.setText("unblock");
            } else {
                requester.unblock(username);
                blockButton.setText("block");
            }

    }

    public void clickOnFollowOrUnFollow(javafx.event.ActionEvent event) {
        //TODO:get username
        String[] jwtParts = requester.getJwt().split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(jwtParts[1]));

        String usernameRegex = "\"username\":\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(payload);
        String username = null;
        if (matcher.find()) {
            username = matcher.group(1);
        } else {
            System.out.println("Username not found in JWT payload.");
        }
        if (Objects.equals(followButton.getText(), "follow")) {
            requester.follow(username);
            followButton.setText("unfollow");
        } else {
            requester.unfollow(username);
            followButton.setText("follow");
        }
    }
}
