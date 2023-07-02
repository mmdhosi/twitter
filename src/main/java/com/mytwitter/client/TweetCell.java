package com.mytwitter.client;

import com.mytwitter.client.controllers.*;
import com.mytwitter.poll.Answer;
import com.mytwitter.poll.Poll;
import com.mytwitter.tweet.*;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.ImageBase64;
import com.mytwitter.util.ProfileImage;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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

            VBox tweetBox = createTweet(currentStage, tweet, true, true);
            if(tweetBox == null)
                return;
            setGraphic(tweetBox);
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

    public static String calculateTweetDate(Date tweetDate) {
        String out;
        Instant instant = tweetDate.toInstant();
        Instant now = Instant.now();
        Duration duration = Duration.between(instant, now);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        if(days==0){
            if(hours==0){
                out=minutes+"m ago";
            }else {
                out=hours+"h ago";
            }
        }else {
            long millis = duration.toMillis();
            Date result = new Date(millis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
            out = dateFormat.format(result);
        }

        return out;
    }

    public static VBox createTweet(Stage currentStage, Tweet tweet, boolean placeActions, boolean showSubjectTweet){
        Hyperlink usernameLabel = new Hyperlink();
        Label typeLabel = new Label();
        VBox contentBox = new VBox();
        TextFlow contentText;
        Button likesButton = new Button();
        Button repliesButton = new Button();
        Button retweetsButton = new Button();
        ImageView profileImg = new ImageView();
        Label timeStamp = new Label();
        HBox actionsBox = null;
        Requester requester = Requester.getRequester();

        typeLabel.setTextFill(Color.GRAY);
        timeStamp.setTextFill(Color.GRAY);

        timeStamp.setText(calculateTweetDate(tweet.getTimestamp()));

        boolean res = setProfile(profileImg, tweet.getUserName());
        if(!res)
            return null;

        setUsername(usernameLabel, tweet, currentStage);


        if (tweet instanceof Retweet retweet) {
            contentBox.getChildren().add(new HBox(profileImg, usernameLabel));
            typeLabel.setText("Retweeted");

            setLikeButton(likesButton, retweet.getRetweetedTweet(), requester);
            setRepliesButton(currentStage, retweet.getRetweetedTweet(), repliesButton, requester);
            setRetweetButton(retweetsButton, retweet.getRetweetedTweet());
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton, timeStamp);
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
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton, timeStamp);
            actionsBox.setSpacing(20);


            contentBox.getChildren().add(typeLabel);
            contentBox.getChildren().add(contentText);
            contentBox.getChildren().add(createTweet(currentStage, quote.getQuotedTweet(), false, true));


        } else if (tweet instanceof Reply reply) {
            setLikeButton(likesButton, tweet, requester);
            setRepliesButton(currentStage, tweet, repliesButton, requester);
            setRetweetButton(retweetsButton, tweet);
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton, timeStamp);
            actionsBox.setSpacing(20);

            contentBox = createContentBoxReply(new HBox(profileImg, usernameLabel), currentStage, reply, showSubjectTweet);
        } else if(tweet instanceof RegularTweet regularTweet) {
            contentBox.getChildren().add(new HBox(profileImg, usernameLabel));
            setLikeButton(likesButton, tweet, requester);
            setRepliesButton(currentStage, tweet, repliesButton, requester);
            setRetweetButton(retweetsButton, tweet);
            actionsBox = new HBox(likesButton, repliesButton, retweetsButton, timeStamp);
            actionsBox.setSpacing(20);

            contentText = parseContent(currentStage, tweet.getContent());
            contentBox.getChildren().add(contentText);

            String base64Img = regularTweet.getImage();
            if (base64Img != null){
                Image tweetImage = new Image(ImageBase64.convertToStream(base64Img));
                ImageView tweetImageView = new ImageView();
                tweetImageView.setPreserveRatio(true);
                tweetImageView.setImage(tweetImage);
                tweetImageView.setFitWidth(200);
                tweetImageView.setFitHeight(80);
                contentBox.getChildren().add(tweetImageView);
            }

            Poll poll = regularTweet.getPoll();
            if(poll != null){
                VBox pollBox;
                if(poll.getAnsweredId() != -1){
                    pollBox = createAnsweredPoll(poll);
                } else {
                    pollBox = createUnansweredPoll(poll);
                }
                contentBox.getChildren().add(pollBox);
            }
        }

        if(placeActions)
            return new VBox(contentBox, actionsBox);
        else
            return new VBox(contentBox);

    }

    private static void setRepliesButton(Stage currentStage, Tweet tweet, Button repliesButton, Requester requester) {
        repliesButton.setText("💬" + tweet.getReplyCount());
        repliesButton.setPrefWidth(40);
        repliesButton.setOnAction(event -> {
            new CommentsViewController(currentStage, tweet);
        });
        repliesButton.setStyle("-fx-padding: 3 2 2 3");

    }

    public static void setRetweetButton(Button retweetsButton, Tweet tweet) {
        retweetsButton.setText("\uD83D\uDD01" + tweet.getRetweetCount());
        retweetsButton.setPrefWidth(40);
        retweetsButton.setOnAction(event -> {
            new QuoteRetweetViewController(tweet);
        });
        retweetsButton.setStyle("-fx-padding: 3 2 2 3");

    }

    public static void setLikeButton(Button likesButton, Tweet tweet, Requester requester) {
        if (tweet.isLiked())
            likesButton.setText("❤" + tweet.getLikeCount());
        else
            likesButton.setText("♡" + tweet.getLikeCount());

        likesButton.setPrefWidth(40);
        likesButton.setStyle("-fx-padding: 3 2 2 3");
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

    public static boolean setProfile(ImageView profileImg, String username) {
        Requester requester = Requester.getRequester();
        UserProfile profile = requester.getProfile(username);
        if(profile == null)
            return false;
        setProfileAvatar(profileImg ,ProfileImage.getAvatarImage(profile.getAvatar()), 15);
        return true;
    }

    public static void setProfileAvatar(ImageView imageView, Image image, float radius){
        imageView.setImage(image);

        imageView.setFitHeight(radius*2);
        imageView.setFitWidth(radius*2);
        Circle clip = new Circle(radius, radius, radius);
        imageView.setClip(clip);
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

    public static VBox createUnansweredPoll(Poll poll){
        // box design
        VBox pollBox = createPollQuestion(poll);

        ToggleGroup answersToggleGroup = new ToggleGroup();
        int i = 1;
        for (Answer answer : poll.getAnswers()) {
            RadioButton answerButton = new RadioButton(""+ i++ +". "+ answer.getAnswer());

            answerButton.setToggleGroup(answersToggleGroup);
            if(i==2)
                answerButton.setSelected(true);

            pollBox.getChildren().add(answerButton);
            VBox.setMargin(answerButton, new Insets(5, 0, 0, 10));
        }
        Button voteButton = new Button();
        voteButton.setText("Vote");
        voteButton.setOnAction(event -> {
            ToggleButton toggleButton = (ToggleButton) answersToggleGroup.getSelectedToggle();
            int index = pollBox.getChildren().indexOf(toggleButton) - 1;
            Requester.getRequester().answerPoll(poll.getAnswers().get(index).getId());
            HomeController.refreshList();
        });

        HBox voteBox = new HBox(voteButton);
        voteBox.setAlignment(Pos.BOTTOM_RIGHT);
        pollBox.getChildren().add(voteBox);
        return pollBox;
    }

    public static VBox createPollQuestion(Poll poll){
        // box design
        VBox pollBox = new VBox();
        pollBox.setMaxWidth(300);
        pollBox.setMaxHeight(120);
        pollBox.setPadding(new Insets(7));
        pollBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Label questionLabel = new Label(poll.getQuestion());
        questionLabel.setPrefWidth(300);
        pollBox.getChildren().add(questionLabel);
        VBox.setMargin(questionLabel, new Insets(0, 0, 5, 0));
        return pollBox;
    }

    public static int sumAllVotes(ArrayList<Answer> answers){
        int sum = 0;
        for(Answer a : answers){
            sum += a.getVotes();
        }
        return sum;
    }

    public static VBox createAnsweredPoll(Poll poll){
        VBox pollBox = createPollQuestion(poll);

        int sumVotes = sumAllVotes(poll.getAnswers());

        DecimalFormat df = new DecimalFormat("#.#");

        int i = 1;

        for (Answer answer : poll.getAnswers()) {

            Label answerLabel = new Label(""+ i++ + ". "+ answer.getAnswer());

            double percent = (double) answer.getVotes()*100 / sumVotes;
            Label votePercent = new Label(df.format(percent)+"%");

            Label voteCount = new Label("(" + answer.getVotes()+" votes)");
            voteCount.setTextFill(Color.GRAY);

            HBox voteBox = new HBox(votePercent, voteCount);
            voteBox.setSpacing(3);

            AnchorPane answerBox = new AnchorPane(answerLabel, voteBox);
            if(poll.getAnsweredId() == answer.getId())
                answerBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));

            answerBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

            AnchorPane.setTopAnchor(answerLabel, 4.0);
            AnchorPane.setBottomAnchor(answerLabel, 4.0);
            AnchorPane.setLeftAnchor(answerLabel, 10.0);

            AnchorPane.setTopAnchor(voteBox, 4.0);
            AnchorPane.setBottomAnchor(voteBox, 4.0);
            AnchorPane.setRightAnchor(voteBox, 10.0);

            pollBox.getChildren().add(answerBox);
            VBox.setMargin(answerBox, new Insets(5, 0, 0, 10));
        }
        return pollBox;
    }

}
