package com.mytwitter.client.controllers;


import com.mytwitter.client.Requester;
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

public class HashtagController implements Initializable{


        private Stage currentStage;
        private Requester requester;

        @FXML
        private ListView<Tweet> cardsListView;

        @FXML
        private TextField searchField;

        @FXML
        private Button searchButton;

        @FXML
        private BorderPane rootPaneV;
        ObservableList<Tweet> items = FXCollections.observableArrayList();;
        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {

            // set button image
            Image image = new Image("file:icons/magnifier.jpg");
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);

            searchButton.setGraphic(imageView);


            cardsListView.setStyle("-fx-control-inner-background: #FFFFFF;");

            cardsListView.setCellFactory(new Callback<ListView<Tweet>, ListCell<Tweet>>() {
                @Override
                public ListCell<Tweet> call(ListView<Tweet> cardModelListView) {
//                return new ListViewCell();
                    return new ListCell<Tweet>() {
                        private Button usernameLabel = new Button();
                        private Label typeLabel = new Label();
                        private Label contentLabel = new Label();
                        private Button likesButton = new Button();
                        private Button repliesButton = new Button();
                        private Button retweetsButton = new Button();
                        private ImageView profileImg = new ImageView();

                        @Override
                        protected void updateItem(Tweet tweet, boolean empty) {
                            super.updateItem(tweet, empty);
                            if (!empty && tweet != null) {
                                //TODO: get profile image

                                profileImg.setImage(new Image("file:profiles/king.jpg"));
                                float radius = 20;
                                profileImg.setFitHeight(radius);
                                profileImg.setFitWidth(radius);
                                Circle clip = new Circle(radius/2,radius/2,radius/2);
                                profileImg.setClip(clip);

                                usernameLabel.setText(tweet.getUserName());
                                usernameLabel.setStyle("-fx-padding: 3");
                                usernameLabel.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        currentStage.hide();


                                        Scene profileScene = null;
                                        FXMLLoader profileLoader = null;
                                        try {
                                            profileLoader = new FXMLLoader(getClass().getResource("/fxml/profile-view.fxml"));
                                            profileScene = new Scene(profileLoader.load());

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Stage profileStage = new Stage();
                                        ProfileViewController controller = profileLoader.getController();
                                        profileStage.setScene(profileScene);
                                        controller.setCurrentStage(profileStage);
                                        profileStage.show();

                                    }
                                });
                                if(tweet instanceof Retweet){
                                    Retweet retweet=(Retweet) tweet;
                                    typeLabel.setText("retweet");
                                    contentLabel.setText(retweet.getRetweetedTweet().getContent());
                                    contentLabel.setStyle("-fx-padding: 3 15 5 15");
                                }else if (tweet instanceof Quote){
                                    Quote quote=(Quote)tweet;
//                                typeLabel.setText("quote");
                                    contentLabel.setText(tweet.getContent()+"\n"+"quote"+"\n"+quote.getQuotedTweet().getContent());
                                    contentLabel.setStyle("-fx-padding: 3 15 5 15");
                                }else {
                                    contentLabel.setText(tweet.getContent());
                                    contentLabel.setStyle("-fx-padding: 3 15 5 15");
                                }

                                likesButton.setText("♡"+tweet.getLikeCount());
                                likesButton.setPrefWidth(40);
                                likesButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        //TODO: in mysql send the liked or not liked timeline
//                                    requester.like(tweet.getTweetId());
                                    }
                                });


                                repliesButton.setText("💬"+tweet.getReplyCount());
                                repliesButton.setPrefWidth(40);

                                retweetsButton.setText("\uD83D\uDD01"+tweet.getRetweetCount());
                                retweetsButton.setPrefWidth(40);


                                HBox hBox = new HBox(likesButton, repliesButton, retweetsButton);
                                hBox.setSpacing(20);
                                setGraphic(new VBox(new HBox(profileImg, usernameLabel),typeLabel, contentLabel, hBox));

                                getStyleClass().add("fx-cell-size: 50px;");
//                            CardController card = new CardController();
//                            card.updateDetails(item.getName(), item.getContent());
//                            setGraphic(card.getRootVBox());
//
//                            setStyle("-fx-border-color: black;");
                            } else {
                                setGraphic(null);
                            }
                        }
                    };
                }
            });
        }

        @FXML
        public void searchAction(ActionEvent event){

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
            cardsListView.setItems(items);
        }
}
