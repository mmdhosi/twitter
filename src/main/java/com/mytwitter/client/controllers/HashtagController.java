package com.mytwitter.client.controllers;


import com.mytwitter.client.Requester;
import com.mytwitter.client.TweetCell;
import com.mytwitter.tweet.Quote;
import com.mytwitter.tweet.Retweet;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.UserProfile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HashtagController implements Initializable {


    private Stage currentStage;
    private Requester requester = Requester.getRequester();

    @FXML
    private ListView<Tweet> listView;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button homeButton;

    ObservableList<Tweet> items = FXCollections.observableArrayList();

    String hashtag;


    public HashtagController(Stage currentStage, String hashtag) {
        this.hashtag = hashtag;
        this.currentStage = currentStage;

        Scene scene = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/hashtag-view.fxml"));
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

        // set listview items
        listView.setItems(items);

        // set search button image
        Image image = new Image("file:icons/magnifier.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        searchButton.setGraphic(imageView);

        // set hashtag name in search box
        searchField.setText("#" + hashtag);

        homeButton.setOnAction(event -> {
            new HomeController(currentStage, requester);
        });

        listView.setStyle("-fx-control-inner-background: #FFFFFF;");
        listView.setCellFactory(new Callback<ListView<Tweet>, ListCell<Tweet>>() {
            @Override
            public ListCell<Tweet> call(ListView<Tweet> tweetListView) {
                return new TweetCell(currentStage, requester);
            }
        });

        // set listview items
        ArrayList<Tweet> hashtagTweets = requester.getHashtagTweets(hashtag);
        if(hashtagTweets != null)
            items.setAll(hashtagTweets);

    }

    @FXML
    public void searchAction(ActionEvent event) {

        String keyword = searchField.getText();
        ArrayList<UserProfile> searchedUsers = requester.search(keyword);
        new SearchListController(currentStage, searchedUsers);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
        items.setAll(requester.getTimeline());
        listView.setItems(items);
    }
}
