package com.mytwitter.tweet;

import com.mytwitter.user.User;

public class Quote extends Tweet{
    private final String type = "Quote";

    private Tweet quotedTweet;

    public Quote(String username, String content, int likeCount, int replyCount, int retweetCount, Tweet quotedTweet) {
        super(username, content, likeCount, replyCount, retweetCount);
        this.quotedTweet = quotedTweet;
    }

    public Tweet getQuotedTweet() {
        return quotedTweet;
    }

    public String getType(){
        return type;
    }
}
