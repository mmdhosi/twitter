package com.mytwitter.client.controllers;

import com.mytwitter.client.LikeEventHandler;
import com.mytwitter.client.Requester;
import com.mytwitter.client.TweetCell;
import com.mytwitter.tweet.Quote;
import com.mytwitter.tweet.Reply;
import com.mytwitter.tweet.Retweet;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.User;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.ImageBase64;
import com.mytwitter.util.ProfileImage;
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
import org.w3c.dom.events.Event;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class HomeController implements Initializable {

    private Stage currentStage;
    private Requester requester = Requester.getRequester();

    @FXML
    private ListView<Tweet> cardsListView;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private BorderPane rootPaneV;
    @FXML
    private Button tweetButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button messagesButton;

    @FXML
    private Button profileButton;

    @FXML
    private ImageView profileImageView;

    static ObservableList<Tweet> items = FXCollections.observableArrayList();


    public HomeController(Stage currentStage) {
        this.currentStage = currentStage;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            loader.setController(this);
            Scene homeScene = new Scene(loader.load());
            currentStage.setScene(homeScene);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        items.setAll(requester.getTimeline());
        cardsListView.setItems(items);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTweetButtonFeatures(60);
        String currentUsername = Requester.getUsername();
        profileImageView.setImage(ProfileImage.getAvatarImage(requester.getUserAvatar(currentUsername)));

        profileButton.setOnAction(event -> new ProfileViewController(currentStage, currentUsername));
        logoutButton.setOnAction(event -> new WelcomeViewController(currentStage));
        messagesButton.setOnAction(event -> new DirectViewController(currentStage));

        setMagnifierButtonFeatures();

        cardsListView.setFocusTraversable(false);
        cardsListView.setOnMouseClicked(mouseEvent -> {
            mouseEvent.consume();
        });

        cardsListView.setStyle("-fx-control-inner-background: #FFFFFF;");

        cardsListView.setCellFactory(new Callback<ListView<Tweet>, ListCell<Tweet>>() {
            @Override
            public ListCell<Tweet> call(ListView<Tweet> cardModelListView) {
//                return new ListViewCell();
                return new TweetCell(currentStage, requester);
            }
        });
    }

    public static void refreshList(){
        items.setAll(Requester.getRequester().getTimeline());
    }

    private void setMagnifierButtonFeatures() {
        // set search button image
        Image magnifierImage = new Image("file:icons/magnifier.png");
        ImageView magnifierImageView = new ImageView(magnifierImage);
        magnifierImageView.setFitHeight(20);
        magnifierImageView.setFitWidth(20);

        searchButton.setGraphic(magnifierImageView);
    }

    private void setTweetButtonFeatures(double size) {

        // set tweet button image
        Image plusImage = new Image("file:icons/plus.png");
        ImageView plusImageView = new ImageView(plusImage);
        plusImageView.setFitWidth(size);
        plusImageView.setFitHeight(size);
        tweetButton.setGraphic(plusImageView);

        Circle circle = new Circle(size/2);
        tweetButton.setShape(circle);

        tweetButton.setMinSize(size, size);
        tweetButton.setMaxSize(size, size);
        tweetButton.setPrefSize(size, size);
    }

    @FXML
    private void searchAction(ActionEvent event){
        String keyword = searchField.getText();
        ArrayList<UserProfile> searchedUsers = requester.search(keyword);
        new SearchListController(currentStage, searchedUsers);
    }

    @FXML
    private void tweetAction(ActionEvent event){
        new TweetViewController(requester);
    }


    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }


}
