package com.mytwitter.client;

import com.mytwitter.client.controllers.CommentsViewController;
import com.mytwitter.client.controllers.ProfileController;
import com.mytwitter.tweet.Quote;
import com.mytwitter.tweet.Reply;
import com.mytwitter.tweet.Retweet;
import com.mytwitter.tweet.Tweet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public class TweetCell extends ListCell<Tweet> {

    private Stage currentStage;

    public TweetCell(Stage currentStage, Requester requester) {
        this.currentStage = currentStage;
    }

    @Override
    protected void updateItem(Tweet tweet, boolean empty) {
        super.updateItem(tweet, empty);
        if (!empty && tweet != null) {
            setGraphic(createTweet(currentStage, tweet));
            getStyleClass().add("fx-cell-size: 50px;");
//                            CardController card = new CardController();
//                            card.updateDetails(item.getName(), item.getContent());
//                            setGraphic(card.getRootVBox());
//
            setStyle("-fx-border-color: black;");
        } else {
            setGraphic(null);
        }
    }

    public static VBox createTweet(Stage currentStage, Tweet tweet){
        Button usernameLabel = new Button();
        Label typeLabel = new Label();
        Label contentLabel = new Label();
        Button likesButton = new Button();
        Button repliesButton = new Button();
        Button retweetsButton = new Button();
        ImageView profileImg = new ImageView();

        Requester requester = Requester.getRequester();

        //TODO: get profile image

        setProfile(profileImg, "file:profiles/king.jpg");

        setUsername(usernameLabel, tweet, currentStage);

        if (tweet instanceof Retweet) {
            Retweet retweet = (Retweet) tweet;
            typeLabel.setText("retweet");
            contentLabel.setText(retweet.getRetweetedTweet().getContent());
        } else if (tweet instanceof Quote) {
            Quote quote = (Quote) tweet;
//                                typeLabel.setText("quote");
            //TODO: show the quoted tweet
            contentLabel.setText(tweet.getContent() + "\n" + "quote" + "\n" + quote.getQuotedTweet().getContent());
        } else if (tweet instanceof Reply reply) {
            //TODO: show the replied to tweet
            Set<String> replyToUsernames = reply.getRepliedToUsernames();
            //TODO: hyperlink for each username
            String usernames = "";
            for (String name : replyToUsernames) {
                usernames += name + " ";
            }
            typeLabel.setText("Reply to " + usernames);
            contentLabel.setText(tweet.getContent());
        } else {
            contentLabel.setText(tweet.getContent());
        }

        contentLabel.setStyle("-fx-padding: 3 15 5 15");


        setLikeButton(likesButton, tweet, requester);


        repliesButton.setText("💬" + tweet.getReplyCount());
        repliesButton.setPrefWidth(40);
        repliesButton.setOnAction(event -> {
            new CommentsViewController(currentStage, requester, tweet);
        });

        setRetweetButton(retweetsButton, tweet);


        HBox hBox = new HBox(likesButton, repliesButton, retweetsButton);
        hBox.setSpacing(20);
        return new VBox(new HBox(profileImg, usernameLabel), typeLabel, contentLabel, hBox);
    }

    public static void setRetweetButton(Button retweetsButton, Tweet tweet) {
        retweetsButton.setText("\uD83D\uDD01" + tweet.getRetweetCount());
        retweetsButton.setPrefWidth(40);
    }

    public static void setLikeButton(Button likesButton, Tweet tweet, Requester requester) {
        if (tweet.isLiked())
            likesButton.setText("❤" + tweet.getLikeCount());
        else
            likesButton.setText("♡" + tweet.getLikeCount());

        likesButton.setPrefWidth(40);
        likesButton.setOnAction(new LikeEventHandler(tweet, requester));
    }

    public static void setUsername(Button usernameLabel, Tweet tweet, Stage currentStage) {
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
                ProfileController controller = profileLoader.getController();
                profileStage.setScene(profileScene);
                controller.setCurrentStage(profileStage);
                profileStage.show();

            }
        });
    }

    public static void setProfile(ImageView profileImg, String avatarLocation) {
        profileImg.setImage(new Image(avatarLocation));
        float radius = 20;
        profileImg.setFitHeight(radius);
        profileImg.setFitWidth(radius);
        Circle clip = new Circle(radius / 2, radius / 2, radius / 2);
        profileImg.setClip(clip);
    }
}
