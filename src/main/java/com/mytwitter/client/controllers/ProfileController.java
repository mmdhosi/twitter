package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;

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
    public void clickOnHomeButton(ActionEvent event){

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
    @FXML
    public void clickOnFollowOrUnFollow(ActionEvent event){
        //TODO:get username
        if(Objects.equals(followButton.getText(), "follow")){
            requester.unfollow("username");
        }else {
            requester.follow("username");
        }


    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

}
