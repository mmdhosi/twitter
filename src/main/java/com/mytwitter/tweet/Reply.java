package com.mytwitter.tweet;

import java.util.ArrayList;

public class Reply extends Tweet {
    private final String type = "Reply";
    Tweet repliedTo;
    ArrayList<String> repliedToUsernames = new ArrayList<>();

    public Reply(String username, String content, int likeCount, int replyCount, int retweetCount, Tweet repliedTo) {
        super(username, content, likeCount, replyCount, retweetCount);
        this.repliedTo = repliedTo;
    }
    //TODO: get replies from a tweet and its retweets
    public void addRepliedToUsername(String username){
        repliedToUsernames.add(username);
    }

    public String getType() {
        return type;
    }
}
