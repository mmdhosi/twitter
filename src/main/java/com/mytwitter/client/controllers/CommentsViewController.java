package com.mytwitter.client.controllers;

import com.mytwitter.client.ReplyCell;
import com.mytwitter.client.Requester;
import com.mytwitter.client.TweetCell;
import com.mytwitter.tweet.Reply;
import com.mytwitter.tweet.Tweet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CommentsViewController {

    private static Stage stage;
    private Tweet tweet;

    private static StackPane stackPane = new StackPane();

    public CommentsViewController(Stage stage, Tweet tweet) {
        CommentsViewController.stage = stage;
        this.tweet = tweet;


        Button backButton = new Button();
        backButton.setText("⬅");
        backButton.setFont(new Font(16));
        backButton.setOnAction(event -> {
            int size = stackPane.getChildren().size();
            if(size == 1){
                stackPane.getChildren().remove(0);
                new HomeController(stage);
            } else if(size > 1){
                stackPane.getChildren().remove(size-1);
                stackPane.getChildren().get(size-2).setVisible(true);
            }

        });

        Scene scene = new Scene(new VBox(backButton, stackPane));
        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(600);
        stage.show();

        createCommentsView(stage, tweet);

    }


    public static void createCommentsView(Stage stage, Tweet subjectTweet) {
        Requester requester = Requester.getRequester();

        // create replies list view
        ListView<Reply> repliesListView = new ListView<>();
        ObservableList<Reply> repliesList = FXCollections.observableArrayList();
        repliesList.setAll(requester.getReplies(subjectTweet.getTweetId()));
        repliesListView.setItems(repliesList);

        repliesListView.setCellFactory(replyListView -> new ReplyCell(stage, requester, stackPane));

        // new comment field
        TextField newCommentField = new TextField();

        newCommentField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                TextField textField = (TextField) keyEvent.getSource();
                textField.setText("");
                requester.comment(newCommentField.getText(), subjectTweet.getTweetId());
                repliesList.setAll(requester.getReplies(subjectTweet.getTweetId()));
            }
        });

        // position new nodes and subject tweet node
        VBox subjectTweetBox = TweetCell.createTweet(stage, subjectTweet, true, false);
        AnchorPane anchorPane = new AnchorPane(subjectTweetBox, repliesListView, newCommentField);


        AnchorPane.setLeftAnchor(subjectTweetBox, 30.);
        AnchorPane.setRightAnchor(subjectTweetBox, 20.);
        AnchorPane.setTopAnchor(subjectTweetBox, 10.);

        AnchorPane.setLeftAnchor(repliesListView, 50.);
        AnchorPane.setRightAnchor(repliesListView, 20.);
        AnchorPane.setTopAnchor(repliesListView, 130.);
        AnchorPane.setBottomAnchor(repliesListView, 100.);

        AnchorPane.setLeftAnchor(newCommentField, 50.);
        AnchorPane.setRightAnchor(newCommentField, 20.);
        AnchorPane.setBottomAnchor(newCommentField, 20.);

        int size = stackPane.getChildren().size();
        if(size > 0)
            stackPane.getChildren().get(size-1).setVisible(false);
        stackPane.getChildren().add(anchorPane);
    }
}
