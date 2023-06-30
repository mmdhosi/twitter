package com.mytwitter.client;

import com.mytwitter.client.controllers.CommentsViewController;
import com.mytwitter.client.controllers.HashtagController;
import com.mytwitter.client.controllers.ProfileViewController;
import com.mytwitter.client.controllers.QuoteRetweetViewController;
import com.mytwitter.tweet.Quote;
import com.mytwitter.tweet.Reply;
import com.mytwitter.tweet.Retweet;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.ProfileImage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

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
            //TODO: handle tweet image
            setGraphic(createTweet(currentStage, tweet, true, true));
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

    private static int getExpressionEnd(String content, int fromIndex, char space, char endline){
        int wordEnd = content.indexOf(space, fromIndex);
        int lineEnd = content.indexOf(endline, fromIndex);
        if(wordEnd == -1 && lineEnd == -1){
            wordEnd = content.length();
        } else if(lineEnd < wordEnd && lineEnd != -1){
            wordEnd = lineEnd;
        } else if(wordEnd == -1){
            wordEnd = lineEnd;
        }
        return wordEnd;
    }

    private static TextFlow parseContent(Stage currentStage, String content){
        TextFlow textFlow = new TextFlow();
        for (int i = 0; i<content.length(); i++) {
            if(content.charAt(i) == '#'){
                int hashtagEnd = getExpressionEnd(content, i, ' ', '\n');

                Hyperlink hashtag = new Hyperlink(content.substring(i, hashtagEnd));
                hashtag.setOnAction(event -> {
                    new HashtagController(currentStage, hashtag.getText().substring(1));
                });
                textFlow.getChildren().add(hashtag);
                i = hashtagEnd - 1;

            } else if(content.charAt(i) == '@'){
                int mentionEnd = getExpressionEnd(content, i, ' ', '\n');
                Hyperlink mention = new Hyperlink(content.substring(i, mentionEnd));
                mention.setOnAction(event -> {
                    new ProfileViewController(currentStage, mention.getText().substring(1));
                });
                textFlow.getChildren().add(mention);
                i = mentionEnd - 1;

            } else {
                int textEnd = getExpressionEnd(content, i, '#', '@');
                Text text = new Text(content.substring(i, textEnd));
                textFlow.getChildren().add(text);
                i = textEnd -1;
            }
        }
        return textFlow;
    }

    public static VBox createTweet(Stage currentStage, Tweet tweet, boolean placeActions, boolean showSubjectTweet){
        Hyperlink usernameLabel = new Hyperlink();
        Label typeLabel = new Label();
        VBox contentBox = new VBox();
        TextFlow contentText = new TextFlow();
        Button likesButton = new Button();
        Button repliesButton = new Button();
        Button retweetsButton = new Button();
        ImageView profileImg = new ImageView();
        HBox actionsBox;
        Requester requester = Requester.getRequester();

        typeLabel.setTextFill(Color.GRAY);


        setProfile(profileImg, tweet.getUserName());
        setUsername(usernameLabel, tweet, currentStage);



        if (tweet instanceof Retweet retweet) {
            contentBox.getChildren().add(new HBox(profileImg, usernameLabel));
            typeLabel.setText("Retweeted");

            setLikeButton(likesButton, retweet.getRetweetedTweet(), requester);
            setRepliesButton(currentStage, retweet.getRetweetedTweet(), repliesButton, requester);
            setRetweetButton(retweetsButton, retweet.getRetweetedTweet());
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton);
            actionsBox.setSpacing(20);

            contentBox.getChildren().add(typeLabel);
            contentBox.getChildren().add(createTweet(currentStage, retweet.getRetweetedTweet(), false, true));

        } else if (tweet instanceof Quote quote) {
            contentBox.getChildren().add(new HBox(profileImg, usernameLabel));
            typeLabel.setText("Quoted");
            contentText = parseContent(currentStage,tweet.getContent());

            setLikeButton(likesButton, tweet, requester);
            setRepliesButton(currentStage, tweet, repliesButton, requester);
            setRetweetButton(retweetsButton, tweet);
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton);
            actionsBox.setSpacing(20);


            contentBox.getChildren().add(typeLabel);
            contentBox.getChildren().add(contentText);
            contentBox.getChildren().add(createTweet(currentStage, quote.getQuotedTweet(), false, true));


        } else if (tweet instanceof Reply reply) {
            setLikeButton(likesButton, tweet, requester);
            setRepliesButton(currentStage, tweet, repliesButton, requester);
            setRetweetButton(retweetsButton, tweet);
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton);
            actionsBox.setSpacing(20);

            contentBox = createContentBoxReply(new HBox(profileImg, usernameLabel), currentStage, reply, showSubjectTweet);
        } else {
            contentBox.getChildren().add(new HBox(profileImg, usernameLabel));
            setLikeButton(likesButton, tweet, requester);
            setRepliesButton(currentStage, tweet, repliesButton, requester);
            setRetweetButton(retweetsButton, tweet);
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton);
            actionsBox.setSpacing(20);

            contentText = parseContent(currentStage, tweet.getContent());
            contentBox.getChildren().add(contentText);
        }

        if(placeActions)
            return new VBox(contentBox, actionsBox);
        else
            return new VBox(contentBox);

    }

    //TODO: bigger space for bigger numbers
    private static void setRepliesButton(Stage currentStage, Tweet tweet, Button repliesButton, Requester requester) {
        repliesButton.setText("💬" + tweet.getReplyCount());
        repliesButton.setPrefWidth(40);
        repliesButton.setOnAction(event -> {
            new CommentsViewController(currentStage, tweet);
        });
    }

    public static void setRetweetButton(Button retweetsButton, Tweet tweet) {
        retweetsButton.setText("\uD83D\uDD01" + tweet.getRetweetCount());
        retweetsButton.setPrefWidth(40);
        retweetsButton.setOnAction(event -> {
            new QuoteRetweetViewController(tweet);
        });
    }

    public static void setLikeButton(Button likesButton, Tweet tweet, Requester requester) {
        if (tweet.isLiked())
            likesButton.setText("❤" + tweet.getLikeCount());
        else
            likesButton.setText("♡" + tweet.getLikeCount());

        likesButton.setPrefWidth(40);
        likesButton.setOnAction(new LikeEventHandler(tweet, requester));
    }

    public static void setUsername(Hyperlink username, Tweet tweet, Stage currentStage) {
        username.setText(tweet.getUserName());
        username.setStyle("-fx-padding: 3");
        username.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new ProfileViewController(currentStage, tweet.getUserName());
            }
        });
    }

    public static void setProfile(ImageView profileImg, String username) {
        Requester requester = Requester.getRequester();
        String img = requester.getUserAvatar(username);

        profileImg.setImage(ProfileImage.getAvatarImage(img));

        float radius = 20;
        profileImg.setFitHeight(radius);
        profileImg.setFitWidth(radius);
        Circle clip = new Circle(radius / 2, radius / 2, radius / 2);
        profileImg.setClip(clip);
    }

    public static VBox createContentBoxReply(HBox userInfoBox, Stage currentStage, Reply reply, boolean showSubjectTweet){
        VBox contentBox = new VBox();
        TextFlow contentText;

        if(showSubjectTweet)
            contentBox.getChildren().add(createTweet(currentStage, reply.getRepliedTo(), true, true));

        Set<String> replyToUsernames = reply.getRepliedToUsernames();
        StringBuilder usernames = new StringBuilder();
        usernames.append("Replying to ");
        for (String name : replyToUsernames) {
            usernames.append("@").append(name).append(" ");
        }

        contentText = parseContent(currentStage, reply.getContent());
        contentText.setStyle("-fx-padding: 3 15 5 15");

        contentBox.getChildren().add(userInfoBox);
        contentBox.getChildren().add(parseContent(currentStage, usernames.toString()));
        contentBox.getChildren().add(contentText);

        return contentBox;
    }

}
