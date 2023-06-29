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

public class CommentsViewController implements Initializable {

    @FXML
    private TextField commentField;

    @FXML
    private ListView<?> commentsList;

    @FXML
    private VBox tweetBox;

    private static Stage stage;
    private static Requester requester;
    private int tweetId;

    private static StackPane stackPane = new StackPane();

    public CommentsViewController(Stage stage, Requester requester, Tweet tweet) {
        this.stage = stage;
        this.requester = requester;
        this.tweetId = tweetId;

        Button backButton = new Button();
        backButton.setText("⬅");
        backButton.setFont(new Font(16));
        backButton.setOnAction(event -> {
            int size = stackPane.getChildren().size();
            if(size == 1){
                new HomeController(stage, requester);
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

        createCommentsView(stage, requester, tweet);

    }


    public static void createCommentsView(Stage stage, Requester requester, Tweet tweet) {
        ListView<Reply> repliesListView = new ListView<>();
        ObservableList<Reply> repliesList = FXCollections.observableArrayList();
        repliesList.setAll(requester.getReplies(tweet.getTweetId()));
        repliesListView.setItems(repliesList);

        repliesListView.setCellFactory(replyListView -> new ReplyCell(stage, requester, stackPane));

        TextField newCommentField = new TextField();

        newCommentField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                requester.comment(newCommentField.getText(), tweet.getTweetId());
                repliesList.setAll(requester.getReplies(tweet.getTweetId()));
            }
        });

        AnchorPane anchorPane = new AnchorPane(TweetCell.createTweet(stage, tweet), repliesListView, newCommentField);

        AnchorPane.setLeftAnchor(repliesListView, 20.);
        AnchorPane.setRightAnchor(repliesListView, 20.);
        AnchorPane.setTopAnchor(repliesListView, 100.);
        AnchorPane.setBottomAnchor(repliesListView, 100.);

        AnchorPane.setLeftAnchor(newCommentField, 20.);
        AnchorPane.setRightAnchor(newCommentField, 20.);
        AnchorPane.setBottomAnchor(newCommentField, 20.);

        int size = stackPane.getChildren().size();
        if(size > 0)
            stackPane.getChildren().get(size-1).setVisible(false);
        stackPane.getChildren().add(anchorPane);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
