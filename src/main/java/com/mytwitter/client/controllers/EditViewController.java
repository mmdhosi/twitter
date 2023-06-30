package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.event.ActionEvent;

public class EditViewController {
    @FXML
    private Button submit;
    @FXML
    private TextField bioText;
    @FXML
    private ImageView blockButton;

    private Stage currentStage;
    private Requester requester;
    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }


}
