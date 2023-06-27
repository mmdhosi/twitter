package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import javafx.stage.Stage;

public class ProfileController {

    private Stage currentStage;
    private Requester requester;

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }
}
