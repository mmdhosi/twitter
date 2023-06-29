package com.mytwitter.client;


import com.mytwitter.tweet.Tweet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class LikeEventHandler implements EventHandler<ActionEvent> {
    boolean clicked = false;
    Requester requester;
    Tweet tweet;

    public LikeEventHandler(Tweet tweet, Requester requester) {
        this.tweet = tweet;
        this.requester = requester;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        clicked = !clicked;
        Button likesButton = (Button) actionEvent.getSource();

        if(tweet.isLiked()) {
            requester.unlike(tweet.getTweetId());
            tweet.setUnliked();
            tweet.removeLike();
            likesButton.setText("♡" + tweet.getLikeCount());
        }
        else{
            requester.like(tweet.getTweetId());
            tweet.setLiked();
            tweet.addLike();
            likesButton.setText("❤" + tweet.getLikeCount());
        }

    }
}
