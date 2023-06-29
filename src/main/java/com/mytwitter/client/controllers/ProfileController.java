package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProfileController {
    @FXML
    private ImageView avatarImageView;

    @FXML
    private Text bioText;

    @FXML
    private Label birthdate;

    @FXML
    private HBox birthdateBox;

    @FXML
    private Button blockButton;

    @FXML
    private Button followButton;

    @FXML
    private Hyperlink followersLink;

    @FXML
    private Hyperlink followingLink;

    @FXML
    private Label fullnameLabel;

    @FXML
    private ImageView headerImageView;

    @FXML
    private Button homeButton;

    @FXML
    private Label joindate;

    @FXML
    private ListView<?> tweetList;

    @FXML
    private Label usernameLabel;

    @FXML
    private HBox webAddressBox;

    @FXML
    private Hyperlink webAddressLink;

    private Stage currentStage;
    private Requester requester = Requester.getRequester();

    public ProfileController(Stage currentStage) {

        this.currentStage = currentStage;

        Scene profileScene = null;
        FXMLLoader profileLoader = null;
        try {
            profileLoader = new FXMLLoader(getClass().getResource("/fxml/profile-view.fxml"));
            profileScene = new Scene(profileLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
        profileLoader.setController(this);
        currentStage.setScene(profileScene);
        currentStage.show();
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    @FXML
    public void clickOnHomeButton(ActionEvent event) {

        currentStage.hide();

        new HomeController(currentStage, requester);
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
