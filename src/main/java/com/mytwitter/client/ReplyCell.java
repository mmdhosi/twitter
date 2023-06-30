package com.mytwitter.client;

import com.mytwitter.client.controllers.CommentsViewController;
import com.mytwitter.tweet.Reply;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReplyCell extends ListCell<Reply> {

    private Stage currentStage;
    private Requester requester;
    private StackPane stackPane;

    public ReplyCell(Stage currentStage, Requester requester, StackPane stackPane) {
        this.currentStage = currentStage;
        this.requester = requester;
        this.stackPane = stackPane;
    }

    private Hyperlink usernameLabel = new Hyperlink();
    private Label typeLabel = new Label();
    private Label contentLabel = new Label();
    private Button likesButton = new Button();
    private Button repliesButton = new Button();
    private Button retweetsButton = new Button();
    private ImageView profileImg = new ImageView();

    @Override
    protected void updateItem(Reply reply, boolean empty) {
        super.updateItem(reply, empty);
        if (!empty && reply != null) {
            setStyle("-fx-border-color: black;");

            TweetCell.setProfile(profileImg, reply.getUserName());
            TweetCell.setUsername(usernameLabel, reply, currentStage);

            TweetCell.setLikeButton(likesButton, reply, requester);
            TweetCell.setRetweetButton(retweetsButton, reply);
            HBox actionsBox = new HBox(likesButton, repliesButton, retweetsButton);
            actionsBox.setSpacing(20);

            VBox contentBox = TweetCell.createContentBoxReply(new HBox(profileImg, usernameLabel), currentStage, reply, false);

            repliesButton.setText("💬" + reply.getReplyCount());
            repliesButton.setPrefWidth(40);
            repliesButton.setOnAction(event -> {
                CommentsViewController.createCommentsView(currentStage, reply);
            });

            setGraphic(new VBox(contentBox, actionsBox));

        } else {
            setGraphic(null);
        }
    }
}
