package com.mytwitter.tweet;

import com.mytwitter.user.User;

public class Retweet extends Tweet{
    private final String type = "Retweet";


    private Tweet retweetedTweet;
    public Retweet(String user, int likeCount, int replyCount, int retweetCount, Tweet retweetedTweet) {
        super(user, "", likeCount, replyCount, retweetCount);
        this.retweetedTweet = retweetedTweet;
    }

    public Tweet getRetweetedTweet() {
        return retweetedTweet;
    }

    @Override
    public String toString() {
        return super.toString() + retweetedTweet.getContent();
    }
    public String getType(){
        return type;
    }
}
