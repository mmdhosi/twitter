package com.mytwitter.tweet;

import com.mytwitter.user.User;

public class Retweet extends Tweet{
    Tweet retweetedTweet;

    public Retweet(User user, int likeCount, int replyCount, int retweetCount, Tweet retweetedTweet) {
        super(user, "", likeCount, replyCount, retweetCount);
        this.retweetedTweet = retweetedTweet;
    }

    @Override
    public String toString() {
        return super.toString() + retweetedTweet.getContent();
    }
}
