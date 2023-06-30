package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import com.mytwitter.client.TweetCell;
import com.mytwitter.tweet.Tweet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuoteRetweetViewController implements Initializable {

    @FXML
    private VBox quoteBox;

    @FXML
    private Button quoteSubmit;

    @FXML
    private TextArea quoteTextArea;

    @FXML
    private VBox retweetBox;

    @FXML
    private Button retweetSubmit;

    private Requester requester = Requester.getRequester();
    private Stage currentStage;
    private Tweet tweet;

    public QuoteRetweetViewController(Tweet tweet) {
        this.tweet = tweet;

        currentStage = new Stage();

        Scene scene = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/retweet-quote-view.fxml"));
            loader.setController(this);
            scene = new Scene(loader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
        currentStage.setScene(scene);
        currentStage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        retweetSubmit.setOnAction(event -> {
            requester.retweet(tweet.getTweetId());
            currentStage.close();
        });
        quoteSubmit.setOnAction(event -> {
            requester.quote(quoteTextArea.getText(), tweet.getTweetId());
            currentStage.close();
        });

        quoteBox.getChildren().add(TweetCell.createTweet(currentStage, tweet, false, false));
        retweetBox.getChildren().add(TweetCell.createTweet(currentStage, tweet, false, false));

    }
}
